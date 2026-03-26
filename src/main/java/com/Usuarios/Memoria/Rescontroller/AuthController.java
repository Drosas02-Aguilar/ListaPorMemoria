
package com.Usuarios.Memoria.Rescontroller;

import com.Usuarios.Memoria.Model.ServiceResult;
import com.Usuarios.Memoria.Model.User;
import com.Usuarios.Memoria.Service.UserService;
import com.Usuarios.Memoria.Util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<ServiceResult<Map<String, String>>> Login(
            @RequestBody Map<String, String> body) {

        ServiceResult<Map<String, String>> result = new ServiceResult<>();

        try {
            String taxId    = body.get("tax_id");
            String password = body.get("password");

            if (taxId == null || taxId.isBlank()) {
                result.correct     = false;
                result.status      = 400;
                result.erroMessage = "El campo tax_id es requerido";
                return ResponseEntity.status(result.status).body(result);
            }

            if (password == null || password.isBlank()) {
                result.correct     = false;
                result.status      = 400;
                result.erroMessage = "El campo password es requerido";
                return ResponseEntity.status(result.status).body(result);
            }

            ServiceResult<User> loginResult = userService.Login(taxId, password);

            if (!loginResult.correct) {
                result.correct     = false;
                result.status      = 401;
                result.erroMessage = loginResult.erroMessage;
                return ResponseEntity.status(result.status).body(result);
            }

            String token = jwtUtil.generatedToken(loginResult.object.getTaxId());

            result.correct = true;
            result.status  = 200;
            result.Message = "Login exitoso";
            result.object  = Map.of(
                    "token",  token,
                    "tax_id", loginResult.object.getTaxId(),
                    "name",   loginResult.object.getName()
            );
            return ResponseEntity.status(result.status).body(result);

        } catch (Exception ex) {
            result.correct     = false;
            result.status      = 500;
            result.erroMessage = ex.getLocalizedMessage();
            result.ex          = ex;
            return ResponseEntity.status(result.status).body(result);
        }
    }
}