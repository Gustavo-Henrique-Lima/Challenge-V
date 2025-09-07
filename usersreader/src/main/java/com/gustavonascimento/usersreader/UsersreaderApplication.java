package com.gustavonascimento.usersreader;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gustavonascimento.usersreader.entities.Role;
import com.gustavonascimento.usersreader.entities.User;
import com.gustavonascimento.usersreader.repositories.RoleRepository;
import com.gustavonascimento.usersreader.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

@SpringBootApplication
public class UsersreaderApplication {

	private static final Logger LOG = LoggerFactory.getLogger(UsersreaderApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(UsersreaderApplication.class, args);
	}

	static class MockUser {
		public String name;
		public String email;
		public String role;
		@JsonProperty("is_active") public Boolean isActive;
		@JsonProperty("created_at") public String createdAt;
	}

	@Bean
	CommandLineRunner seedUsers(ObjectMapper mapper,
								UserRepository users,
								RoleRepository roles) {
		return args -> {
			ClassPathResource res = new ClassPathResource("mock-users.json");
			if (!res.exists()) {
				LOG.warn("Arquivo mock-users.json não encontrado, seed não executado.");
				return;
			}

			LOG.info("Iniciando seed de usuários a partir de mock-users.json...");

			try (InputStream is = res.getInputStream()) {
				List<MockUser> rows = mapper.readValue(is, new TypeReference<List<MockUser>>() {});
				LOG.info("{} registros carregados do JSON.", rows.size());

				int inseridos = 0;
				int ignorados = 0;

				for (MockUser r : rows) {
					if (r == null || r.email == null || r.email.isBlank()) {
						LOG.warn("Registro inválido ignorado: {}", r);
						ignorados++;
						continue;
					}

					Optional<User> existing = users.findByEmailIgnoreCase(r.email.trim());
					if (existing.isPresent()) {
						LOG.info("Usuário já existe, ignorado: {}", r.email);
						ignorados++;
						continue;
					}

					try {
						Role role = roles.findByAuthority(r.role)
								.orElseThrow(() -> new IllegalStateException("Role não encontrada: " + r.role));

						User u = new User();
						u.setName(r.name);
						u.setEmail(r.email.trim());
						u.setActive(Boolean.TRUE.equals(r.isActive));
						if (r.createdAt != null && !r.createdAt.isBlank()) {
							Instant instant = Instant.parse(r.createdAt);
							LocalDateTime ldt = LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
							u.setCreatedAt(ldt);
						} else {
							u.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC));
						}
						u.getRoles().add(role);

						users.save(u);
						inseridos++;
						LOG.info("Usuário inserido: {}", u.getEmail());
					} catch (Exception e) {
						LOG.error("Erro ao salvar usuário {}: {}", r.email, e.getMessage(), e);
						ignorados++;
					}
				}

				LOG.info("Seed concluído. Inseridos: {}, Ignorados: {}", inseridos, ignorados);

			} catch (Exception e) {
				LOG.error("Falha ao executar seed de usuários", e);
			}
		};
	}
}