package org.example.p1vaadin.views.organizer;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.RolesAllowed;
import org.example.p1vaadin.domain.Event;
import org.example.p1vaadin.domain.Reservation;
import org.example.p1vaadin.service.EventService;
import org.example.p1vaadin.service.ReservationService;
import org.example.p1vaadin.views.MainLayout;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Route(value = "organizer/event/:id/reservations", layout = MainLayout.class)
@PageTitle("Réservations de l'événement | Event Booking")
@RolesAllowed({"ORGANIZER", "ADMIN"})
public class EventReservationsView extends VerticalLayout implements BeforeEnterObserver {

    private final EventService eventService;
    private final ReservationService reservationService;
    private final Grid<Reservation> grid = new Grid<>(Reservation.class, false);
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public EventReservationsView(EventService eventService, ReservationService reservationService) {
        this.eventService = eventService;
        this.reservationService = reservationService;

        setSizeFull();
        setPadding(true);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        String idParam = event.getRouteParameters().get("id").orElse("0");
        try {
            Long id = Long.parseLong(idParam);
            Event ev = eventService.findById(id);
            displayReservations(ev);
        } catch (Exception ex) {
            add(new H2("Événement non trouvé"));
            add(new Button("Retour", e -> getUI().ifPresent(ui -> ui.navigate("organizer/events"))));
        }
    }

    private void displayReservations(Event event) {
        removeAll();

        add(new H2("🎫 Réservations:  " + event.getTitre()));

        // Stats
        List<Reservation> reservations = reservationService.findByEvent(event);
        int totalPlaces = reservations.stream()
                .filter(r -> r.getStatut() != org.example.p1vaadin. domain.enums.ReservationStatus.ANNULEE)
                .mapToInt(Reservation::getNombrePlaces)
                .sum();
        double totalRevenue = reservations.stream()
                .filter(r -> r. getStatut() != org.example.p1vaadin.domain. enums.ReservationStatus. ANNULEE)
                .mapToDouble(Reservation::getMontantTotal)
                .sum();

        HorizontalLayout stats = new HorizontalLayout();
        stats.add(createStatCard("🎫", "Réservations", String.valueOf(reservations.size())));
        stats.add(createStatCard("👥", "Places réservées", totalPlaces + " / " + event.getCapaciteMax()));
        stats.add(createStatCard("💰", "Revenus", totalRevenue + " DH"));
        add(stats);

        // Grid
        grid.addColumn(Reservation::getCodeReservation).setHeader("Code").setAutoWidth(true);
        grid.addColumn(r -> r.getUtilisateur().getFullName()).setHeader("Client").setAutoWidth(true);
        grid.addColumn(r -> r.getUtilisateur().getEmail()).setHeader("Email").setAutoWidth(true);
        grid.addColumn(Reservation::getNombrePlaces).setHeader("Places").setAutoWidth(true);
        grid.addColumn(r -> r.getMontantTotal() + " DH").setHeader("Montant").setAutoWidth(true);
        grid.addColumn(r -> r.getDateReservation().format(formatter)).setHeader("Date").setAutoWidth(true);

        grid.addComponentColumn(r -> {
            Span badge = new Span(r.getStatut().getLabel());
            badge.getElement().getThemeList().add("badge " + r.getStatut().getBadgeVariant());
            return badge;
        }).setHeader("Statut");

        grid.setItems(reservations);
        grid.setSizeFull();
        add(grid);

        Button backBtn = new Button("← Retour à mes événements", e -> getUI().ifPresent(ui -> ui. navigate("organizer/events")));
        add(backBtn);
    }

    private VerticalLayout createStatCard(String icon, String label, String value) {
        VerticalLayout card = new VerticalLayout();
        card.addClassNames(LumoUtility.Background.CONTRAST_5, LumoUtility.BorderRadius.MEDIUM, LumoUtility.Padding.MEDIUM);
        card.setAlignItems(Alignment.CENTER);
        card.setWidth("150px");

        card.add(new Span(icon));
        card.add(new H3(value));
        card.add(new Span(label));

        return card;
    }
}