package com.gustavonascimento.usersreader.repositories;

import com.gustavonascimento.usersreader.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("""
      SELECT u FROM User u
       WHERE (:q IS NULL OR
              LOWER(u.name)  LIKE CONCAT('%', LOWER(CAST(:q AS string)), '%') OR
              LOWER(u.email) LIKE CONCAT('%', LOWER(CAST(:q AS string)), '%'))
         AND (:role IS NULL OR EXISTS (
                SELECT 1 FROM u.roles r WHERE r.authority = :role))
         AND (:isActive IS NULL OR u.isActive = :isActive)
    """)
    Page<User> search(@Param("q") String q,
                      @Param("role") String role,
                      @Param("isActive") Boolean isActive,
                      Pageable pageable);

    Optional<User> findByEmailIgnoreCase(String email);
}