package org.example.p1vaadin.views.client;

import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.example.p1vaadin.domain.Reservation;
import org.example.p1vaadin.domain.User;
import org.example.p1vaadin.domain.enums.ReservationStatus;
import org.example.p1vaadin.security.UserPrincipal;
import org.example.p1vaadin.service.ReservationService;
import org.example.p1vaadin.views.MainLayout;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Route(value = "dashboard", layout = MainLayout.class)
@PageTitle("Dashboard | Event Booking")
@RolesAllowed({"CLIENT", "ORGANIZER", "ADMIN"})
public class DashboardView extends VerticalLayout {

    private final ReservationService reservationService;
    private static final String FONT_FAMILY = "-apple-system, BlinkMacSystemFont, 'Segoe UI', 'Inter', sans-serif";
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy");

    public DashboardView(ReservationService reservationService) {
        this.reservationService = reservationService;

        setSizeFull();
        setPadding(false);
        setSpacing(false);
        getStyle()
                .set("background", "var(--bg-gradient)")
                .set("font-family", FONT_FAMILY);

        User currentUser = getCurrentUser();

        Div container = new Div();
        container.getStyle()
                .set("max-width", "1400px")
                .set("width", "100%")
                .set("margin", "0 auto")
                .set("padding", "40px 24px");

        container.add(
                createWelcomeSection(currentUser),
                createStatsGrid(currentUser),
                createContentGrid(currentUser)
        );

        add(container);
    }

    private Div createWelcomeSection(User user) {
        Div section = new Div();
        section.getStyle()
                .set("background", "linear-gradient(135deg, var(--primary-color) 0%, var(--primary-hover, #2563eb) 100%)")
                .set("border-radius", "20px")
                .set("padding", "40px")
                .set("margin-bottom", "32px")
                .set("box-shadow", "0 10px 40px var(--primary-color)20")
                .set("position", "relative")
                .set("overflow", "hidden");

        // Decorative circles
        Div circle1 = new Div();
        circle1.getStyle()
                .set("position", "absolute")
                .set("width", "200px")
                .set("height", "200px")
                .set("border-radius", "50%")
                .set("background", "rgba(255, 255, 255, 0.1)")
                .set("top", "-50px")
                .set("right", "-50px");

        Div circle2 = new Div();
        circle2.getStyle()
                .set("position", "absolute")
                .set("width", "150px")
                .set("height", "150px")
                .set("border-radius", "50%")
                .set("background", "rgba(255, 255, 255, 0.05)")
                .set("bottom", "-30px")
                .set("left", "100px");

        Div content = new Div();
        content.getStyle().set("position", "relative").set("z-index", "1");

        Span greeting = new Span("Bonjour,");
        greeting.getStyle()
                .set("color", "rgba(255, 255, 255, 0.9)")
                .set("font-size", "1.125rem")
                .set("display", "block")
                .set("margin-bottom", "8px");

        H1 userName = new H1(user.getFullName() + " 👋");
        userName.getStyle()
                .set("color", "white")
                .set("font-size", "2.5rem")
                .set("font-weight", "800")
                .set("margin", "0 0 12px 0")
                .set("letter-spacing", "-1px");

        Span subtitle = new Span("Bienvenue sur votre tableau de bord personnel");
        subtitle.getStyle()
                .set("color", "rgba(255, 255, 255, 0.85)")
                .set("font-size", "1.0625rem");

        content.add(greeting, userName, subtitle);
        section.add(circle1, circle2, content);
        return section;
    }

    private FlexLayout createStatsGrid(User user) {
        FlexLayout grid = new FlexLayout();
        grid.setWidthFull();
        grid.setFlexWrap(FlexLayout.FlexWrap.WRAP);
        grid.getStyle()
                .set("gap", "24px")
                .set("margin-bottom", "32px");

        long totalReservations = reservationService.countByUser(user);
        long confirmedReservations = reservationService.countConfirmedByUser(user);
        long pendingReservations = reservationService.countPendingByUser(user);

        grid.add(
                createStatCard("Total", String.valueOf(totalReservations),
                        VaadinIcon.TICKET, "var(--primary-color)", "var(--primary-light, #dbeafe)", "Réservations totales"),
                createStatCard("Confirmées", String.valueOf(confirmedReservations),
                        VaadinIcon.CHECK_CIRCLE, "var(--success-color, #10b981)", "var(--success-bg, #d1fae5)", "Réservations confirmées"),
                createStatCard("En attente", String.valueOf(pendingReservations),
                        VaadinIcon.CLOCK, "var(--warning-color, #f59e0b)", "var(--warning-bg, #fef3c7)", "En attente de confirmation")
        );

        return grid;
    }

    private Div createStatCard(String label, String value, VaadinIcon iconType,
                               String color, String bgColor, String description) {
        Div card = new Div();
        card.getStyle()
                .set("flex", "1")
                .set("min-width", "280px")
                .set("background", "var(--card-bg)")
                .set("border", "1px solid var(--border-color)")
                .set("border-radius", "16px")
                .set("padding", "28px")
                .set("box-shadow", "var(--shadow-sm)")
                .set("transition", "all 0.3s cubic-bezier(0.4, 0, 0.2, 1)")
                .set("cursor", "pointer")
                .set("position", "relative")
                .set("overflow", "hidden");

        card.getElement().setAttribute("onmouseover",
                "this.style.transform='translateY(-8px)'; this.style.boxShadow='0 12px 32px rgba(0, 0, 0, 0.12)'; this.style.borderColor=" + color + ";");
        card.getElement().setAttribute("onmouseout",
                "this.style.transform='translateY(0)'; this.style.boxShadow='var(--shadow-sm)'; this.style.borderColor='var(--border-color)';");

        Div bgDecoration = new Div();
        bgDecoration.getStyle()
                .set("position", "absolute")
                .set("width", "120px")
                .set("height", "120px")
                .set("border-radius", "50%")
                .set("background", bgColor)
                .set("top", "-40px")
                .set("right", "-40px")
                .set("opacity", "0.5");

        Div iconContainer = new Div();
        iconContainer.getStyle()
                .set("width", "56px")
                .set("height", "56px")
                .set("border-radius", "14px")
                .set("background", bgColor)
                .set("display", "flex")
                .set("align-items", "center")
                .set("justify-content", "center")
                .set("margin-bottom", "20px")
                .set("position", "relative")
                .set("z-index", "1");

        Icon icon = new Icon(iconType);
        icon.setSize("28px");
        icon.getStyle().set("color", color);
        iconContainer.add(icon);

        H2 valueText = new H2(value);
        valueText.getStyle()
                .set("font-size", "3rem")
                .set("font-weight", "800")
                .set("color", "var(--text-primary)")
                .set("margin", "0 0 8px 0")
                .set("letter-spacing", "-2px")
                .set("position", "relative")
                .set("z-index", "1");

        Span labelText = new Span(label);
        labelText.getStyle()
                .set("font-size", "1rem")
                .set("color", "var(--text-primary)")
                .set("font-weight", "700")
                .set("display", "block")
                .set("margin-bottom", "4px")
                .set("position", "relative")
                .set("z-index", "1");

        Span descText = new Span(description);
        descText.getStyle()
                .set("font-size", "0.875rem")
                .set("color", "var(--text-secondary)")
                .set("position", "relative")
                .set("z-index", "1");

        card.add(bgDecoration, iconContainer, valueText, labelText, descText);
        return card;
    }

    private HorizontalLayout createContentGrid(User user) {
        HorizontalLayout grid = new HorizontalLayout();
        grid.setWidthFull();
        grid.setSpacing(false);
        grid.getStyle()
                .set("gap", "24px")
                .set("flex-wrap", "wrap");

        grid.add(createRecentReservations(user), createQuickActions());
        return grid;
    }

    private Div createRecentReservations(User user) {
        Div card = new Div();
        card.getStyle()
                .set("flex", "2")
                .set("min-width", "400px")
                .set("background", "var(--card-bg)")
                .set("border", "1px solid var(--border-color)")
                .set("border-radius", "16px")
                .set("padding", "28px")
                .set("box-shadow", "var(--shadow-sm)");

        // Header
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setJustifyContentMode(JustifyContentMode.BETWEEN);
        header.setAlignItems(Alignment.CENTER);

        H3 title = new H3("📋 Réservations récentes");
        title.getStyle()
                .set("font-size", "1.25rem")
                .set("font-weight", "700")
                .set("color", "var(--text-primary)")
                .set("margin", "0");

        Anchor viewAll = new Anchor("my-reservations", "Voir tout →");
        viewAll.getStyle()
                .set("color", "var(--primary-color)")
                .set("font-weight", "600")
                .set("font-size", "0.9375rem")
                .set("text-decoration", "none");

        header.add(title, viewAll);

        VerticalLayout list = new VerticalLayout();
        list.setPadding(false);
        list.setSpacing(false);
        list.getStyle()
                .set("gap", "12px")
                .set("margin-top", "24px");

        List<Reservation> recentReservations = reservationService.findByUser(user).stream()
                .sorted((r1, r2) -> r2.getDateReservation().compareTo(r1.getDateReservation()))
                .limit(5)
                .collect(Collectors.toList());

        if (recentReservations.isEmpty()) {
            Div empty = new Div();
            empty.getStyle()
                    .set("text-align", "center")
                    .set("padding", "40px 20px")
                    .set("color", "var(--text-secondary)");

            Span emptyIcon = new Span("📭");
            emptyIcon.getStyle()
                    .set("font-size", "3rem")
                    .set("display", "block")
                    .set("margin-bottom", "12px");

            Span emptyText = new Span("Aucune réservation pour le moment");
            emptyText.getStyle().set("font-size", "1rem");

            empty.add(emptyIcon, emptyText);
            list.add(empty);
        } else {
            for (Reservation reservation : recentReservations) {
                list.add(createReservationItem(reservation));
            }
        }

        card.add(header, list);
        return card;
    }

    private Div createReservationItem(Reservation reservation) {
        Div item = new Div();
        item.getStyle()
                .set("padding", "16px")
                .set("border", "1px solid var(--border-color)")
                .set("border-radius", "12px")
                .set("transition", "all 0.2s ease")
                .set("cursor", "pointer");

        item.getElement().setAttribute("onmouseover",
                "this.style.backgroundColor='var(--bg-secondary)'; this.style.transform='translateX(4px)';");
        item.getElement().setAttribute("onmouseout",
                "this.style.backgroundColor='transparent'; this.style.transform='translateX(0)';");

        item.addClickListener(e ->
                getUI().ifPresent(ui -> ui.navigate("my-reservations")));

        H4 eventTitle = new H4(reservation.getEvenement().getTitre());
        eventTitle.getStyle()
                .set("font-size", "1rem")
                .set("font-weight", "600")
                .set("color", "var(--text-primary)")
                .set("margin", "0 0 8px 0");

        HorizontalLayout meta = new HorizontalLayout();
        meta.setSpacing(false);
        meta.getStyle()
                .set("gap", "16px")
                .set("flex-wrap", "wrap");

        Span date = new Span("📅 " + reservation.getEvenement().getDateDebut().format(dateFormatter));
        date.getStyle()
                .set("font-size", "0.875rem")
                .set("color", "var(--text-secondary)");

        Span places = new Span("🎫 " + reservation.getNombrePlaces() + " place(s)");
        places.getStyle()
                .set("font-size", "0.875rem")
                .set("color", "var(--text-secondary)");

        Span status = new Span(getStatusIcon(reservation.getStatut()) + " " + reservation.getStatut().getLabel());
        status.getStyle()
                .set("font-size", "0.8125rem")
                .set("font-weight", "700")
                .set("padding", "4px 10px")
                .set("border-radius", "6px")
                .set("background", getStatusBgColor(reservation.getStatut()))
                .set("color", getStatusColor(reservation.getStatut()));

        meta.add(date, places, status);

        item.add(eventTitle, meta);
        return item;
    }

    private Div createQuickActions() {
        Div card = new Div();
        card.getStyle()
                .set("flex", "1")
                .set("min-width", "320px")
                .set("background", "var(--card-bg)")
                .set("border", "1px solid var(--border-color)")
                .set("border-radius", "16px")
                .set("padding", "28px")
                .set("box-shadow", "var(--shadow-sm)");

        H3 title = new H3("⚡ Actions rapides");
        title.getStyle()
                .set("font-size", "1.25rem")
                .set("font-weight", "700")
                .set("color", "var(--text-primary)")
                .set("margin", "0 0 24px 0");

        VerticalLayout actions = new VerticalLayout();
        actions.setPadding(false);
        actions.setSpacing(false);
        actions.getStyle().set("gap", "12px");

        actions.add(
                createActionButton("Parcourir les événements", "Découvrir de nouveaux événements",
                        VaadinIcon.CALENDAR, "events", "var(--primary-color)"),
                createActionButton("Mes réservations", "Gérer mes réservations",
                        VaadinIcon.TICKET, "my-reservations", "var(--success-color, #10b981)"),
                createActionButton("Mon profil", "Modifier mes informations",
                        VaadinIcon.USER, "profile", "#8b5cf6")
        );

        card.add(title, actions);
        return card;
    }

    private Div createActionButton(String title, String description, VaadinIcon iconType,
                                   String route, String color) {
        Div button = new Div();
        button.getStyle()
                .set("padding", "16px")
                .set("border", "2px solid var(--border-color)")
                .set("border-radius", "12px")
                .set("cursor", "pointer")
                .set("transition", "all 0.2s ease")
                .set("display", "flex")
                .set("align-items", "center")
                .set("gap", "16px");

        button.getElement().setAttribute("onmouseover",
                "this.style.borderColor=" + color + "; this.style.backgroundColor='var(--bg-secondary)'; this.style.transform='translateX(4px)';");
        button.getElement().setAttribute("onmouseout",
                "this.style.borderColor='var(--border-color)'; this.style.backgroundColor='transparent'; this.style.transform='translateX(0)';");

        button.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate(route)));

        Div iconContainer = new Div();
        iconContainer.getStyle()
                .set("width", "48px")
                .set("height", "48px")
                .set("border-radius", "12px")
                .set("background", color)
                .set("display", "flex")
                .set("align-items", "center")
                .set("justify-content", "center")
                .set("flex-shrink", "0");

        Icon icon = new Icon(iconType);
        icon.setSize("24px");
        icon.getStyle().set("color", "white");
        iconContainer.add(icon);

        Div textContent = new Div();
        textContent.getStyle().set("flex", "1");

        Div titleDiv = new Div(title);
        titleDiv.getStyle()
                .set("font-weight", "700")
                .set("color", "var(--text-primary)")
                .set("font-size", "1rem")
                .set("margin-bottom", "4px");

        Div descDiv = new Div(description);
        descDiv.getStyle()
                .set("font-size", "0.875rem")
                .set("color", "var(--text-secondary)");

        textContent.add(titleDiv, descDiv);

        Icon arrow = new Icon(VaadinIcon.ARROW_RIGHT);
        arrow.setSize("20px");
        arrow.getStyle()
                .set("color", color)
                .set("flex-shrink", "0");

        button.add(iconContainer, textContent, arrow);
        return button;
    }

    private String getStatusIcon(ReservationStatus status) {
        return switch (status) {
            case CONFIRMEE -> "✅";
            case EN_ATTENTE -> "⏳";
            case ANNULEE -> "❌";
        };
    }

    private String getStatusColor(ReservationStatus status) {
        return switch (status) {
            case CONFIRMEE -> "var(--success-color, #065f46)";
            case EN_ATTENTE -> "var(--warning-color, #92400e)";
            case ANNULEE -> "var(--error-color, #991b1b)";
        };
    }

    private String getStatusBgColor(ReservationStatus status) {
        return switch (status) {
            case CONFIRMEE -> "var(--success-bg, #d1fae5)";
            case EN_ATTENTE -> "var(--warning-bg, #fef3c7)";
            case ANNULEE -> "var(--error-bg, #fee2e2)";
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