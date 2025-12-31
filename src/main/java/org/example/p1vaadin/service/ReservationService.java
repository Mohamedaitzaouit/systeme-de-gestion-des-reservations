package org.example.p1vaadin.service;

import org. example.p1vaadin.domain.Event;
import org.example.p1vaadin.domain.Reservation;
import org.example.p1vaadin.domain.User;
import org.example.p1vaadin.domain.enums. EventStatus;
import org.example. p1vaadin.domain.enums.ReservationStatus;
import org.example.p1vaadin.exception.*;
import org.example.p1vaadin.repository.EventRepository;
import org.example.p1vaadin.repository.ReservationRepository;
import org.example. p1vaadin.util.CodeGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time. LocalDateTime;
import java.util. HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final EventRepository eventRepository;
    private final CodeGenerator codeGenerator;

    public ReservationService(ReservationRepository reservationRepository, EventRepository eventRepository, CodeGenerator codeGenerator) {
        this.reservationRepository = reservationRepository;
        this.eventRepository = eventRepository;
        this.codeGenerator = codeGenerator;
    }

    public Reservation create(Long eventId, int nombrePlaces, String commentaire, User currentUser) {
        if (nombrePlaces < 1 || nombrePlaces > 10) {
            throw new BadRequestException("Le nombre de places doit être entre 1 et 10");
        }

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Événement non trouvé"));

        if (event.getStatut() != EventStatus.PUBLIE) {
            throw new BadRequestException("Cet événement n'est pas disponible à la réservation");
        }

        if (event.getDateDebut().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Cet événement a déjà commencé");
        }

        Integer reserved = reservationRepository.sumPlacesReservedForEvent(event);
        int totalReserved = reserved != null ? reserved : 0;
        int availableSeats = event.getCapaciteMax() - totalReserved;

        if (nombrePlaces > availableSeats) {
            throw new BadRequestException("Il ne reste que " + availableSeats + " places disponibles");
        }

        Reservation reservation = Reservation.builder()
                .utilisateur(currentUser)
                .evenement(event)
                .nombrePlaces(nombrePlaces)
                .montantTotal(nombrePlaces * event.getPrixUnitaire())
                .dateReservation(LocalDateTime.now())
                .statut(ReservationStatus.EN_ATTENTE)
                .codeReservation(codeGenerator.generateReservationCode())
                .commentaire(commentaire)
                .build();

        return reservationRepository.save(reservation);
    }

    public Reservation confirm(Long id, User currentUser) {
        Reservation reservation = reservationRepository. findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Réservation non trouvée"));

        if (!reservation.getUtilisateur().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("Vous ne pouvez pas modifier cette réservation");
        }

        if (reservation.getStatut() != ReservationStatus.EN_ATTENTE) {
            throw new BadRequestException("Cette réservation ne peut pas être confirmée");
        }

        reservation.setStatut(ReservationStatus.CONFIRMEE);
        return reservationRepository.save(reservation);
    }

    public Reservation cancel(Long id, User currentUser) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Réservation non trouvée"));

        if (!reservation.getUtilisateur().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("Vous ne pouvez pas annuler cette réservation");
        }

        if (reservation.getStatut() == ReservationStatus.ANNULEE) {
            throw new BadRequestException("Cette réservation est déjà annulée");
        }

        Event event = eventRepository.findById(reservation.getEvenement().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Événement non trouvé"));

        LocalDateTime eventDate = event.getDateDebut();
        if (eventDate.isBefore(LocalDateTime.now().plusHours(48))) {
            throw new BadRequestException("Les réservations ne peuvent être annulées que 48h avant l'événement");
        }

        reservation.setStatut(ReservationStatus.ANNULEE);
        return reservationRepository.save(reservation);
    }

    @Transactional(readOnly = true)
    public List<Reservation> findByUser(User user) {
        return reservationRepository.findByUtilisateurWithDetails(user);
    }

    @Transactional(readOnly = true)
    public List<Reservation> findByEvent(Event event) {
        return reservationRepository.findByEvenementWithDetails(event);
    }

    @Transactional(readOnly = true)
    public List<Reservation> findAll() {
        return reservationRepository.findAllWithDetails();
    }

    public Optional<Reservation> findByCode(String code) {
        return reservationRepository.findByCodeReservation(code);
    }

    public Optional<Reservation> findById(Long id) {
        return reservationRepository.findById(id);
    }

    public Map<String, Object> getReservationSummary(Long reservationId) {
        Reservation res = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Réservation non trouvée"));

        Map<String, Object> summary = new HashMap<>();
        summary.put("code", res.getCodeReservation());
        summary.put("event", res.getEvenement().getTitre());
        summary.put("date", res.getEvenement().getDateDebut());
        summary.put("lieu", res.getEvenement().getLieu() + ", " + res.getEvenement().getVille());
        summary.put("places", res.getNombrePlaces());
        summary.put("montant", res.getMontantTotal());
        summary.put("statut", res.getStatut().getLabel());
        return summary;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getGlobalStats() {
        Map<String, Object> stats = new HashMap<>();
        List<Reservation> all = reservationRepository.findAllWithDetails();

        stats.put("total", all.size());
        stats.put("confirmees", all.stream().filter(r -> r.getStatut() == ReservationStatus.CONFIRMEE).count());
        stats.put("enAttente", all.stream().filter(r -> r.getStatut() == ReservationStatus.EN_ATTENTE).count());
        stats.put("annulees", all.stream().filter(r -> r.getStatut() == ReservationStatus.ANNULEE).count());

        double totalRevenue = all.stream()
                .filter(r -> r.getStatut() != ReservationStatus.ANNULEE)
                .mapToDouble(Reservation::getMontantTotal)
                .sum();
        stats.put("totalRevenue", totalRevenue);

        return stats;
    }

    // ✅ Nouvelles méthodes pour les dashboards

    @Transactional(readOnly = true)
    public long countByUser(User user) {
        return reservationRepository.countByUtilisateur(user);
    }

    @Transactional(readOnly = true)
    public long countConfirmedByUser(User user) {
        return reservationRepository. findByUtilisateurWithDetails(user).stream()
                .filter(r -> r.getStatut() == ReservationStatus.CONFIRMEE)
                .count();
    }

    @Transactional(readOnly = true)
    public long countPendingByUser(User user) {
        return reservationRepository. findByUtilisateurWithDetails(user).stream()
                .filter(r -> r.getStatut() == ReservationStatus.EN_ATTENTE)
                .count();
    }

    @Transactional(readOnly = true)
    public long countByOrganizer(User organizer) {
        List<Event> organizerEvents = eventRepository.findByOrganisateurWithDetails(organizer);
        return organizerEvents.stream()
                .mapToLong(event -> reservationRepository.countByEvenement(event))
                .sum();
    }

    @Transactional(readOnly = true)
    public double calculateRevenueByOrganizer(User organizer) {
        List<Event> organizerEvents = eventRepository.findByOrganisateurWithDetails(organizer);
        return organizerEvents.stream()
                .flatMap(event -> reservationRepository. findByEvenementWithDetails(event).stream())
                .filter(r -> r.getStatut() != ReservationStatus. ANNULEE)
                .mapToDouble(Reservation::getMontantTotal)
                .sum();
    }

    @Transactional(readOnly = true)
    public long countAll() {
        return reservationRepository.count();
    }

    @Transactional(readOnly = true)
    public long countPending() {
        return reservationRepository.findAllWithDetails().stream()
                .filter(r -> r.getStatut() == ReservationStatus. EN_ATTENTE)
                .count();
    }

    @Transactional(readOnly = true)
    public double calculateTotalRevenue() {
        return reservationRepository.findAllWithDetails().stream()
                .filter(r -> r.getStatut() != ReservationStatus.ANNULEE)
                .mapToDouble(Reservation:: getMontantTotal)
                .sum();
    }
}