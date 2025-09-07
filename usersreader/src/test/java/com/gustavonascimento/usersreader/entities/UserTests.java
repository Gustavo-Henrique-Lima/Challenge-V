package com.gustavonascimento.usersreader.entities;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

class UserTests {

    @Test
    void userShouldHaveCorrectStructure() {
        User entity = new User();
        entity.setId(1L);
        entity.setName("Alice");
        entity.setEmail("alice@example.com");
        entity.setActive(true);
        LocalDateTime now = LocalDateTime.now();
        entity.setCreatedAt(now);

        Assertions.assertNotNull(entity.getClass());
        Assertions.assertNotNull(entity.getId());
        Assertions.assertEquals(1L, entity.getId());
        Assertions.assertEquals("Alice", entity.getName());
        Assertions.assertEquals("alice@example.com", entity.getEmail());
        Assertions.assertTrue(entity.isActive());
        Assertions.assertEquals(now, entity.getCreatedAt());

        Assertions.assertNotNull(entity.getRoles());
        Assertions.assertTrue(entity.getRoles().isEmpty());
    }

    @Test
    void userAllArgsConstructorShouldSetFieldsCorrectly() {
        LocalDateTime created = LocalDateTime.of(2025, 9, 1, 10, 30, 0);
        User entity = new User(2L, "Bob", "bob@example.com", false, created);

        Assertions.assertEquals(2L, entity.getId());
        Assertions.assertEquals("Bob", entity.getName());
        Assertions.assertEquals("bob@example.com", entity.getEmail());
        Assertions.assertFalse(entity.isActive());
        Assertions.assertEquals(created, entity.getCreatedAt());
        Assertions.assertNotNull(entity.getRoles());
        Assertions.assertTrue(entity.getRoles().isEmpty());
    }

    @Test
    void rolesShouldAcceptAddAndAvoidDuplicatesByRoleEquality() {
        User user = new User();
        Role r1 = new Role(1L, "ROLE_ADMIN");
        Role r2 = new Role(1L, "ROLE_USER");
        Role r3 = new Role(2L, "ROLE_USER");

        user.getRoles().add(r1);
        user.getRoles().add(r2);
        user.getRoles().add(r3);

        Assertions.assertEquals(2, user.getRoles().size());
        Assertions.assertTrue(user.getRoles().contains(r1));
        Assertions.assertTrue(user.getRoles().contains(r3));
    }

    @Test
    void equalsShouldReturnTrueWhenComparingSameObject() {
        User user = new User();
        user.setEmail("unique@example.com");

        Assertions.assertEquals(user, user);
    }

    @Test
    void equalsShouldReturnFalseWhenComparingWithNull() {
        User user = new User();
        user.setEmail("unique@example.com");

        Assertions.assertNotEquals(null, user);
    }

    @Test
    void equalsShouldReturnFalseWhenComparingDifferentClass() {
        User user = new User();
        user.setEmail("unique@example.com");

        String differentClassObject = "Different Class Object";
        Assertions.assertNotEquals(user, differentClassObject);
    }

    @Test
    void equalsShouldReturnFalseWhenEmailsAreDifferent() {
        User u1 = new User();
        u1.setEmail("a@example.com");

        User u2 = new User();
        u2.setEmail("b@example.com");

        Assertions.assertNotEquals(u1, u2);
    }

    @Test
    void equalsShouldReturnTrueWhenEmailsAreEqualEvenIfOtherFieldsDiffer() {
        LocalDateTime t1 = LocalDateTime.of(2024, 1, 1, 12, 0);
        LocalDateTime t2 = LocalDateTime.of(2025, 1, 1, 12, 0);

        User u1 = new User(10L, "Alice", "same@example.com", true, t1);
        User u2 = new User(20L, "Bob",   "same@example.com", false, t2);

        Assertions.assertEquals(u1, u2);
        Assertions.assertEquals(u1.hashCode(), u2.hashCode());
    }

    @Test
    void equalsShouldReturnTrueWhenBothEmailsAreNull_CurrentBehavior() {
        User u1 = new User();
        u1.setEmail(null);
        User u2 = new User();
        u2.setEmail(null);

        Assertions.assertEquals(u1, u2);
        Assertions.assertEquals(u1.hashCode(), u2.hashCode());
    }

    @Test
    void hashCodeShouldChangeWhenEmailChanges() {
        User user = new User();
        user.setEmail("x@example.com");
        int initial = user.hashCode();

        user.setEmail("y@example.com");
        int updated = user.hashCode();

        Assertions.assertNotEquals(initial, updated);
    }

    @Test
    void defaultActiveShouldBeFalseUntilSet() {
        User user = new User();

        Assertions.assertFalse(user.isActive());
    }
}