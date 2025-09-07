package com.gustavonascimento.usersreader.entities.dto;

import com.gustavonascimento.usersreader.entities.Role;
import com.gustavonascimento.usersreader.entities.User;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

public class UserDTO {
    private Long id;
    private String name;
    private String email;
    private List<RoleDTO> roles = new ArrayList<>();
    private boolean isActive;
    private OffsetDateTime createdAt;

    public UserDTO(){
    }

    public UserDTO(User entity){
        this.id = entity.getId();
        this.name = entity.getName();
        this.email = entity.getEmail();
        for (Role r : entity.getRoles()) {
            roles.add(new RoleDTO(r.getAuthority()));
        }
        this.isActive = entity.isActive();
        this.createdAt = entity.getCreatedAt()
                .atOffset(ZoneOffset.UTC);
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

    public List<RoleDTO> getRoles() {
        return roles;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
