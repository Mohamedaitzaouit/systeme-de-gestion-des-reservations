package org.example.p1vaadin.views.publics;

import com.vaadin.flow. component.button.Button;
import com.vaadin.flow.component.button. ButtonVariant;
import com. vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon. VaadinIcon;
import com.vaadin.flow.component. orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server. auth.AnonymousAllowed;
import org.example.p1vaadin.domain.Event;
import org.example.p1vaadin.domain.enums.EventStatus;
import org.example.p1vaadin. service.EventService;
import org. example.p1vaadin.views.MainLayout;
import org. example.p1vaadin.views.client.ReservationFormView;

import java.time.format.DateTimeFormatter;
import java.util. Locale;

@Route(value = "event", layout = MainLayout.class)
@PageTitle("Détail Événement | Event Booking")
@AnonymousAllowed
public class EventDetailView extends VerticalLayout implements HasUrlParameter<Long> {

    private final EventService eventService;
    private final DateTimeFormatter formatter = DateTimeFormatter. ofPattern("EEEE dd MMMM yyyy 'à' HH:mm", Locale.FRENCH);

    private static final String FONT_FAMILY = "-apple-system, BlinkMacSystemFont, 'Segoe UI', 'Inter', sans-serif";

    public EventDetailView(EventService eventService) {
        this.eventService = eventService;

        setSizeFull();
        setPadding(false);
        setSpacing(false);
        getStyle()
                .set("background", "var(--bg-gradient)")
                .set("font-family", FONT_FAMILY);
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, @OptionalParameter Long id) {
        if (id == null) {
            showError("Aucun ID d'événement fourni");
            return;
        }

        try {
            Event event = eventService.findById(id);
            displayEvent(event);
        } catch (Exception ex) {
            showError("Événement non trouvé (ID: " + id + ")");
        }
    }

    private void showError(String message) {
        removeAll();

        Div container = new Div();
        container. getStyle()
                .set("max-width", "600px")
                .set("margin", "80px auto")
                .set("padding", "48px")
                .set("text-align", "center")
                .set("background", "var(--card-bg)")
                .set("border", "1px solid var(--border-color)")
                .set("border-radius", "16px")
                .set("box-shadow", "var(--shadow-md)");

        Span icon = new Span("❌");
        icon.getStyle()
                .set("font-size", "4rem")
                .set("display", "block")
                .set("margin-bottom", "16px");

        H2 title = new H2("Événement non trouvé");
        title.getStyle()
                .set("color", "var(--text-primary)")
                .set("margin", "0 0 12px 0");

        Paragraph desc = new Paragraph(message);
        desc.getStyle()
                .set("color", "var(--text-secondary)")
                .set("margin", "0 0 24px 0");

        Button backBtn = new Button("Retour aux événements");
        backBtn.setIcon(new Icon(VaadinIcon.ARROW_LEFT));
        backBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        backBtn.getStyle()
                .set("background", "var(--primary-color)")
                .set("border-radius", "10px")
                .set("font-weight", "600");
        backBtn.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("events")));

        container.add(icon, title, desc, backBtn);
        add(container);
    }

    private void displayEvent(Event event) {
        removeAll();

        Div container = new Div();
        container. getStyle()
                .set("max-width", "900px")
                .set("margin", "0 auto")
                .set("padding", "40px 24px");

        // Back button
        Button backBtn = new Button("Retour à la liste");
        backBtn.setIcon(new Icon(VaadinIcon. ARROW_LEFT));
        backBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        backBtn.getStyle()
                .set("margin-bottom", "24px")
                .set("font-weight", "600")
                .set("color", "var(--text-secondary)");
        backBtn.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("events")));

        // Header card
        Div headerCard = createHeaderCard(event);

        // Info card
        Div infoCard = createInfoCard(event);

        // Action card
        Div actionCard = createActionCard(event);

        container. add(backBtn, headerCard, infoCard, actionCard);
        add(container);
    }

    private Div createHeaderCard(Event event) {
        Div card = new Div();
        card.getStyle()
                .set("background", "var(--card-bg)")
                .set("border", "1px solid var(--border-color)")
                .set("border-radius", "16px")
                .set("padding", "32px")
                .set("margin-bottom", "24px")
                .set("box-shadow", "var(--shadow-sm)");

        // Category badge
        Span categoryBadge = new Span(event. getCategorie().getIcon() + " " + event.getCategorie().getLabel());
        categoryBadge.getStyle()
                .set("display", "inline-block")
                .set("background", "var(--primary-light)")
                .set("color", "var(--primary-color)")
                .set("padding", "6px 12px")
                .set("border-radius", "8px")
                .set("font-size", "0.8125rem")
                .set("font-weight", "700")
                .set("text-transform", "uppercase")
                .set("margin-bottom", "16px");

        // Title
        H1 title = new H1(event.getTitre());
        title.getStyle()
                .set("font-size", "2.25rem")
                .set("font-weight", "800")
                .set("color", "var(--text-primary)")
                .set("margin", "0 0 12px 0")
                .set("letter-spacing", "-0.5px")
                .set("line-height", "1.2");

        // Status badge
        Span statusBadge = new Span(event. getStatut().getLabel());
        String statusColor = event.getStatut() == EventStatus.PUBLIE ?  "var(--success-color)" : "var(--text-secondary)";
        String statusBg = event.getStatut() == EventStatus.PUBLIE ? "var(--success-bg)" : "var(--bg-secondary)";
        statusBadge.getStyle()
                .set("display", "inline-block")
                .set("background", statusBg)
                .set("color", statusColor)
                .set("padding", "6px 12px")
                .set("border-radius", "8px")
                .set("font-size", "0.8125rem")
                .set("font-weight", "700")
                .set("margin-bottom", "16px");

        // Description
        Paragraph description = new Paragraph(
                event.getDescription() != null && !event.getDescription().isBlank()
                        ? event.getDescription()
                        : "Aucune description disponible."
        );
        description.getStyle()
                .set("font-size", "1.0625rem")
                .set("color", "var(--text-secondary)")
                .set("line-height", "1.7")
                .set("margin", "0");

        card.add(categoryBadge, title, statusBadge, description);
        return card;
    }

    private Div createInfoCard(Event event) {
        Div card = new Div();
        card.getStyle()
                .set("background", "var(--card-bg)")
                .set("border", "1px solid var(--border-color)")
                .set("border-radius", "16px")
                .set("padding", "32px")
                .set("margin-bottom", "24px")
                .set("box-shadow", "var(--shadow-sm)");

        H3 sectionTitle = new H3("Informations détaillées");
        sectionTitle.getStyle()
                .set("font-size", "1.25rem")
                .set("font-weight", "700")
                .set("color", "var(--text-primary)")
                .set("margin", "0 0 24px 0");

        VerticalLayout infoList = new VerticalLayout();
        infoList.setPadding(false);
        infoList.setSpacing(false);
        infoList.getStyle().set("gap", "16px");

        infoList.add(createInfoRow(VaadinIcon.CALENDAR, "Date de début", event.getDateDebut().format(formatter)));
        infoList.add(createInfoRow(VaadinIcon.CALENDAR, "Date de fin", event. getDateFin().format(formatter)));
        infoList.add(createInfoRow(VaadinIcon.MAP_MARKER, "Lieu", event.getLieu()));
        infoList.add(createInfoRow(VaadinIcon.LOCATION_ARROW, "Ville", event.getVille()));
        infoList.add(createInfoRow(VaadinIcon.DOLLAR, "Prix", event.getPrixUnitaire() + " DH"));

        int availableSeats = eventService. getAvailableSeats(event.getId());
        String placesText = availableSeats + " / " + event.getCapaciteMax() + " places disponibles";
        String placesColor = availableSeats == 0 ? "var(--error-color)" : availableSeats < 10 ? "var(--warning-color)" : "var(--success-color)";
        infoList.add(createInfoRow(VaadinIcon.TICKET, "Places", placesText, placesColor));

        infoList.add(createInfoRow(VaadinIcon.USER, "Organisateur", event.getOrganisateur().getFullName()));

        card.add(sectionTitle, infoList);
        return card;
    }

    private Div createInfoRow(VaadinIcon iconType, String label, String value) {
        return createInfoRow(iconType, label, value, "var(--text-primary)");
    }

    private Div createInfoRow(VaadinIcon iconType, String label, String value, String valueColor) {
        Div row = new Div();
        row.getStyle()
                .set("display", "grid")
                .set("grid-template-columns", "24px 1fr 2fr")
                .set("gap", "16px")
                .set("align-items", "start")
                .set("padding", "12px 0")
                .set("border-bottom", "1px solid var(--border-color)");

        Icon icon = new Icon(iconType);
        icon.setSize("20px");
        icon.getStyle()
                .set("color", "var(--primary-color)")
                .set("margin-top", "2px");

        Span labelSpan = new Span(label);
        labelSpan.getStyle()
                .set("font-weight", "600")
                .set("color", "var(--text-secondary)")
                .set("font-size", "0.9375rem");

        Span valueSpan = new Span(value);
        valueSpan.getStyle()
                .set("font-weight", "600")
                .set("color", valueColor)
                .set("font-size", "0.9375rem");

        row.add(icon, labelSpan, valueSpan);
        return row;
    }

    private Div createActionCard(Event event) {
        Div card = new Div();
        card.getStyle()
                .set("background", "var(--card-bg)")
                .set("border", "1px solid var(--border-color)")
                .set("border-radius", "16px")
                .set("padding", "32px")
                .set("box-shadow", "var(--shadow-sm)");

        int availableSeats = eventService. getAvailableSeats(event.getId());
        boolean canReserve = availableSeats > 0 && event.getStatut() == EventStatus.PUBLIE;

        Button reserveBtn = new Button("Réserver maintenant");
        reserveBtn.setIcon(new Icon(VaadinIcon.TICKET));
        reserveBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE);
        reserveBtn.setEnabled(canReserve);
        reserveBtn.getStyle()
                .set("width", "100%")
                .set("background", canReserve ? "var(--primary-color)" : "var(--text-muted)")
                .set("border-radius", "12px")
                .set("font-weight", "700")
                .set("font-size", "1.0625rem")
                .set("padding", "16px");

        if (! canReserve) {
            reserveBtn.setText(availableSeats == 0 ? "Complet" : "Non disponible");
        }

        reserveBtn.addClickListener(e ->
                getUI().ifPresent(ui -> ui.navigate(ReservationFormView.class, event.getId()))
        );

        Paragraph hint = new Paragraph();
        Icon lockIcon = new Icon(VaadinIcon.LOCK);
        lockIcon.setSize("16px");
        lockIcon.getStyle()
                .set("color", "var(--text-secondary)")
                .set("margin-right", "6px");

        Span hintText = new Span("Vous devez être ");
        Anchor loginLink = new Anchor("/login", "connecté");
        loginLink.getStyle()
                .set("color", "var(--primary-color)")
                .set("font-weight", "600")
                .set("text-decoration", "none");
        Span hintText2 = new Span(" pour réserver.");

        hintText.getStyle().set("color", "var(--text-secondary)");
        hintText2.getStyle().set("color", "var(--text-secondary)");

        hint.add(lockIcon, hintText, loginLink, hintText2);
        hint.getStyle()
                .set("font-size", "0.9375rem")
                .set("color", "var(--text-secondary)")
                .set("text-align", "center")
                .set("margin", "16px 0 0 0")
                .set("display", "flex")
                .set("align-items", "center")
                .set("justify-content", "center");

        card.add(reserveBtn, hint);
        return card;
    }
}