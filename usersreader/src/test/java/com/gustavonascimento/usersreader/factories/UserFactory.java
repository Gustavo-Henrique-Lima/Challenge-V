package com.gustavonascimento.usersreader.factories;

import com.gustavonascimento.usersreader.entities.Role;
import com.gustavonascimento.usersreader.entities.User;

import java.time.LocalDateTime;
import java.util.Set;

public class UserFactory {

    public static User createUser(String name, String email, boolean active, Role... roles) {
        User user = new User(null, name, email, active, LocalDateTime.now());
        for (Role role : roles) {
            user.getRoles().add(role);
        }
        return user;
    }
}
