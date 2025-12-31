package org.example.p1vaadin.repository;

import org.example.p1vaadin.domain.Event;
import org.example.p1vaadin.domain.User;
import org.example.p1vaadin.domain.enums.EventCategory;
import org.example.p1vaadin.domain.enums.EventStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    @Query("SELECT e FROM Event e JOIN FETCH e.organisateur WHERE e.id = :id")
    Optional<Event> findByIdWithOrganisateur(@Param("id") Long id);

    @Query("SELECT e FROM Event e JOIN FETCH e.organisateur")
    List<Event> findAllWithOrganisateur();

    @Query("SELECT e FROM Event e JOIN FETCH e.organisateur WHERE e.statut = 'PUBLIE' AND e.dateFin > CURRENT_TIMESTAMP")
    List<Event> findAvailableEventsWithOrganisateur();

    @Query("SELECT e FROM Event e JOIN FETCH e.organisateur WHERE e.statut = 'PUBLIE' ORDER BY SIZE(e.reservations) DESC")
    List<Event> findPopularEventsWithOrganisateur();

    @Query("SELECT e FROM Event e JOIN FETCH e.organisateur WHERE e.organisateur = :organisateur")
    List<Event> findByOrganisateurWithDetails(@Param("organisateur") User organisateur);

    List<Event> findByCategorie(EventCategory categorie);
    List<Event> findByOrganisateurAndStatut(User organisateur, EventStatus statut);
    List<Event> findByOrganisateur(User organisateur);
    List<Event> findByStatut(EventStatus statut);
    long countByCategorie(EventCategory categorie);
}