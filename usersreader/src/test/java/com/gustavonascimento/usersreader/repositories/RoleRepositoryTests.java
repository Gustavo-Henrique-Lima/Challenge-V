package com.gustavonascimento.usersreader.repositories;

import com.gustavonascimento.usersreader.entities.Role;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

@DataJpaTest
@ActiveProfiles("test")
class RoleRepositoryTests {

    @Autowired
    private RoleRepository repository;

    private String existingAuthority;
    private String nonExistingAuthority;

    @BeforeEach
    void setUp() {
        repository.save(new Role(1L, "ROLE_ADMIN"));
        repository.save(new Role(2L, "ROLE_USER"));

        existingAuthority = "ROLE_ADMIN";
        nonExistingAuthority = "ROLE_UNKNOWN";
    }

    @Test
    void findByAuthorityShouldReturnRoleWhenAuthorityExists() {
        Optional<Role> result = repository.findByAuthority(existingAuthority);

        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(existingAuthority, result.get().getAuthority());
    }

    @Test
    void findByAuthorityShouldReturnEmptyWhenAuthorityDoesNotExist() {
        Optional<Role> result = repository.findByAuthority(nonExistingAuthority);

        Assertions.assertTrue(result.isEmpty());
    }
}
