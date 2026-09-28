package com.example.wastemanagement.dto;

import com.example.wastemanagement.entity.Role;

public class LoginResponse {

    private Long userId;
    private String username;
    private Role role;
    private Long citizenId;

    public LoginResponse() {
    }

    public LoginResponse(Long userId, String username, Role role, Long citizenId) {
        this.userId = userId;
        this.username = username;
        this.role = role;
        this.citizenId = citizenId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Long getCitizenId() {
        return citizenId;
    }

    public void setCitizenId(Long citizenId) {
        this.citizenId = citizenId;
    }
}
