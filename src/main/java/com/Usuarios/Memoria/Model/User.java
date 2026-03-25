package com.Usuarios.Memoria.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class User {

    private UUID id;
    
    
    @NotBlank(message = "El correo es obligatorio")
    private String email;
    @NotBlank(message = "el usuario es obligatorio")
    private String name;
    @Pattern(regexp = "^(\\+\\d{1,3}\\s)?[\\d\\s]{10,14}$",
            message = "El telefono no tiene un formato valido")
    private String phone;

    @Size(min = 8, message = "la contraseña debe tener al menos 8 caracteres")
    @JsonIgnore
    private String password;

    
    @Pattern(regexp = "^[A-ZÑ&]{4}\\d{6}[A-Z0-9]{3}$",
        message = "El RFC no tiene un formato válido"
    )
    @JsonProperty("tax_id")
    private String taxId;
    
    
    @JsonProperty("created_at")
    private String createdAt;

    private List<Address> addresses;

    public User() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTaxId() {
        return taxId;
    }

    public void setTaxId(String taxId) {
        this.taxId = taxId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public List<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<Address> addresses) {
        this.addresses = addresses;
    }

}
