package org.example.p1vaadin.views.client;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.example.p1vaadin.domain.Reservation;
import org.example.p1vaadin.domain.User;
import org.example.p1vaadin.domain.enums.ReservationStatus;
import org.example.p1vaadin.security.UserPrincipal;
import org.example.p1vaadin.service.ReservationService;
import org.example.p1vaadin.views.MainLayout;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Route(value = "my-reservations", layout = MainLayout.class)
@PageTitle("Mes Réservations | Event Booking")
@PermitAll
public class MyReservationsView extends VerticalLayout {

    private final ReservationService reservationService;
    private final Grid<Reservation> grid = new Grid<>(Reservation.class, false);
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public MyReservationsView(ReservationService reservationService) {
        this.reservationService = reservationService;

        setSizeFull();
        setPadding(true);

        add(new H2("🎫 Mes Réservations"));

        createGrid();
        refreshGrid();
    }

    private void createGrid() {
        grid.addColumn(Reservation::getCodeReservation).setHeader("Code").setAutoWidth(true);
        grid.addColumn(r -> r.getEvenement().getTitre()).setHeader("Événement").setAutoWidth(true);
        grid.addColumn(r -> r.getEvenement().getDateDebut().format(formatter)).setHeader("Date").setAutoWidth(true);
        grid.addColumn(Reservation::getNombrePlaces).setHeader("Places").setAutoWidth(true);
        grid.addColumn(r -> r. getMontantTotal() + " DH").setHeader("Montant").setAutoWidth(true);

        grid.addComponentColumn(r -> {
            Span badge = new Span(r.getStatut().getLabel());
            badge.getElement().getThemeList().add("badge " + r.getStatut().getBadgeVariant());
            return badge;
        }).setHeader("Statut");

        grid.addComponentColumn(r -> {
            if (r.getStatut() != ReservationStatus.ANNULEE) {
                Button cancelBtn = new Button("Annuler", e -> cancelReservation(r));
                cancelBtn. addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ERROR);
                return cancelBtn;
            }
            return new Span("-");
        }).setHeader("Actions");

        grid.setSizeFull();
        add(grid);
    }

    private void cancelReservation(Reservation reservation) {
        try {
            User currentUser = getCurrentUser();
            reservationService.cancel(reservation.getId(), currentUser);
            Notification.show("Réservation annulée", 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant. LUMO_SUCCESS);
            refreshGrid();
        } catch (Exception ex) {
            Notification.show(ex.getMessage(), 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant. LUMO_ERROR);
        }
    }

    private void refreshGrid() {
        User currentUser = getCurrentUser();
        if (currentUser != null) {
            List<Reservation> reservations = reservationService.findByUser(currentUser);
            grid.setItems(reservations);
        }
    }

    private User getCurrentUser() {
        try {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal instanceof UserPrincipal up) {
                return up.getUser();
            }
        } catch (Exception ignored) {}
        return null;
    }
}