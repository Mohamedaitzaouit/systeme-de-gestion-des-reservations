package org.example.p1vaadin.views.organizer;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com. vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.example.p1vaadin.domain.Event;
import org.example.p1vaadin.domain.User;
import org.example.p1vaadin.domain.enums.EventCategory;
import org.example.p1vaadin.security.UserPrincipal;
import org.example.p1vaadin.service.EventService;
import org.example.p1vaadin.views.MainLayout;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;

@Route(value = "organizer/event/new", layout = MainLayout.class)
@PageTitle("Créer un événement | Event Booking")
@RolesAllowed({"ORGANIZER", "ADMIN"})
public class EventFormView extends VerticalLayout {

    private final EventService eventService;

    private final TextField titre = new TextField("Titre");
    private final TextArea description = new TextArea("Description");
    private final ComboBox<EventCategory> categorie = new ComboBox<>("Catégorie");
    private final DateTimePicker dateDebut = new DateTimePicker("Date de début");
    private final DateTimePicker dateFin = new DateTimePicker("Date de fin");
    private final TextField lieu = new TextField("Lieu");
    private final TextField ville = new TextField("Ville");
    private final IntegerField capaciteMax = new IntegerField("Capacité maximale");
    private final NumberField prixUnitaire = new NumberField("Prix unitaire (DH)");
    private final TextField imageUrl = new TextField("URL de l'image (optionnel)");

    public EventFormView(EventService eventService) {
        this.eventService = eventService;

        setPadding(true);
        setSpacing(true);
        setMaxWidth("600px");

        add(new H2("📝 Créer un nouvel événement"));

        titre.setRequired(true);
        titre.setMinLength(5);
        titre.setMaxLength(100);
        titre.setWidthFull();

        description. setMaxLength(1000);
        description. setWidthFull();
        description.setHeight("150px");

        categorie.setItems(EventCategory.values());
        categorie.setItemLabelGenerator(EventCategory::getLabel);
        categorie.setRequired(true);
        categorie.setWidthFull();

        dateDebut. setMin(LocalDateTime.now());
        dateDebut.setValue(LocalDateTime.now().plusDays(7));
        dateDebut.setWidthFull();

        dateFin.setMin(LocalDateTime.now());
        dateFin.setValue(LocalDateTime.now().plusDays(7).plusHours(2));
        dateFin.setWidthFull();

        lieu.setRequired(true);
        lieu.setWidthFull();

        ville.setRequired(true);
        ville.setWidthFull();

        capaciteMax.setMin(1);
        capaciteMax.setValue(100);
        capaciteMax.setStepButtonsVisible(true);
        capaciteMax.setWidthFull();

        prixUnitaire.setMin(0);
        prixUnitaire.setValue(100.0);
        prixUnitaire.setWidthFull();

        imageUrl.setWidthFull();

        Button saveBtn = new Button("💾 Sauvegarder en brouillon", e -> saveEvent(false));
        saveBtn.addThemeVariants(ButtonVariant. LUMO_PRIMARY);

        Button publishBtn = new Button("🚀 Publier directement", e -> saveEvent(true));
        publishBtn.addThemeVariants(ButtonVariant.LUMO_SUCCESS);

        Button cancelBtn = new Button("← Retour", e -> getUI().ifPresent(ui -> ui.navigate("organizer/events")));

        HorizontalLayout buttons = new HorizontalLayout(saveBtn, publishBtn, cancelBtn);
        buttons.setWidthFull();

        add(titre, description, categorie, dateDebut, dateFin, lieu, ville, capaciteMax, prixUnitaire, imageUrl, buttons);
    }

    private void saveEvent(boolean publish) {
        if (titre.isEmpty() || categorie.isEmpty() || lieu.isEmpty() || ville.isEmpty()) {
            Notification.show("Veuillez remplir tous les champs obligatoires", 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        if (dateFin.getValue().isBefore(dateDebut. getValue())) {
            Notification.show("La date de fin doit être après la date de début", 3000, Notification.Position.MIDDLE)
                    . addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        User currentUser = getCurrentUser();
        if (currentUser == null) {
            Notification.show("Erreur d'authentification", 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant. LUMO_ERROR);
            return;
        }

        try {
            Event event = Event.builder()
                    .titre(titre.getValue())
                    .description(description.getValue())
                    .categorie(categorie.getValue())
                    . dateDebut(dateDebut. getValue())
                    .dateFin(dateFin.getValue())
                    .lieu(lieu. getValue())
                    .ville(ville.getValue())
                    . capaciteMax(capaciteMax. getValue())
                    .prixUnitaire(prixUnitaire.getValue())
                    . imageUrl(imageUrl.getValue())
                    .build();

            Event savedEvent = eventService.create(event, currentUser);

            if (publish) {
                eventService.publish(savedEvent. getId(), currentUser);
                Notification.show("Événement créé et publié !", 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant. LUMO_SUCCESS);
            } else {
                Notification.show("Événement sauvegardé en brouillon", 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant. LUMO_SUCCESS);
            }

            getUI().ifPresent(ui -> ui.navigate("organizer/events"));
        } catch (Exception ex) {
            Notification.show(ex. getMessage(), 3000, Notification.Position.MIDDLE)
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