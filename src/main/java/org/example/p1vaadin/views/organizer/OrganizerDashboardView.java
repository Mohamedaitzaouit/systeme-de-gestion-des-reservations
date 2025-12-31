package org.example.p1vaadin.views.organizer;

import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.example.p1vaadin.domain.Event;
import org.example.p1vaadin.domain.User;
import org.example.p1vaadin.domain.enums.EventStatus;
import org.example.p1vaadin.security.UserPrincipal;
import org.example.p1vaadin.service.EventService;
import org.example.p1vaadin.service.ReservationService;
import org.example.p1vaadin.views.MainLayout;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.stream.Collectors;

@Route(value = "organizer-dashboard", layout = MainLayout.class)
@PageTitle("Dashboard Organisateur | Event Booking")
@RolesAllowed({"ORGANIZER", "ADMIN"})
public class OrganizerDashboardView extends VerticalLayout {

    private final EventService eventService;
    private final ReservationService reservationService;

    private static final String FONT_FAMILY = "-apple-system, BlinkMacSystemFont, 'Segoe UI', 'Inter', sans-serif";

    public OrganizerDashboardView(EventService eventService, ReservationService reservationService) {
        this.eventService = eventService;
        this.reservationService = reservationService;

        setSizeFull();
        setPadding(false);
        setSpacing(false);
        getStyle()
                .set("background", "var(--bg-secondary)")
                .set("font-family", FONT_FAMILY);

        User currentUser = getCurrentUser();

        Div container = new Div();
        container.getStyle()
                .set("max-width", "1600px")
                .set("width", "100%")
                .set("margin", "0 auto")
                .set("padding", "40px 24px");

        container.add(
                createHeader(currentUser),
                createKPIGrid(currentUser),
                createChartsSection(currentUser),
                createEventsTable(currentUser)
        );

        add(container);
    }

    private VerticalLayout createHeader(User user) {
        VerticalLayout header = new VerticalLayout();
        header.setPadding(false);
        header.setSpacing(false);
        header.getStyle().set("margin-bottom", "32px");

        Span badge = new Span("ORGANISATEUR");
        badge.getStyle()
                .set("display", "inline-block")
                .set("padding", "4px 12px")
                .set("background", "var(--warning-color, #f59e0b)")
                .set("color", "white")
                .set("border-radius", "6px")
                .set("font-size", "0.75rem")
                .set("font-weight", "700")
                .set("letter-spacing", "1px")
                .set("margin-bottom", "12px");

        H1 title = new H1("Dashboard Business");
        title.getStyle()
                .set("font-size", "2.5rem")
                .set("font-weight", "800")
                .set("color", "var(--text-primary)")
                .set("margin", "0 0 8px 0")
                .set("letter-spacing", "-1px");

        Span subtitle = new Span("Suivez les performances de vos événements en temps réel");
        subtitle.getStyle()
                .set("font-size", "1rem")
                .set("color", "var(--text-secondary)");

        header.add(badge, title, subtitle);
        return header;
    }

    private FlexLayout createKPIGrid(User user) {
        FlexLayout grid = new FlexLayout();
        grid.setWidthFull();
        grid.setFlexWrap(FlexLayout.FlexWrap.WRAP);
        grid.getStyle()
                .set("gap", "20px")
                .set("margin-bottom", "32px");

        long totalEvents = eventService.countByOrganizer(user);
        long publishedEvents = eventService.countPublishedByOrganizer(user);
        long totalReservations = reservationService.countByOrganizer(user);
        double totalRevenue = reservationService.calculateRevenueByOrganizer(user);

        grid.add(
                createKPICard("Événements", String.valueOf(totalEvents), VaadinIcon.CALENDAR, "var(--primary-color)", "+12%"),
                createKPICard("Publiés", String.valueOf(publishedEvents), VaadinIcon.CHECK_CIRCLE, "var(--success-color, #10b981)", "+8%"),
                createKPICard("Réservations", String.valueOf(totalReservations), VaadinIcon.TICKET, "#8b5cf6", "+24%"),
                createKPICard("Revenus", String.format("%.0f DH", totalRevenue), VaadinIcon.DOLLAR, "var(--warning-color, #f59e0b)", "+15%")
        );

        return grid;
    }

    private Div createKPICard(String label, String value, VaadinIcon iconType, String color, String trend) {
        Div card = new Div();
        card.getStyle()
                .set("flex", "1")
                .set("min-width", "240px")
                .set("background", "var(--card-bg)")
                .set("border", "1px solid var(--border-color)")
                .set("border-radius", "16px")
                .set("padding", "24px")
                .set("box-shadow", "var(--shadow-sm)")
                .set("position", "relative")
                .set("overflow", "hidden");

        Div bgGradient = new Div();
        bgGradient.getStyle()
                .set("position", "absolute")
                .set("top", "0")
                .set("right", "0")
                .set("width", "100px")
                .set("height", "100px")
                .set("background", "linear-gradient(135deg, " + color + "15, transparent)")
                .set("border-radius", "50%")
                .set("transform", "translate(30%, -30%)");

        Div content = new Div();
        content.getStyle().set("position", "relative").set("z-index", "1");

        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setJustifyContentMode(JustifyContentMode.BETWEEN);
        header.setAlignItems(Alignment.CENTER);
        header.getStyle().set("margin-bottom", "16px");

        Span labelSpan = new Span(label);
        labelSpan.getStyle()
                .set("font-size", "0.875rem")
                .set("font-weight", "600")
                .set("color", "var(--text-secondary)");

        Icon icon = new Icon(iconType);
        icon.setSize("20px");
        icon.getStyle().set("color", color);

        header.add(labelSpan, icon);

        H2 valueText = new H2(value);
        valueText.getStyle()
                .set("font-size", "2rem")
                .set("font-weight", "800")
                .set("color", "var(--text-primary)")
                .set("margin", "0 0 12px 0")
                .set("letter-spacing", "-1px");

        Span trendSpan = new Span(trend + " vs mois dernier");
        trendSpan.getStyle()
                .set("font-size", "0.8125rem")
                .set("color", "var(--success-color, #10b981)")
                .set("font-weight", "600");

        content.add(header, valueText, trendSpan);
        card.add(bgGradient, content);
        return card;
    }

    private HorizontalLayout createChartsSection(User user) {
        HorizontalLayout section = new HorizontalLayout();
        section.setWidthFull();
        section.setSpacing(false);
        section.getStyle()
                .set("gap", "24px")
                .set("margin-bottom", "32px")
                .set("flex-wrap", "wrap");

        section.add(createRevenueChart(user), createReservationsChart(user));
        return section;
    }

    private Div createRevenueChart(User user) {
        Div card = new Div();
        card.getStyle()
                .set("flex", "1")
                .set("min-width", "400px")
                .set("background", "var(--card-bg)")
                .set("border", "1px solid var(--border-color)")
                .set("border-radius", "16px")
                .set("padding", "24px")
                .set("box-shadow", "var(--shadow-sm)");

        H3 title = new H3("Revenus Mensuels");
        title.getStyle()
                .set("font-size", "1.125rem")
                .set("font-weight", "700")
                .set("color", "var(--text-primary)")
                .set("margin", "0 0 20px 0");

        VerticalLayout bars = new VerticalLayout();
        bars.setPadding(false);
        bars.setSpacing(false);
        bars.getStyle().set("gap", "12px");

        String[] months = {"Jan", "Fév", "Mar", "Avr", "Mai", "Juin"};
        int[] values = {45, 62, 58, 75, 88, 95};

        for (int i = 0; i < months.length; i++) {
            bars.add(createBarItem(months[i], values[i], "var(--warning-color, #f59e0b)"));
        }

        card.add(title, bars);
        return card;
    }

    private HorizontalLayout createBarItem(String label, int value, String color) {
        HorizontalLayout bar = new HorizontalLayout();
        bar.setWidthFull();
        bar.setAlignItems(Alignment.CENTER);
        bar.setSpacing(false);
        bar.getStyle().set("gap", "12px");

        Span labelSpan = new Span(label);
        labelSpan.getStyle()
                .set("width", "40px")
                .set("font-size", "0.8125rem")
                .set("font-weight", "600")
                .set("color", "var(--text-secondary)");

        Div barContainer = new Div();
        barContainer.getStyle()
                .set("flex", "1")
                .set("height", "32px")
                .set("background", "var(--bg-secondary)")
                .set("border-radius", "6px")
                .set("overflow", "hidden")
                .set("position", "relative");

        Div barFill = new Div();
        barFill.getStyle()
                .set("width", value + "%")
                .set("height", "100%")
                .set("background", color)
                .set("border-radius", "6px")
                .set("transition", "width 1s ease");

        barContainer.add(barFill);

        Span valueSpan = new Span(value + "%");
        valueSpan.getStyle()
                .set("width", "50px")
                .set("text-align", "right")
                .set("font-size", "0.875rem")
                .set("font-weight", "700")
                .set("color", "var(--text-primary)");

        bar.add(labelSpan, barContainer, valueSpan);
        return bar;
    }

    private Div createReservationsChart(User user) {
        Div card = new Div();
        card.getStyle()
                .set("flex", "1")
                .set("min-width", "400px")
                .set("background", "var(--card-bg)")
                .set("border", "1px solid var(--border-color)")
                .set("border-radius", "16px")
                .set("padding", "24px")
                .set("box-shadow", "var(--shadow-sm)");

        H3 title = new H3("Répartition Réservations");
        title.getStyle()
                .set("font-size", "1.125rem")
                .set("font-weight", "700")
                .set("color", "var(--text-primary)")
                .set("margin", "0 0 20px 0");

        VerticalLayout segments = new VerticalLayout();
        segments.setPadding(false);
        segments.setSpacing(false);
        segments.getStyle().set("gap", "12px");

        long total = reservationService.countByOrganizer(user);
        segments.add(createSegmentItem("Confirmées", 65, "var(--success-color, #10b981)", total));
        segments.add(createSegmentItem("En Attente", 25, "var(--warning-color, #f59e0b)", total));
        segments.add(createSegmentItem("Annulées", 10, "var(--error-color, #ef4444)", total));

        card.add(title, segments);
        return card;
    }

    private HorizontalLayout createSegmentItem(String label, int percentage, String color, long total) {
        HorizontalLayout item = new HorizontalLayout();
        item.setWidthFull();
        item.setAlignItems(Alignment.CENTER);
        item.setSpacing(false);
        item.getStyle().set("gap", "12px");

        Div colorDot = new Div();
        colorDot.getStyle()
                .set("width", "12px")
                .set("height", "12px")
                .set("border-radius", "50%")
                .set("background", color)
                .set("flex-shrink", "0");

        Span labelSpan = new Span(label);
        labelSpan.getStyle()
                .set("flex", "1")
                .set("font-size", "0.875rem")
                .set("font-weight", "600")
                .set("color", "var(--text-secondary)");

        Div progressBar = new Div();
        progressBar.getStyle()
                .set("width", "100px")
                .set("height", "8px")
                .set("background", "var(--bg-secondary)")
                .set("border-radius", "4px")
                .set("overflow", "hidden");

        Div progressFill = new Div();
        progressFill.getStyle()
                .set("width", percentage + "%")
                .set("height", "100%")
                .set("background", color)
                .set("transition", "width 1s ease");

        progressBar.add(progressFill);

        Span percentSpan = new Span(percentage + "%");
        percentSpan.getStyle()
                .set("width", "50px")
                .set("text-align", "right")
                .set("font-size", "0.875rem")
                .set("font-weight", "700")
                .set("color", "var(--text-primary)");

        item.add(colorDot, labelSpan, progressBar, percentSpan);
        return item;
    }

    private Div createEventsTable(User user) {
        Div card = new Div();
        card.getStyle()
                .set("background", "var(--card-bg)")
                .set("border", "1px solid var(--border-color)")
                .set("border-radius", "16px")
                .set("padding", "24px")
                .set("box-shadow", "var(--shadow-sm)");

        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setJustifyContentMode(JustifyContentMode.BETWEEN);
        header.setAlignItems(Alignment.CENTER);
        header.getStyle().set("margin-bottom", "20px");

        H3 title = new H3("Mes Événements Récents");
        title.getStyle()
                .set("font-size", "1.125rem")
                .set("font-weight", "700")
                .set("color", "var(--text-primary)")
                .set("margin", "0");

        Anchor viewAll = new Anchor("my-events", "Voir tout");
        viewAll.getStyle()
                .set("color", "var(--primary-color)")
                .set("font-weight", "600")
                .set("font-size", "0.875rem")
                .set("text-decoration", "none");

        header.add(title, viewAll);

        VerticalLayout table = new VerticalLayout();
        table.setPadding(false);
        table.setSpacing(false);
        table.getStyle().set("gap", "8px");

        List<Event> events = eventService.findByOrganisateur(user).stream()
                .sorted((e1, e2) -> e2.getDateCreation().compareTo(e1.getDateCreation()))
                .limit(5)
                .collect(Collectors.toList());

        if (events.isEmpty()) {
            Div empty = new Div("Aucun événement");
            empty.getStyle()
                    .set("text-align", "center")
                    .set("padding", "40px")
                    .set("color", "var(--text-secondary)");
            table.add(empty);
        } else {
            for (Event event : events) {
                table.add(createEventRow(event));
            }
        }

        card.add(header, table);
        return card;
    }

    private Div createEventRow(Event event) {
        Div row = new Div();
        row.getStyle()
                .set("padding", "16px")
                .set("border", "1px solid var(--border-color)")
                .set("border-radius", "10px")
                .set("transition", "all 0.2s ease")
                .set("cursor", "pointer");

        row.getElement().setAttribute("onmouseover",
                "this.style.backgroundColor='var(--bg-secondary)'; this.style.borderColor='var(--warning-color, #f59e0b)';");
        row.getElement().setAttribute("onmouseout",
                "this.style.backgroundColor='transparent'; this.style.borderColor='var(--border-color)';");

        row.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("my-events")));

        HorizontalLayout content = new HorizontalLayout();
        content.setWidthFull();
        content.setJustifyContentMode(JustifyContentMode.BETWEEN);
        content.setAlignItems(Alignment.CENTER);

        VerticalLayout info = new VerticalLayout();
        info.setPadding(false);
        info.setSpacing(false);
        info.getStyle().set("gap", "4px");

        Span eventName = new Span(event.getTitre());
        eventName.getStyle()
                .set("font-weight", "600")
                .set("color", "var(--text-primary)")
                .set("font-size", "0.9375rem");

        Span eventMeta = new Span(event.getVille() + " • " + event.getCapaciteMax() + " places");
        eventMeta.getStyle()
                .set("font-size", "0.8125rem")
                .set("color", "var(--text-secondary)");

        info.add(eventName, eventMeta);

        Span statusBadge = new Span(event.getStatut().getLabel());
        statusBadge.getStyle()
                .set("padding", "4px 12px")
                .set("border-radius", "6px")
                .set("font-size", "0.75rem")
                .set("font-weight", "700")
                .set("background", getEventStatusBg(event.getStatut()))
                .set("color", getEventStatusColor(event.getStatut()));

        content.add(info, statusBadge);
        row.add(content);
        return row;
    }

    private String getEventStatusColor(EventStatus status) {
        return switch (status) {
            case PUBLIE -> "var(--success-color, #065f46)";
            case BROUILLON -> "var(--warning-color, #92400e)";
            case ANNULE -> "var(--error-color, #991b1b)";
            case TERMINE -> "var(--primary-color, #1e40af)";
        };
    }

    private String getEventStatusBg(EventStatus status) {
        return switch (status) {
            case PUBLIE -> "var(--success-bg, #d1fae5)";
            case BROUILLON -> "var(--warning-bg, #fef3c7)";
            case ANNULE -> "var(--error-bg, #fee2e2)";
            case TERMINE -> "var(--primary-light, #dbeafe)";
        };
    }

    private User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserPrincipal userPrincipal) {
            return userPrincipal.getUser();
        }
        return null;
    }
}