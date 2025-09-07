package com.gustavonascimento.usersreader.services;

import com.fasterxml.jackson.core.type.TypeReference;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gustavonascimento.usersreader.entities.Role;
import com.gustavonascimento.usersreader.entities.dto.UserUploadDTO;
import com.gustavonascimento.usersreader.repositories.RoleRepository;
import com.gustavonascimento.usersreader.services.exceptions.ResourceNotFoundException;
import com.gustavonascimento.usersreader.entities.dto.UploadReportDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.gustavonascimento.usersreader.entities.User;
import com.gustavonascimento.usersreader.entities.dto.UserDTO;
import com.gustavonascimento.usersreader.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ObjectMapper mapper;

    public UserService(UserRepository userRepository, RoleRepository roleRepository,
                       ObjectMapper mapper) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.mapper = mapper;
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

    @Transactional
    public UploadReportDTO uploadUsersFromFile(MultipartFile file) {
        UploadReportDTO report = new UploadReportDTO();

        if (file == null || file.isEmpty()) {
            report.errors.add("Arquivo não enviado");
            return report;
        }

        try (InputStream is = file.getInputStream()) {
            List<UserUploadDTO> payload = mapper.readValue(is, new TypeReference<List<UserUploadDTO>>() {});

            if (payload == null || payload.isEmpty()) {
                LOG.info("Arquivo de upload está vazio.");
                return report;
            }

            for (UserUploadDTO r : payload) {
                try {
                    if (r == null || r.email == null || r.email.isBlank()) {
                        report.skipped++;
                        report.errors.add("Erro ao gravar usuário: e-mail vazio");
                        continue;
                    }

                    Optional<User> existing = userRepository.findByEmailIgnoreCase(r.email.trim());
                    if (existing.isPresent()) {
                        report.skipped++;
                        LOG.info("Ignorando: {}, usuário já existe", r.email);
                        continue;
                    }

                    Role role = roleRepository.findByAuthority(r.role)
                            .orElseThrow(() -> new ResourceNotFoundException("Role não encontrada: " + r.role));

                    User u = new User();
                    u.setName(r.name);
                    u.setEmail(r.email.trim());
                    u.setActive(Boolean.TRUE.equals(r.isActive));
                    u.getRoles().add(role);

                    if (r.createdAt != null && !r.createdAt.isBlank()) {
                        Instant instant = Instant.parse(r.createdAt);
                        u.setCreatedAt(LocalDateTime.ofInstant(instant, ZoneOffset.UTC));
                    } else {
                        u.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC));
                    }

                    userRepository.save(u);
                    report.inserted++;
                    LOG.info("Usuário salvo via upload: {}", u.getEmail());

                } catch (Exception e) {
                    report.skipped++;
                    String email = (r != null ? r.email : "null");
                    String msg = "Erro ao processar" + email + ": " + e.getMessage();
                    report.errors.add(msg);
                    LOG.error(msg, e);
                }
            }

            LOG.info("Upload concluído. Inseridos: {}, Ignorados: {}", report.inserted, report.skipped);
            return report;

        } catch (Exception e) {
            String msg = "Falha ao processo o arquivo: " + e.getMessage();
            report.errors.add(msg);
            LOG.error(msg, e);
            return report;
        }
    }

}
