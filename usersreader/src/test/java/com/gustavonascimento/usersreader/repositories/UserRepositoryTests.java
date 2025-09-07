package com.gustavonascimento.usersreader.repositories;

import com.gustavonascimento.usersreader.entities.Role;
import com.gustavonascimento.usersreader.entities.User;
import com.gustavonascimento.usersreader.factories.UserFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTests {

    @Autowired
    private UserRepository repository;

    @Autowired
    private RoleRepository roleRepository;

    private User alice;
    private User bob;
    private User carol;
    private User alex;

    @BeforeEach
    void setUp() {

        Role admin = roleRepository.save(new Role(null, "ROLE_ADMIN"));
        Role operator = roleRepository.save(new Role(null, "ROLE_OPERATOR"));

        alice = repository.save(UserFactory.createUser("Alice", "alice@example.com", true, admin));
        bob   = repository.save(UserFactory.createUser("Bob", "bob@example.com", false, operator));
        carol = repository.save(UserFactory.createUser("Carol", "carol@Example.com", true, admin, operator));
        alex  = repository.save(UserFactory.createUser("Alex Brown", "alex.brown@ifpe.com", true));
    }

    @Test
    void findByEmailIgnoreCaseShouldReturnUserWhenEmailExistsDifferentCase() {
        Optional<User> result = repository.findByEmailIgnoreCase("CAROL@example.COM");

        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals("Carol", result.get().getName());
    }

    @Test
    void findByEmailIgnoreCaseShouldReturnEmptyWhenEmailDoesNotExist() {
        Optional<User> result = repository.findByEmailIgnoreCase("nope@example.com");

        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void searchShouldReturnAllWhenAllFiltersNull() {
        Page<User> page = repository.search(null, null, null, PageRequest.of(0, 10));

        Assertions.assertEquals(4, page.getTotalElements());
    }

    @Test
    void searchShouldFilterByQMatchingNameOrEmail() {
        Page<User> byName = repository.search("ali", null, null, PageRequest.of(0, 10));
        Assertions.assertTrue(byName.getContent().stream().anyMatch(u -> "Alice".equals(u.getName())));

        Page<User> byEmail = repository.search("ifpe", null, null, PageRequest.of(0, 10));
        Assertions.assertTrue(byEmail.getContent().stream().anyMatch(u -> "Alex Brown".equals(u.getName())));
    }

    @Test
    void searchShouldFilterByRoleAuthority() {
        Page<User> admins = repository.search(null, "ROLE_ADMIN", null, PageRequest.of(0, 10));
        Assertions.assertEquals(2, admins.getTotalElements());

        Page<User> operators = repository.search(null, "ROLE_OPERATOR", null, PageRequest.of(0, 10));
        Assertions.assertEquals(2, operators.getTotalElements());
    }

    @Test
    void searchShouldFilterByIsActiveFlag() {
        Page<User> active = repository.search(null, null, true, PageRequest.of(0, 10));
        Page<User> inactive = repository.search(null, null, false, PageRequest.of(0, 10));

        Assertions.assertEquals(3, active.getTotalElements());  // Alice, Carol, Alex
        Assertions.assertEquals(1, inactive.getTotalElements()); // Bob
    }

    @Test
    void searchShouldCombineFilters() {
        Page<User> page = repository.search("al", "ROLE_ADMIN", true, PageRequest.of(0, 10));

        Assertions.assertEquals(1, page.getTotalElements());
        Assertions.assertEquals("Alice", page.getContent().get(0).getName());
    }

    @Test
    void searchShouldRespectPaging() {
        Page<User> page0 = repository.search(null, null, null, PageRequest.of(0, 2));
        Page<User> page1 = repository.search(null, null, null, PageRequest.of(1, 2));

        Assertions.assertEquals(2, page0.getNumberOfElements());
        Assertions.assertEquals(2, page1.getNumberOfElements());
        Assertions.assertEquals(4, page0.getTotalElements());
    }
}
