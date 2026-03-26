package com.Usuarios.Memoria.DAO;

import com.Usuarios.Memoria.Model.Address;
import com.Usuarios.Memoria.Model.User;
import com.Usuarios.Memoria.Util.AesUtil;
import com.Usuarios.Memoria.Util.DateUtil;
import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserDAO {

    @Autowired
    private AesUtil aesUtil;

    private final List<User> users = new ArrayList<>();
    
    private User buildUser(String email, String name, String phone,
            String rawPassword, String taxId) throws Exception {

        User user = new User();

        user.setId(UUID.randomUUID());
        user.setEmail(email);
        user.setName(name);
        user.setPhone(phone);
        user.setPassword(aesUtil.encrypt(rawPassword));
        user.setTaxId(taxId);
        user.setCreatedAt(DateUtil.nowMadagascar());
        user.setAddresses(Arrays.asList(
                new Address(1, "avenidaMiraFlores", "Street No.1", "Mexico"),
                new Address(2, "AvMorelos", "Street No.2", "EDOMEX")
        ));

        return user;

    }

    @PostConstruct
    public void init() {
        try {
            
            users.add(buildUser("user1@mail.com", "user1", "+1 55 555 555 55",
                    "7c4a8d09ca3762af61e59520943dc26494f8941b", "AARR990101AAA"));
            users.add(buildUser("user2@mail.com", "user2", "+52 55 666 666 66",
                    "7c4a8d09ca3762af61e59520943dc26494f8941b", "BBRR990202BBB"));
            users.add(buildUser("user3@mail.com", "user3", "+44 20 777 777 77",
                    "7c4a8d09ca3762af61e59520943dc26494f8941b", "CCRR990303CCC"));

        } catch (Exception ex) {
            throw new RuntimeException("Error al iniciar usuarios", ex);
        }

    }
    

    public List<User> findAll() {
        return users;
    }

    public void save(User user) {
        users.add(user);
    }

    public void delete(User user) {
        users.remove(user);
    }

    

}
