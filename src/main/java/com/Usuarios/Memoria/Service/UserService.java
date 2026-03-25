package com.Usuarios.Memoria.Service;

import com.Usuarios.Memoria.DAO.UserDAO;
import com.Usuarios.Memoria.Model.ServiceResult;
import com.Usuarios.Memoria.Model.User;
import com.Usuarios.Memoria.Util.AesUtil;
import com.Usuarios.Memoria.Util.DateUtil;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserDAO userDAO;

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
                case "created_id":
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

    public ServiceResult<User> GetAllFiltered(String filter) {

        ServiceResult<User> result = new ServiceResult<>();

        try {
            String[] parts = filter.split("\\+");
            if (parts.length != 3) {
                result.correct = false;
                result.erroMessage = "Formato invalido. Ejemplo: name+co+user";
                return result;
            }

            String field = parts[0].trim().toLowerCase();
            String operator = parts[1].trim().toLowerCase();
            String value = parts[2].trim().toLowerCase();

            switch (field) {
                case "email":
                case "id":
                case "name":
                case "phone":
                case "tax_id":
                case "created_at":
                    break;
                default:
                    result.correct = false;
                    result.erroMessage = field;
                    return result;
            }

            switch (operator) {
                case "co":
                case "eq":
                case "sw":
                case "ew":

                    break;
                default:
                    result.correct = false;
                    result.erroMessage = operator;
                    return result;
            }

            result.correct = true;
            result.Objects = userDAO.findAll().stream()
                    .filter(user -> {
                        String fielValue;
                        switch (field) {
                            case "email":
                                fielValue = user.getEmail();
                                break;

                            case "id":
                                fielValue = user.getId() != null ? user.getId().toString() : null;
                                break;

                            case "name":
                                fielValue = user.getName();
                                break;

                            case "phone":
                                fielValue = user.getPhone();
                                break;

                            case "tax_id":
                                fielValue = user.getTaxId();
                                break;

                            case "created_at":
                                fielValue = user.getCreatedAt();
                                break;

                            default:
                                fielValue = null;

                        }
                        if (fielValue == null) {
                            return false;
                        }

                        String fielva = fielValue.toLowerCase();

                        switch (operator) {
                            case "co":
                                return fielva.contains(value);

                            case "eq":
                                return fielva.equals(value);

                            case "sw":
                                return fielva.startsWith(value);

                            case "ew":
                                return fielva.endsWith(value);

                            default:
                                return false;
                        }
                    })
                    .collect(Collectors.toList());
            return result;

        } catch (Exception ex) {

            result.correct = false;
            result.erroMessage = ex.getLocalizedMessage();
            result.ex = ex;
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

}
