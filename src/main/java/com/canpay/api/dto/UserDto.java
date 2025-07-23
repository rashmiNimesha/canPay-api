//package com.canpay.api.dto;
//
//import java.util.UUID;
//
//import com.canpay.api.entity.User;
//import com.canpay.api.entity.User.UserRole;
//
//public class UserDto {
//    private UUID id;
//    private String name;
//    private String email;
//    private String nic;
//    private UserRole role;
//
//
//    public UserDto(User user) {
//        this.id = user.getId();
//        this.name = user.getName();
//        this.email = user.getEmail();
//        this.nic = user.getNic();
//        this.role = user.getRole();
//    }
//
//    public UserDto(String name, String email, String nic, UserRole role) {
//        this.name = name;
//        this.email = email;
//        this.nic = nic;
//        this.role = role;
//    }
//
//    public UserDto(String name, String email, String nic) {
//        this.name = name;
//        this.email = email;
//        this.nic = nic;
//    }
//
//    public UserDto(String name, String email) {
//        this.name = name;
//        this.email = email;
//    }
//
//    public UserDto() {
//
//    }
//
//
//    public UUID getId() {
//        return id;
//    }
//
//    public void setId(UUID id) {
//        this.id = id;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public String getEmail() {
//        return email;
//    }
//
//    public void setEmail(String email) {
//        this.email = email;
//    }
//
//    public String getNic() {
//        return nic;
//    }
//
//    public void setNic(String nic) {
//        this.nic = nic;
//    }
//
//    public UserRole getRole() {
//        return role;
//    }
//
//    public void setRole(UserRole role) {
//        this.role = role;
//    }
//}
