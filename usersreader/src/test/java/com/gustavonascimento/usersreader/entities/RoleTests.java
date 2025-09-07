package com.gustavonascimento.usersreader.entities;

import com.gustavonascimento.usersreader.entities.dto.RoleDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class RoleTests {

    @Test
    void roleShouldHaveCorrectStructure() {
        Role entity = new Role();
        entity.setId(1L);
        entity.setAuthority("ROLE_OWNER");

        Assertions.assertNotNull(entity.getClass());
        Assertions.assertNotNull(entity.getId());
        Assertions.assertNotNull(entity.getAuthority());
        Assertions.assertEquals(1L, entity.getId());
        Assertions.assertEquals("ROLE_OWNER", entity.getAuthority());
    }

    @Test
    void roleAllArgsConstructorShouldSetFieldsCorrectly() {
        Role entity = new Role(2L, "ROLE_CHIEF");

        Assertions.assertEquals(2L, entity.getId());
        Assertions.assertEquals("ROLE_CHIEF", entity.getAuthority());
    }

    @Test
    void equalsShouldReturnTrueWhenComparingSameObject() {
        Role role = new Role();
        role.setId(1L);
        role.setAuthority("ROLE_OWNER");

        Assertions.assertEquals(role, role);
    }

    @Test
    void equalsShouldReturnFalseWhenComparingWithNull() {
        Role role = new Role();
        role.setId(1L);
        role.setAuthority("ROLE_OWNER");

        Assertions.assertNotEquals(null, role);
    }

    @Test
    void equalsShouldReturnFalseWhenComparingDifferentClass() {
        Role role = new Role();
        role.setId(1L);
        role.setAuthority("ROLE_OWNER");

        String differentClassObject = "Different Class Object";
        Assertions.assertNotEquals(role, differentClassObject);
    }

    @Test
    void equalsShouldReturnFalseWhenIdsAreDifferent() {
        Role role1 = new Role();
        role1.setId(1L);
        role1.setAuthority("ROLE_CHIEF");

        Role role2 = new Role();
        role2.setId(2L);
        role2.setAuthority("ROLE_CHIEF");

        Assertions.assertNotEquals(role1, role2);
    }

    @Test
    void equalsShouldReturnTrueWhenIdsAreEqualEvenIfAuthorityDiffers() {
        Role role1 = new Role();
        role1.setId(1L);
        role1.setAuthority("ROLE_CHIEF");

        Role role2 = new Role();
        role2.setId(1L);
        role2.setAuthority("ROLE_USER");

        Assertions.assertEquals(role1, role2);
    }

    @Test
    void equalsShouldReturnTrueWhenBothIdsAreNull_CurrentBehavior() {
        Role role1 = new Role();
        role1.setId(null);
        role1.setAuthority("A");

        Role role2 = new Role();
        role2.setId(null);
        role2.setAuthority("B");

        Assertions.assertEquals(role1, role2);
    }

    @Test
    void hashCodeShouldBeEqualWhenIdsAreEqual() {
        Role role1 = new Role(10L, "A");
        Role role2 = new Role(10L, "B");

        Assertions.assertEquals(role1.hashCode(), role2.hashCode());
    }

    @Test
    void hashCodeShouldChangeWhenIdChanges() {
        Role role = new Role(99L, "ADMIN");
        int initial = role.hashCode();

        role.setId(100L);
        int updated = role.hashCode();

        Assertions.assertNotEquals(initial, updated);
    }
}
