package org.example.p1vaadin.views.admin;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router. Route;
import jakarta.annotation.security.RolesAllowed;
import org.example.p1vaadin.domain.Event;
import org.example.p1vaadin.domain.User;
import org.example.p1vaadin.domain.enums.EventStatus;
import org.example.p1vaadin.security.UserPrincipal;
import org.example.p1vaadin.service.EventService;
import org.example.p1vaadin.views.MainLayout;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Route(value = "admin/events", layout = MainLayout.class)
@PageTitle("Gestion Événements | Event Booking")
@RolesAllowed("ADMIN")
public class AllEventsManagementView extends VerticalLayout {

    private final EventService eventService;
    private final Grid<Event> grid = new Grid<>(Event.class, false);
    private final DateTimeFormatter formatter = DateTimeFormatter. ofPattern("dd/MM/yyyy HH:mm");

    public AllEventsManagementView(EventService eventService) {
        this.eventService = eventService;

        setSizeFull();
        setPadding(true);

        add(new H2("📅 Tous les événements"));

        createGrid();
        refreshGrid();
    }

    private void createGrid() {
        grid.addColumn(Event::getId).setHeader("ID").setAutoWidth(true);
        grid.addColumn(e -> e.getCategorie().getIcon() + " " + e.getCategorie().getLabel()).setHeader("Catégorie").setAutoWidth(true);
        grid.addColumn(Event::getTitre).setHeader("Titre").setAutoWidth(true);
        grid.addColumn(e -> e.getOrganisateur().getFullName()).setHeader("Organisateur").setAutoWidth(true);
        grid.addColumn(e -> e.getDateDebut().format(formatter)).setHeader("Date").setAutoWidth(true);
        grid.addColumn(Event::getVille).setHeader("Ville").setAutoWidth(true);

        grid.addComponentColumn(e -> {
            Span badge = new Span(e.getStatut().getLabel());
            badge.getElement().getThemeList().add("badge " + e.getStatut().getBadgeVariant());
            return badge;
        }).setHeader("Statut");

        grid.addComponentColumn(event -> {
            HorizontalLayout actions = new HorizontalLayout();

            if (event.getStatut() == EventStatus.BROUILLON) {
                Button publishBtn = new Button("Publier", e -> publishEvent(event));
                publishBtn.addThemeVariants(ButtonVariant. LUMO_SMALL, ButtonVariant.LUMO_SUCCESS);
                actions.add(publishBtn);
            }

            if (event.getStatut() != EventStatus.ANNULE && event.getStatut() != EventStatus.TERMINE) {
                Button cancelBtn = new Button("Annuler", e -> cancelEvent(event));
                cancelBtn.addThemeVariants(ButtonVariant. LUMO_SMALL, ButtonVariant.LUMO_ERROR);
                actions.add(cancelBtn);
            }

            return actions;
        }).setHeader("Actions");

        grid.setSizeFull();
        add(grid);
    }

    private void publishEvent(Event event) {
        try {
            eventService.publish(event. getId(), getCurrentUser());
            Notification.show("Événement publié", 3000, Notification.Position.MIDDLE)
                    . addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            refreshGrid();
        } catch (Exception ex) {
            Notification.show(ex. getMessage(), 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void cancelEvent(Event event) {
        try {
            eventService.cancel(event.getId(), getCurrentUser());
            Notification.show("Événement annulé", 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant. LUMO_SUCCESS);
            refreshGrid();
        } catch (Exception ex) {
            Notification. show(ex.getMessage(), 3000, Notification.Position. MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void refreshGrid() {
        List<Event> events = eventService.findAll();
        grid.setItems(events);
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