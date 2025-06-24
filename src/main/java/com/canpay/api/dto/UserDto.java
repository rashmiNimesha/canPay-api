package com.canpay.api.dto;

import com.canpay.api.entity.User;

public class UserDto {
    private Long id;
    private String name;
    private String email;
    private String nic;
    private String role;


    public UserDto(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.nic = user.getNic();
        this.role = user.getRole();
    }

    public UserDto(String name, String email, String nic) {
        this.name = name;
        this.email = email;
        this.nic = nic;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNic() {
        return nic;
    }

    public void setNic(String nic) {
        this.nic = nic;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
