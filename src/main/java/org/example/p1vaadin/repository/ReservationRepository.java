package org.example.p1vaadin.repository;

import org.example.p1vaadin.domain.Event;
import org.example.p1vaadin.domain.Reservation;
import org.example.p1vaadin.domain.User;
import org.example.p1vaadin.domain.enums.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query. Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    // Charger les réservations avec l'événement (JOIN FETCH)
    @Query("SELECT r FROM Reservation r JOIN FETCH r.evenement JOIN FETCH r.utilisateur WHERE r.utilisateur = :user")
    List<Reservation> findByUtilisateurWithDetails(@Param("user") User user);

    // Charger les réservations d'un événement avec les détails
    @Query("SELECT r FROM Reservation r JOIN FETCH r.utilisateur JOIN FETCH r.evenement WHERE r.evenement = :event")
    List<Reservation> findByEvenementWithDetails(@Param("event") Event event);

    // Charger toutes les réservations avec détails (pour admin)
    @Query("SELECT r FROM Reservation r JOIN FETCH r.utilisateur JOIN FETCH r. evenement")
    List<Reservation> findAllWithDetails();

    List<Reservation> findByUtilisateur(User utilisateur);

    List<Reservation> findByEvenementAndStatut(Event evenement, ReservationStatus statut);

    List<Reservation> findByEvenement(Event evenement);

    @Query("SELECT COALESCE(SUM(r. nombrePlaces), 0) FROM Reservation r WHERE r.evenement = :event AND r.statut != 'ANNULEE'")
    Integer sumPlacesReservedForEvent(@Param("event") Event event);

    Optional<Reservation> findByCodeReservation(String code);

    List<Reservation> findByDateReservationBetween(LocalDateTime start, LocalDateTime end);

    List<Reservation> findByUtilisateurAndStatut(User utilisateur, ReservationStatus statut);

    @Query("SELECT COALESCE(SUM(r.montantTotal), 0) FROM Reservation r WHERE r.utilisateur = :user AND r.statut = 'CONFIRMEE'")
    Double sumMontantByUser(@Param("user") User user);

    long countByUtilisateur(User utilisateur);

    long countByEvenement(Event evenement);
}