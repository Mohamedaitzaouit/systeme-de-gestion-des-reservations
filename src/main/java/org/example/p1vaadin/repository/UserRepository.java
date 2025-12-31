package org.example.p1vaadin. repository;

import org.example. p1vaadin.domain.User;
import org.example.p1vaadin.domain. enums.Role;
import org. springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa. repository.Query;
import org. springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findByActifTrueAndRole(Role role);

    List<User> findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(String nom, String prenom);

    long countByRole(Role role);

    List<User> findByRole(Role role);

    List<User> findByActif(Boolean actif);
}