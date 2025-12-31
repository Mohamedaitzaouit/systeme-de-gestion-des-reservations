package org.example.p1vaadin.service;

import org.example.p1vaadin.domain.Event;
import org.example.p1vaadin.domain.User;
import org.example.p1vaadin.domain.enums.EventCategory;
import org.example.p1vaadin.domain.enums.EventStatus;
import org. example.p1vaadin.domain.enums.Role;
import org.example.p1vaadin.exception.*;
import org.example.p1vaadin.repository.EventRepository;
import org.example.p1vaadin.repository.ReservationRepository;
import org. springframework.stereotype.Service;
import org.springframework.transaction.annotation. Transactional;

import java. time.LocalDateTime;
import java.util.*;
import java.util.stream. Collectors;

@Service
@Transactional
public class EventService {

    private final EventRepository eventRepository;
    private final ReservationRepository reservationRepository;

    public EventService(EventRepository eventRepository, ReservationRepository reservationRepository) {
        this.eventRepository = eventRepository;
        this. reservationRepository = reservationRepository;
    }

    public Event create(Event event, User currentUser) {
        if (currentUser. getRole() == Role.CLIENT) {
            throw new ForbiddenException("Seuls les organisateurs et admins peuvent créer des événements");
        }
        if (event.getDateFin().isBefore(event.getDateDebut())) {
            throw new BadRequestException("La date de fin doit être après la date de début");
        }
        event.setOrganisateur(currentUser);
        event.setStatut(EventStatus. BROUILLON);
        event.setDateCreation(LocalDateTime.now());
        event.setDateModification(LocalDateTime.now());
        return eventRepository.save(event);
    }

    public Event update(Long id, Event payload, User currentUser) {
        Event event = eventRepository.findByIdWithOrganisateur(id)
                .orElseThrow(() -> new ResourceNotFoundException("Événement non trouvé"));

        if (event.getStatut() == EventStatus.TERMINE) {
            throw new BadRequestException("Un événement terminé ne peut pas être modifié");
        }

        if (! isOwnerOrAdmin(event, currentUser)) {
            throw new ForbiddenException("Vous n'avez pas le droit de modifier cet événement");
        }

        event.setTitre(payload. getTitre());
        event.setDescription(payload.getDescription());
        event.setCategorie(payload.getCategorie());
        event.setDateDebut(payload.getDateDebut());
        event.setDateFin(payload.getDateFin());
        event.setLieu(payload.getLieu());
        event.setVille(payload.getVille());
        event.setCapaciteMax(payload.getCapaciteMax());
        event.setPrixUnitaire(payload.getPrixUnitaire());
        event.setImageUrl(payload.getImageUrl());
        event.setDateModification(LocalDateTime.now());

        return eventRepository.save(event);
    }

    public Event publish(Long id, User currentUser) {
        Event event = eventRepository.findByIdWithOrganisateur(id)
                .orElseThrow(() -> new ResourceNotFoundException("Événement non trouvé"));

        if (!isOwnerOrAdmin(event, currentUser)) {
            throw new ForbiddenException("Vous n'avez pas le droit de publier cet événement");
        }

        if (event.getStatut() != EventStatus.BROUILLON) {
            throw new BadRequestException("Seul un événement en brouillon peut être publié");
        }

        event.setStatut(EventStatus.PUBLIE);
        event.setDateModification(LocalDateTime.now());
        return eventRepository.save(event);
    }

    public Event cancel(Long id, User currentUser) {
        Event event = eventRepository.findByIdWithOrganisateur(id)
                .orElseThrow(() -> new ResourceNotFoundException("Événement non trouvé"));

        if (!isOwnerOrAdmin(event, currentUser)) {
            throw new ForbiddenException("Vous n'avez pas le droit d'annuler cet événement");
        }

        event.setStatut(EventStatus. ANNULE);
        event.setDateModification(LocalDateTime.now());
        return eventRepository.save(event);
    }

    public void delete(Long id, User currentUser) {
        Event event = eventRepository.findByIdWithOrganisateur(id)
                .orElseThrow(() -> new ResourceNotFoundException("Événement non trouvé"));

        if (!isOwnerOrAdmin(event, currentUser)) {
            throw new ForbiddenException("Vous n'avez pas le droit de supprimer cet événement");
        }

        if (! event.getReservations().isEmpty()) {
            throw new BadRequestException("Impossible de supprimer un événement avec des réservations");
        }

        eventRepository. delete(event);
    }

    @Transactional(readOnly = true)
    public Event findById(Long id) {
        return eventRepository.findByIdWithOrganisateur(id)
                .orElseThrow(() -> new ResourceNotFoundException("Événement non trouvé"));
    }

    @Transactional(readOnly = true)
    public List<Event> findAll() {
        return eventRepository. findAllWithOrganisateur();
    }

    @Transactional(readOnly = true)
    public List<Event> findAvailableEvents() {
        return eventRepository.findAvailableEventsWithOrganisateur();
    }

    @Transactional(readOnly = true)
    public List<Event> findByOrganisateur(User organisateur) {
        return eventRepository.findByOrganisateurWithDetails(organisateur);
    }

    @Transactional(readOnly = true)
    public List<Event> findPopularEvents(int limit) {
        return eventRepository.findPopularEventsWithOrganisateur().stream()
                .limit(limit)
                .collect(Collectors. toList());
    }

    @Transactional(readOnly = true)
    public List<Event> search(EventCategory category, String ville, String keyword, Double minPrice, Double maxPrice) {
        return eventRepository.findAvailableEventsWithOrganisateur().stream()
                .filter(e -> category == null || e.getCategorie() == category)
                .filter(e -> ville == null || ville.isBlank() || e.getVille().toLowerCase().contains(ville.toLowerCase()))
                .filter(e -> keyword == null || keyword.isBlank() || e.getTitre().toLowerCase().contains(keyword.toLowerCase()))
                .filter(e -> minPrice == null || e.getPrixUnitaire() >= minPrice)
                .filter(e -> maxPrice == null || e.getPrixUnitaire() <= maxPrice)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public int getAvailableSeats(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Événement non trouvé"));
        Integer reserved = reservationRepository.sumPlacesReservedForEvent(event);
        return event.getCapaciteMax() - (reserved != null ? reserved : 0);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getOrganizerStats(User organizer) {
        List<Event> events = eventRepository.findByOrganisateurWithDetails(organizer);
        Map<String, Object> stats = new HashMap<>();

        stats.put("totalEvents", events.size());
        stats.put("published", events.stream().filter(e -> e.getStatut() == EventStatus.PUBLIE).count());
        stats.put("draft", events.stream().filter(e -> e.getStatut() == EventStatus.BROUILLON).count());
        stats.put("cancelled", events.stream().filter(e -> e.getStatut() == EventStatus.ANNULE).count());

        long totalReservations = events.stream()
                .mapToLong(e -> reservationRepository.countByEvenement(e))
                .sum();
        stats.put("totalReservations", totalReservations);

        double totalRevenue = events. stream()
                .flatMap(e -> e.getReservations().stream())
                .filter(r -> r.getStatut() != org.example.p1vaadin. domain.enums.ReservationStatus.ANNULEE)
                .mapToDouble(r -> r.getMontantTotal())
                .sum();
        stats.put("totalRevenue", totalRevenue);

        return stats;
    }

    @Transactional(readOnly = true)
    public long countByStatus(EventStatus status) {
        return eventRepository.findByStatut(status).size();
    }

    // ✅ Nouvelles méthodes pour les dashboards

    @Transactional(readOnly = true)
    public long countByOrganizer(User organizer) {
        return eventRepository.findByOrganisateurWithDetails(organizer).size();
    }

    @Transactional(readOnly = true)
    public long countPublishedByOrganizer(User organizer) {
        return eventRepository.findByOrganisateurWithDetails(organizer).stream()
                .filter(e -> e.getStatut() == EventStatus.PUBLIE)
                .count();
    }

    @Transactional(readOnly = true)
    public long countAll() {
        return eventRepository. count();
    }

    @Transactional(readOnly = true)
    public long countPublished() {
        return eventRepository.findByStatut(EventStatus. PUBLIE).size();
    }

    private boolean isOwnerOrAdmin(Event event, User user) {
        return user.getRole() == Role.ADMIN || event.getOrganisateur().getId().equals(user.getId());
    }
}