package com.gustavonascimento.usersreader.controllers;

import com.gustavonascimento.usersreader.services.UserService;
import com.gustavonascimento.usersreader.entities.dto.UserDTO;

import com.gustavonascimento.usersreader.utils.UserPage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping(value = "/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service){
        this.service = service;
    }

    @Operation(
            summary = "Search users",
            description = "Returns a paginated list of users with optional filters (`q`, `role`, `is_active`).",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Ok",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserPage.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(value = "{ \"message\": \"Internal server error\" }")
                            )
                    )
            }
    )
    @GetMapping
    public ResponseEntity<Page<UserDTO>> searchUsers(@RequestParam(required = false) String q,
                                                     @RequestParam(required = false) String role,
                                                     @RequestParam(name = "is_active", required = false) Boolean isActive,
                                                     Pageable pageable){
        Page<UserDTO> entities = service.search(q, role, isActive, pageable);
        return ResponseEntity.ok(entities);
    }
}
