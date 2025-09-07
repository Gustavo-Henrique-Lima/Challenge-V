package com.gustavonascimento.usersreader.services;

import com.gustavonascimento.usersreader.services.exceptions.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.gustavonascimento.usersreader.entities.User;
import com.gustavonascimento.usersreader.entities.dto.UserDTO;
import com.gustavonascimento.usersreader.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public Page<UserDTO> search(String q, String role, Boolean isActive, Pageable pageable){
        LOG.info("Buscando usuários com os parametros: q={}, role={}, isActive={}", q, role, isActive);
        Page<User> entities = userRepository.search(q, role, isActive, pageable);
        return entities.map(UserDTO::new);
    }

    @Transactional
    public UserDTO findById(Long id){
        LOG.info("Buscando usuário com id: {}", id);
        User entity = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));
        return new UserDTO(entity);
    }

}
