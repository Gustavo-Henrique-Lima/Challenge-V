package com.gustavonascimento.usersreader.entities.dto;

import com.gustavonascimento.usersreader.entities.Role;

public class RoleDTO {

    private  String authority;

    public RoleDTO(){
    }

    public RoleDTO(String authority) {
        this.authority = authority;
    }

    public RoleDTO(Role entity){
        this.authority = entity.getAuthority();
    }

    public String getAuthority() {
        return authority;
    }
}
