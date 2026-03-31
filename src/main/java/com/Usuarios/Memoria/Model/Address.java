package com.Usuarios.Memoria.Model;

public class Address {

    private Integer id;
    private String name;
    private String stree;
    private String countryCode;

    public Address(Integer id, String name, String stree, String countryCode) {
        this.id = id;
        this.name = name;
        this.stree = stree;
        this.countryCode = countryCode;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStree() {
        return stree;
    }

    public void setStree(String stree) {
        this.stree = stree;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

}
