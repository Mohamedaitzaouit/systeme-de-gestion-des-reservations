package org.example.p1vaadin.views.client;

import com.vaadin.flow.component. button.Button;
import com. vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component. html.*;
import com.vaadin.flow.component.notification. Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com. vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin. flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com. vaadin.flow.router.*;
import jakarta.annotation.security.PermitAll;
import org.example. p1vaadin.domain.Event;
import org.example.p1vaadin.domain.Reservation;
import org.example.p1vaadin.domain.User;
import org.example.p1vaadin.security.UserPrincipal;
import org.example.p1vaadin.service. EventService;
import org.example.p1vaadin.service. ReservationService;
import org.example.p1vaadin. views.MainLayout;
import org.example.p1vaadin.views.publics.EventDetailView;
import org.springframework.security.core.context.SecurityContextHolder;

@Route(value = "reserve", layout = MainLayout.class)
@PageTitle("Réserver | Event Booking")
@PermitAll
public class ReservationFormView extends VerticalLayout implements HasUrlParameter<Long> {

    private final EventService eventService;
    private final ReservationService reservationService;

    private Event event;
    private final IntegerField placesField = new IntegerField("Nombre de places");
    private final Paragraph totalPrice = new Paragraph();
    private final TextArea commentaire = new TextArea("Commentaire (optionnel)");

    public ReservationFormView(EventService eventService, ReservationService reservationService) {
        this.eventService = eventService;
        this.reservationService = reservationService;

        setPadding(true);
        setSpacing(true);
        setMaxWidth("500px");
        getStyle().set("margin", "0 auto");
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, @OptionalParameter Long eventId) {
        System.out.println("=== ReservationFormView ===");
        System.out.println("ID événement:  " + eventId);

        if (eventId == null) {
            showError("Aucun événement sélectionné.  Veuillez choisir un événement à réserver.");
            return;
        }

        try {
            event = eventService. findById(eventId);
            System.out.println("Événement trouvé: " + event.getTitre());
            buildForm();
        } catch (Exception ex) {
            System.out.println("ERREUR: " + ex.getMessage());
            showError("Événement non trouvé (ID: " + eventId + ")");
        }
    }

    private void showError(String message) {
        removeAll();
        add(new H2("❌ Erreur"));
        add(new Paragraph(message));
        Button backBtn = new Button("← Retour aux événements", e -> getUI().ifPresent(ui -> ui. navigate("events")));
        backBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        add(backBtn);
    }

    private void buildForm() {
        removeAll();

        add(new H2("🎫 Réserver:  " + event.getTitre()));

        int availableSeats = eventService. getAvailableSeats(event.getId());
        add(new Paragraph("📍 " + event.getLieu() + ", " + event.getVille()));
        add(new Paragraph("💰 Prix unitaire: " + event.getPrixUnitaire() + " DH"));
        add(new Paragraph("🎟️ Places disponibles: " + availableSeats));

        add(new Hr());

        placesField.setMin(1);
        placesField.setMax(Math.min(10, availableSeats));
        placesField.setValue(1);
        placesField.setStepButtonsVisible(true);
        placesField.setWidthFull();

        placesField.addValueChangeListener(e -> updateTotalPrice());
        updateTotalPrice();

        commentaire.setWidthFull();
        commentaire.setMaxLength(500);

        Button reserveBtn = new Button("✅ Confirmer la réservation", e -> submitReservation());
        reserveBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE);
        reserveBtn.setWidthFull();

        Button cancelBtn = new Button("← Retour à l'événement", e -> {
            getUI().ifPresent(ui -> ui.navigate(EventDetailView.class, event.getId()));
        });
        cancelBtn.setWidthFull();

        add(placesField, totalPrice, commentaire, reserveBtn, cancelBtn);
    }

    private void updateTotalPrice() {
        int places = placesField. getValue() != null ? placesField.getValue() : 1;
        double total = places * event. getPrixUnitaire();
        totalPrice.setText("💵 Montant total: " + total + " DH");
    }

    private void submitReservation() {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            Notification.show("Veuillez vous connecter", 3000, Notification.Position. MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        try {
            Reservation reservation = reservationService.create(
                    event.getId(),
                    placesField.getValue(),
                    commentaire.getValue(),
                    currentUser
            );

            Notification.show("✅ Réservation créée!  Code:  " + reservation.getCodeReservation(), 5000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);

            getUI().ifPresent(ui -> ui. navigate("my-reservations"));
        } catch (Exception ex) {
            Notification.show(ex.getMessage(), 3000, Notification.Position.MIDDLE)
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