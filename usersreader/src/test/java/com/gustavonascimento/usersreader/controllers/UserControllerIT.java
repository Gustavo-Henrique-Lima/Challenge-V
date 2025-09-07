package com.gustavonascimento.usersreader.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserControllerIT {

    @Autowired
    private MockMvc mockMvc;

    private Long existingId;
    private Long nonExistingId;

    @BeforeEach
    void setUp() {
        existingId = 1L;
        nonExistingId = 1000L;
    }

    @Test
    void searchUsersShouldReturnPagedUsers() throws Exception {
        ResultActions result = mockMvc.perform(
                get("/users")
                        .param("q", "al")
                        .param("is_active", "true")
                        .param("page", "0")
                        .param("size", "5")
                        .contentType(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").exists());
    }

    @Test
    void findByIdShouldReturnUserWhenIdExists() throws Exception {
        ResultActions result = mockMvc.perform(
                get("/users/{id}", existingId)
                        .contentType(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").exists())
                .andExpect(jsonPath("$.email").exists());
    }

    @Test
    void findByIdShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
        ResultActions result = mockMvc.perform(
                get("/users/{id}", nonExistingId)
                        .contentType(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNotFound());
    }

    @Test
    void uploadUsersFileShouldReturnReport() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "users.json",
                "application/json",
                """
                [
                  {"name":"Alice","email":"alice@example.com","role":"analyst","isActive":true},
                  {"name":"Bob","email":"bob@example.com","role":"viewer","isActive":false}
                ]
                """.getBytes()
        );

        ResultActions result = mockMvc.perform(
                multipart("/users/upload-file")
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.inserted").exists())
                .andExpect(jsonPath("$.skipped").exists())
                .andExpect(jsonPath("$.errors").isArray());
    }
}
