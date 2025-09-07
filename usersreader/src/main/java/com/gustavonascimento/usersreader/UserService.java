package com.gustavonascimento.usersreader;

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

@Service
public class UserService {

    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    @Value("${app.pagination.maxPageSize:50}")
    private int maxPageSize;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public Page<UserDTO> search(String q, String role, boolean isActive, Pageable pageable){
        LOG.info("Buscando usuários com os parametros: {}, {], {}", q, role, isActive);
        Page<User> entities = userRepository.search(q, role, isActive, pageable);
        return entities.map(user -> new UserDTO(user));
    }
}
