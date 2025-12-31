package org.example.p1vaadin.views.client;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.example.p1vaadin.domain.User;
import org.example.p1vaadin.security.UserPrincipal;
import org.example.p1vaadin.service.UserService;
import org.example.p1vaadin.views.MainLayout;
import org.springframework.security.core.context.SecurityContextHolder;

@Route(value = "profile", layout = MainLayout.class)
@PageTitle("Mon Profil | Event Booking")
@PermitAll
public class ProfileView extends VerticalLayout {

    private final UserService userService;

    private final TextField nom = new TextField("Nom");
    private final TextField prenom = new TextField("Prénom");
    private final TextField email = new TextField("Email");
    private final TextField telephone = new TextField("Téléphone");

    private final PasswordField oldPassword = new PasswordField("Ancien mot de passe");
    private final PasswordField newPassword = new PasswordField("Nouveau mot de passe");
    private final PasswordField confirmPassword = new PasswordField("Confirmer le nouveau mot de passe");

    public ProfileView(UserService userService) {
        this.userService = userService;

        setPadding(true);
        setSpacing(true);
        setMaxWidth("500px");

        User currentUser = getCurrentUser();
        if (currentUser == null) return;

        add(new H2("👤 Mon Profil"));

        // Profile form
        nom.setValue(currentUser.getNom());
        nom.setWidthFull();

        prenom.setValue(currentUser.getPrenom());
        prenom.setWidthFull();

        email.setValue(currentUser.getEmail());
        email.setReadOnly(true);
        email.setWidthFull();

        telephone.setValue(currentUser.getTelephone() != null ? currentUser.getTelephone() : "");
        telephone.setWidthFull();

        Button updateProfileBtn = new Button("Mettre à jour le profil", e -> updateProfile(currentUser));
        updateProfileBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        updateProfileBtn.setWidthFull();

        add(nom, prenom, email, telephone, updateProfileBtn);

        add(new Hr());
        add(new H2("🔒 Changer le mot de passe"));

        oldPassword.setWidthFull();
        newPassword.setWidthFull();
        newPassword.setMinLength(8);
        confirmPassword.setWidthFull();

        Button changePasswordBtn = new Button("Changer le mot de passe", e -> changePassword(currentUser));
        changePasswordBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        changePasswordBtn.setWidthFull();

        add(oldPassword, newPassword, confirmPassword, changePasswordBtn);
    }

    private void updateProfile(User currentUser) {
        try {
            User payload = User.builder()
                    .nom(nom.getValue())
                    .prenom(prenom.getValue())
                    .telephone(telephone.getValue())
                    .build();

            userService.updateProfile(currentUser.getId(), payload);

            Notification.show("Profil mis à jour !", 3000, Notification. Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } catch (Exception ex) {
            Notification.show(ex.getMessage(), 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void changePassword(User currentUser) {
        if (!newPassword.getValue().equals(confirmPassword.getValue())) {
            Notification.show("Les mots de passe ne correspondent pas", 3000, Notification.Position.MIDDLE)
                    . addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        try {
            userService.changePassword(currentUser.getId(), oldPassword.getValue(), newPassword.getValue());

            oldPassword.clear();
            newPassword.clear();
            confirmPassword. clear();

            Notification.show("Mot de passe changé !", 3000, Notification. Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } catch (Exception ex) {
            Notification. show(ex.getMessage(), 3000, Notification.Position. MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
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