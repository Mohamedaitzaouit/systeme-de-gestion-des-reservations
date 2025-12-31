package org.example.p1vaadin.views.admin;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.example.p1vaadin.domain.User;
import org.example.p1vaadin.domain.enums.Role;
import org.example.p1vaadin.service.UserService;
import org.example.p1vaadin.views.MainLayout;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Route(value = "admin/users", layout = MainLayout.class)
@PageTitle("Gestion Utilisateurs | Event Booking")
@RolesAllowed("ADMIN")
public class UserManagementView extends VerticalLayout {

    private final UserService userService;
    private final Grid<User> grid = new Grid<>(User.class, false);
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public UserManagementView(UserService userService) {
        this.userService = userService;

        setSizeFull();
        setPadding(true);

        add(new H2("👥 Gestion des utilisateurs"));

        createGrid();
        refreshGrid();
    }

    private void createGrid() {
        grid.addColumn(User::getId).setHeader("ID").setAutoWidth(true);
        grid.addColumn(User::getFullName).setHeader("Nom").setAutoWidth(true);
        grid.addColumn(User::getEmail).setHeader("Email").setAutoWidth(true);

        grid.addComponentColumn(user -> {
            Span badge = new Span(user.getRole().getLabel());
            badge.getElement().getThemeList().add("badge " + user.getRole().getBadgeVariant());
            return badge;
        }).setHeader("Rôle");

        grid.addColumn(u -> u.getDateInscription().format(formatter)).setHeader("Inscription").setAutoWidth(true);

        grid.addComponentColumn(user -> {
            Span badge = new Span(user.getActif() ? "Actif" : "Inactif");
            badge.getElement().getThemeList().add("badge " + (user.getActif() ? "success" : "error"));
            return badge;
        }).setHeader("Statut");

        grid.addComponentColumn(user -> {
            HorizontalLayout actions = new HorizontalLayout();

            Button toggleBtn = new Button(user.getActif() ? "Désactiver" : "Activer", e -> toggleUserStatus(user));
            toggleBtn.addThemeVariants(ButtonVariant.LUMO_SMALL, user.getActif() ? ButtonVariant. LUMO_ERROR : ButtonVariant.LUMO_SUCCESS);
            actions.add(toggleBtn);

            ComboBox<Role> roleSelect = new ComboBox<>();
            roleSelect.setItems(Role.values());
            roleSelect.setItemLabelGenerator(Role::getLabel);
            roleSelect.setValue(user.getRole());
            roleSelect.setWidth("130px");
            roleSelect.addValueChangeListener(e -> {
                if (e.getValue() != null && e.getValue() != user.getRole()) {
                    changeUserRole(user, e.getValue());
                }
            });
            actions.add(roleSelect);

            return actions;
        }).setHeader("Actions");

        grid.setSizeFull();
        add(grid);
    }

    private void toggleUserStatus(User user) {
        try {
            userService.setActive(user.getId(), !user.getActif());
            Notification.show("Statut mis à jour", 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant. LUMO_SUCCESS);
            refreshGrid();
        } catch (Exception ex) {
            Notification.show(ex.getMessage(), 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant. LUMO_ERROR);
        }
    }

    private void changeUserRole(User user, Role newRole) {
        try {
            userService.changeRole(user.getId(), newRole);
            Notification.show("Rôle mis à jour", 3000, Notification.Position.MIDDLE)
                    . addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            refreshGrid();
        } catch (Exception ex) {
            Notification.show(ex. getMessage(), 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void refreshGrid() {
        List<User> users = userService. findAll();
        grid.setItems(users);
    }
}