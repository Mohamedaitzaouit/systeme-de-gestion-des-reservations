package org.example.p1vaadin.service;

import org. example.p1vaadin.domain.User;
import org.example.p1vaadin.domain.enums.Role;
import org.example.p1vaadin.exception.*;
import org.example.p1vaadin.repository. ReservationRepository;
import org. example.p1vaadin.repository.UserRepository;
import org. springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java. util. HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, ReservationRepository reservationRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.reservationRepository = reservationRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(User user) {
        if (userRepository.existsByEmail(user. getEmail())) {
            throw new ConflictException("Cet email est déjà utilisé");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setDateInscription(LocalDateTime.now());
        user.setActif(true);
        if (user.getRole() == null) {
            user.setRole(Role.CLIENT);
        }
        return userRepository.save(user);
    }

    public Optional<User> authenticate(String email, String rawPassword) {
        return userRepository.findByEmail(email)
                .filter(User::getActif)
                .filter(u -> passwordEncoder.matches(rawPassword, u.getPassword()));
    }

    public User updateProfile(Long userId, User payload) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
        user.setNom(payload.getNom());
        user.setPrenom(payload.getPrenom());
        user.setTelephone(payload.getTelephone());
        return userRepository.save(user);
    }

    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
        if (!passwordEncoder. matches(oldPassword, user.getPassword())) {
            throw new BadRequestException("Ancien mot de passe incorrect");
        }
        user.setPassword(passwordEncoder. encode(newPassword));
        userRepository.save(user);
    }

    public void setActive(Long userId, boolean active) {
        User user = userRepository. findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
        user.setActif(active);
        userRepository.save(user);
    }

    public void changeRole(Long userId, Role newRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
        user.setRole(newRole);
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userRepository. findAll();
    }

    @Transactional(readOnly = true)
    public List<User> findByRole(Role role) {
        return userRepository.findByRole(role);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getUserStats(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
        Map<String, Object> stats = new HashMap<>();
        stats.put("nbReservations", reservationRepository. countByUtilisateur(user));
        stats.put("montantTotal", Optional.ofNullable(reservationRepository.sumMontantByUser(user)).orElse(0.0));
        stats.put("nbEvenements", user.getEvenements().size());
        return stats;
    }

    @Transactional(readOnly = true)
    public long countByRole(Role role) {
        return userRepository.countByRole(role);
    }

    // ✅ Nouvelle méthode pour le dashboard admin
    @Transactional(readOnly = true)
    public long countAll() {
        return userRepository. count();
    }
}