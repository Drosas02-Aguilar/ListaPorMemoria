package com.Usuarios.Memoria.Service;

import com.Usuarios.Memoria.DAO.UserDAO;
import com.Usuarios.Memoria.Model.Address;
import com.Usuarios.Memoria.Model.ServiceResult;
import com.Usuarios.Memoria.Model.User;
import com.Usuarios.Memoria.Util.AesUtil;
import com.Usuarios.Memoria.Util.DateUtil;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private AesUtil aesUtil;

    public ServiceResult<User> GetAllSorted(String sortedBy) {
        ServiceResult<User> result = new ServiceResult<>();

        try {

            List<User> users = userDAO.findAll();

            if (sortedBy == null || sortedBy.isBlank()) {
                result.Objects = users;
                result.correct = true;
                return result;

            }

            Comparator<User> comparator;
            switch (sortedBy.toLowerCase()) {

                case "email":
                    comparator = Comparator.comparing(User::getEmail);
                    break;
                case "id":
                    comparator = Comparator.comparing(user -> user.getId().toString());
                    break;
                case "name":
                    comparator = Comparator.comparing(User::getName);
                    break;
                case "phone":
                    comparator = Comparator.comparing(User::getPhone);
                    break;
                case "tax_id":
                    comparator = Comparator.comparing(User::getTaxId);
                    break;
                case "created_at":
                    comparator = Comparator.comparing(User::getCreatedAt);
                    break;

                default:
                    result.correct = false;
                    result.erroMessage = sortedBy;

                    return result;

            }

            result.correct = true;
            result.Objects = users.stream().sorted(comparator).collect(Collectors.toList());
            return result;
        } catch (Exception ex) {

            result.correct = false;
            result.erroMessage = ex.getLocalizedMessage();
            result.ex = ex;
            return result;

        }

    }

     public ServiceResult<User> GetAllFiltered(String field, String op, String value) {
    ServiceResult<User> result = new ServiceResult<>();
    try {
        String fieldLower = field.trim().toLowerCase();
        String opLower    = op.trim().toLowerCase();
        String valueLower = value.trim().toLowerCase();

        switch (fieldLower) {
            case "email":
            case "id":
            case "name":
            case "phone":
            case "tax_id":
            case "created_at":
                break;
            default:
                result.correct     = false;
                result.erroMessage = "Campo inválido: " + fieldLower +
                    ". Campos permitidos: email, id, name, phone, tax_id, created_at";
                return result;
        }

        switch (opLower) {
            case "co":
            case "eq":
            case "sw":
            case "ew":
                break;
            default:
                result.correct     = false;
                result.erroMessage = "Operador inválido: " + opLower +
                    ". Operadores permitidos: co, eq, sw, ew";
                return result;
        }

        result.correct = true;
        result.Objects = userDAO.findAll().stream()
                .filter(u -> {
                    String fieldValue;
                    switch (fieldLower) {
                        case "email":      fieldValue = u.getEmail(); break;
                        case "id":         fieldValue = u.getId() != null ? u.getId().toString() : null; break;
                        case "name":       fieldValue = u.getName(); break;
                        case "phone":      fieldValue = u.getPhone(); break;
                        case "tax_id":     fieldValue = u.getTaxId(); break;
                        case "created_at": fieldValue = u.getCreatedAt(); break;
                        default:           fieldValue = null;
                    }

                    if (fieldValue == null) return false;
                    String fv = fieldValue.toLowerCase();

                    switch (opLower) {
                        case "co": return fv.contains(valueLower);
                        case "eq": return fv.equals(valueLower);
                        case "sw": return fv.startsWith(valueLower);
                        case "ew": return fv.endsWith(valueLower);
                        default:   return false;
                    }
                })
                .collect(Collectors.toList());
        return result;

    } catch (Exception ex) {
        result.correct     = false;
        result.erroMessage = ex.getLocalizedMessage();
        result.ex          = ex;
        return result;
    }
}

    public ServiceResult<User> CreateUser(User user) {
        ServiceResult<User> result = new ServiceResult<>();

        try {
            boolean taxIdExist = userDAO.findAll().stream()
                    .anyMatch(us -> us.getTaxId().equalsIgnoreCase(user.getTaxId()));

            if (taxIdExist) {
                result.correct = false;
                result.erroMessage = user.getTaxId();
                return result;
            }

            user.setId(UUID.randomUUID());
            user.setCreatedAt(DateUtil.nowMadagascar());
            user.setPassword(aesUtil.encrypt(user.getPassword()));

            if (user.getAddresses() == null) {
                user.setAddresses(new ArrayList<>());
            }

            userDAO.save(user);

            result.correct = true;
            result.object = user;
            return result;

        } catch (Exception ex) {
            result.correct = false;
            result.erroMessage = ex.getLocalizedMessage();
            result.ex = ex;
            return result;
        }
    }

    public ServiceResult<User> updateUser(UUID id, Map<String, Object> fields) {
        ServiceResult<User> result = new ServiceResult<>();
        try {
            User user = userDAO.findAll().stream()
                    .filter(use -> use.getId().equals(id))
                    .findFirst()
                    .orElse(null);

            if (user == null) {
                result.correct = false;
                return result;
            }

            for (Map.Entry<String, Object> entry : fields.entrySet()) {
                String key = entry.getKey().toLowerCase();
                String value = entry.getValue() != null
                        ? entry.getValue().toString() : null;

                switch (key) {
                    case "email":
                        user.setEmail(value);
                        break;
                    case "name":
                        user.setName(value);
                        break;
                    case "phone":
                        user.setPhone(value);
                        break;
                    case "password":
                        user.setPassword(aesUtil.encrypt(value));
                        break;
                    case "tax_id":
                        boolean taxIdExists = userDAO.findAll().stream()
                                .anyMatch(use -> !use.getId().equals(id)
                                && use.getTaxId().equalsIgnoreCase(value));
                        if (taxIdExists) {
                            result.correct = false;
                            result.erroMessage = value;
                            return result;
                        }

                        user.setTaxId(value);
                        break;

                    case "addresses":
                        if (entry.getValue() instanceof List) {
                            List<?> rawList = (List<?>) entry.getValue();
                            List<Address> addresses = new ArrayList<>();

                            for (Object item : rawList) {
                                if (item instanceof Map) {
                                    Map<?, ?> map = (Map<?, ?>) item;

                                    Address address = new Address(Integer.SIZE, value, value, value);

                                    address.setId(map.get("id") != null
                                            ? Integer.valueOf(map.get("id").toString()) : null);
                                    address.setName(map.get("name") != null
                                            ? map.get("name").toString() : null);
                                    address.setStree(map.get("street") != null
                                            ? map.get("street").toString() : null);
                                    address.setCountryCode(map.get("country_code") != null
                                            ? map.get("country_code").toString() : null);
                                    addresses.add(address);
                                }
                            }
                            user.setAddresses(addresses);
                        }
                        break;

                    default:

                        result.correct = false;
                        result.erroMessage = key;
                        return result;
                }
            }

            result.correct = true;
            result.object = user;
            return result;

        } catch (Exception ex) {
            result.correct = false;
            result.erroMessage = ex.getLocalizedMessage();
            result.ex = ex;
            return result;
        }

    }

    public ServiceResult<User> DeleteUser(UUID id) {
        ServiceResult<User> result = new ServiceResult<>();

        try {
            User user = userDAO.findAll().stream()
                    .filter(use -> use.getId().equals(id))
                    .findFirst()
                    .orElse(null);

            if (user == null) {
                result.correct = false;
                return result;
            }

            userDAO.delete(user);
            result.correct = true;
            return result;

        } catch (Exception ex) {
            result.correct = false;
            result.erroMessage = ex.getLocalizedMessage();
            result.ex = ex;
            return result;
        }

    }

    public ServiceResult<User> Login(String taxId, String rawPassword) {
        ServiceResult<User> result = new ServiceResult<>();
        try {
            User user = userDAO.findAll().stream()
                    .filter(use -> use.getTaxId().equals(taxId))
                    .findFirst()
                    .orElse(null);

            if (user == null) {
                result.correct = false;
                result.status = 401;
                result.erroMessage = "Credenciales inválidas";
                return result;
            }

            String decryptedPassword = aesUtil.decryp(user.getPassword());

            if (!decryptedPassword.equals(rawPassword)) {
                result.correct = false;
                result.status = 401;
                result.erroMessage = "Credenciales inválidas";
                return result;
            }

            result.correct = true;
            result.status = 200;
            result.object = user;
            return result;

        } catch (Exception ex) {

            result.correct = false;
            result.erroMessage = ex.getLocalizedMessage();
            result.ex = ex;
            return result;
        }

    }

}
