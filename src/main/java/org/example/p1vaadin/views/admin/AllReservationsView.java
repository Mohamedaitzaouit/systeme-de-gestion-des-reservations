package org.example.p1vaadin.views.admin;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.example.p1vaadin.domain.Reservation;
import org.example.p1vaadin.service.ReservationService;
import org.example.p1vaadin.views.MainLayout;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Route(value = "admin/reservations", layout = MainLayout.class)
@PageTitle("Toutes les réservations | Event Booking")
@RolesAllowed("ADMIN")
public class AllReservationsView extends VerticalLayout {

    private final ReservationService reservationService;
    private final Grid<Reservation> grid = new Grid<>(Reservation.class, false);
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public AllReservationsView(ReservationService reservationService) {
        this.reservationService = reservationService;

        setSizeFull();
        setPadding(true);

        add(new H2("🎫 Toutes les réservations"));

        createGrid();
        refreshGrid();
    }

    private void createGrid() {
        grid.addColumn(Reservation::getId).setHeader("ID").setAutoWidth(true);
        grid.addColumn(Reservation::getCodeReservation).setHeader("Code").setAutoWidth(true);
        grid.addColumn(r -> r.getUtilisateur().getFullName()).setHeader("Client").setAutoWidth(true);
        grid.addColumn(r -> r.getEvenement().getTitre()).setHeader("Événement").setAutoWidth(true);
        grid.addColumn(Reservation::getNombrePlaces).setHeader("Places").setAutoWidth(true);
        grid.addColumn(r -> r.getMontantTotal() + " DH").setHeader("Montant").setAutoWidth(true);
        grid.addColumn(r -> r.getDateReservation().format(formatter)).setHeader("Date").setAutoWidth(true);

        grid.addComponentColumn(r -> {
            Span badge = new Span(r.getStatut().getLabel());
            badge.getElement().getThemeList().add("badge " + r.getStatut().getBadgeVariant());
            return badge;
        }).setHeader("Statut");

        grid.setSizeFull();
        add(grid);
    }

    private void refreshGrid() {
        List<Reservation> reservations = reservationService.findAll();
        grid.setItems(reservations);
    }
}