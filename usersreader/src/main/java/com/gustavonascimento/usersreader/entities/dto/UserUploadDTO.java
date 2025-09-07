package com.gustavonascimento.usersreader.entities.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserUploadDTO {
    public String name;
    public String email;
    public String role;

    @JsonProperty("is_active")
    public Boolean isActive;

    @JsonProperty("created_at")
    public String createdAt;

    public UserUploadDTO(){

    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public Boolean getActive() {
        return isActive;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}
