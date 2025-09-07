package com.gustavonascimento.usersreader;

import com.gustavonascimento.usersreader.entities.dto.UserDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service){
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<Page<UserDTO>> searchUsers(@RequestParam(required = false) String q,
                                                     @RequestParam(required = false) String role,
                                                     @RequestParam(required = false) boolean is_active,
                                                     Pageable pageable){
        Page<UserDTO> entities = service.search(q, role, is_active, pageable);
        return ResponseEntity.ok(entities);
    }
}
