package com.Usuarios.Memoria.ResController;

import com.Usuarios.Memoria.Model.ServiceResult;
import com.Usuarios.Memoria.Model.User;
import com.Usuarios.Memoria.Service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<ServiceResult<User>> GetUsers(
            @RequestParam(required = false) String sortedBy,
            @RequestParam(required = false) String field,
            @RequestParam(required = false) String op,
            @RequestParam(required = false) String value) {

        ServiceResult<User> result = new ServiceResult<>();

        try {
            if (field != null && !field.isBlank()) {

                if (op == null || op.isBlank()) {
                    result.correct = false;
                    result.status = 400;
                    result.erroMessage = "El parámetro op es requerido cuando se usa field";
                    return ResponseEntity.status(result.status).body(result);
                }

                if (value == null || value.isBlank()) {
                    result.correct = false;
                    result.status = 400;
                    result.erroMessage = "El parámetro value es requerido cuando se usa field";
                    return ResponseEntity.status(result.status).body(result);
                }

                result = userService.GetAllFiltered(field, op, value);

            } else {
                result = userService.GetAllSorted(sortedBy);
            }

            if (!result.correct) {
                result.status = 400;
                return ResponseEntity.status(result.status).body(result);
            }

            result.status = 200;
            result.Message = "Usuarios obtenidos exitosamente";
            return ResponseEntity.status(result.status).body(result);

        } catch (Exception e) {
            result.correct = false;
            result.status = 500;
            result.erroMessage = "Error interno del servidor";
            result.ex = e;
            return ResponseEntity.status(result.status).body(result);
        }
    }

    @PostMapping
    public ResponseEntity<ServiceResult<User>> CreateUser(
            @Valid @RequestBody User user,
            BindingResult bindingResult) {

        ServiceResult<User> result = new ServiceResult<>();

        try {
            if (bindingResult.hasErrors()) {
                result.correct = false;
                result.status = 404;
                result.erroMessage = bindingResult.getFieldErrors().stream()
                        .map(e -> e.getField() + ": " + e.getDefaultMessage())
                        .collect(Collectors.joining(", "));
                return ResponseEntity.status(result.status).body(result);
            }

            result = userService.CreateUser(user);

            result.status = 201;
            result.Message = "Usuario creado exitosamente";
            return ResponseEntity.status(result.status).body(result);

        } catch (Exception ex) {
            result.correct = false;
            result.status = 500;
            result.erroMessage = "Error interno del servidor";
            result.ex = ex;
            return ResponseEntity.status(result.status).body(result);
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ServiceResult<User>> UpdateUser(
            @PathVariable UUID id,
            @RequestBody Map<String, Object> fields) {

        ServiceResult<User> result = new ServiceResult<>();

        try {
            if (fields == null || fields.isEmpty()) {
                result.correct = false;
                result.status = 400;
                result.erroMessage = "El body no puede estar vacío";
                return ResponseEntity.status(result.status).body(result);
            }

            result = userService.updateUser(id, fields);

            if (!result.correct) {
                result.erroMessage = result.erroMessage;
                return ResponseEntity.status(result.status).body(result);
            }

            result.status = 200;
            result.Message = "Usuario actualizado exitosamente";
            return ResponseEntity.status(result.status).body(result);

        } catch (Exception ex) {
            result.correct = false;
            result.status = 500;
            result.erroMessage = "Error interno del servidor";
            result.ex = ex;
            return ResponseEntity.status(result.status).body(result);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ServiceResult<User>> DeleteUser(@PathVariable UUID id) {

        ServiceResult<User> result = new ServiceResult<>();

        try {
            result = userService.DeleteUser(id);

            if (!result.correct) {
                result.status = 404;
                result.erroMessage = result.erroMessage;
                return ResponseEntity.status(result.status).body(result);
            }

            result.status = 200;
            result.Message = "Usuario eliminado exitosamente";
            return ResponseEntity.status(result.status).body(result);

        } catch (Exception ex) {
            result.correct = false;
            result.status = 500;
            result.erroMessage = "Error interno del servidor";
            result.ex = ex;
            return ResponseEntity.status(result.status).body(result);
        }
    }
}
