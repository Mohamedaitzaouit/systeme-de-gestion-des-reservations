package org.example.p1vaadin.views.admin;

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
import org.example.p1vaadin.domain.enums.Role;
import org.example.p1vaadin.service.EventService;
import org.example.p1vaadin.service.ReservationService;
import org.example.p1vaadin.service.UserService;
import org.example.p1vaadin.views.MainLayout;

import java.util.List;
import java.util.stream.Collectors;

@Route(value = "admin-dashboard", layout = MainLayout.class)
@PageTitle("Dashboard Admin | Event Booking")
@RolesAllowed("ADMIN")
public class AdminDashboardView extends VerticalLayout {

    private final UserService userService;
    private final EventService eventService;
    private final ReservationService reservationService;
    private static final String FONT_FAMILY = "-apple-system, BlinkMacSystemFont, 'Segoe UI', 'Inter', sans-serif";

    public AdminDashboardView(UserService userService, EventService eventService, ReservationService reservationService) {
        this.userService = userService;
        this.eventService = eventService;
        this.reservationService = reservationService;

        setSizeFull();
        setPadding(false);
        setSpacing(false);
        getStyle()
                .set("background", "var(--bg-gradient)")
                .set("font-family", FONT_FAMILY);

        Div container = new Div();
        container.getStyle()
                .set("max-width", "1600px")
                .set("width", "100%")
                .set("margin", "0 auto")
                .set("padding", "40px 24px");

        container.add(
                createPremiumHeader(),
                createKPISection(),
                createAnalyticsSection(),
                createManagementSection()
        );

        add(container);
    }

    private Div createPremiumHeader() {
        Div header = new Div();
        header.getStyle()
                .set("background", "linear-gradient(135deg, var(--primary-color) 0%, #6366f1 100%)")
                .set("border-radius", "20px")
                .set("padding", "40px")
                .set("margin-bottom", "32px")
                .set("box-shadow", "0 10px 40px var(--primary-color)30")
                .set("position", "relative")
                .set("overflow", "hidden");

        Div circle1 = new Div();
        circle1.getStyle()
                .set("position", "absolute")
                .set("width", "300px")
                .set("height", "300px")
                .set("border-radius", "50%")
                .set("background", "rgba(255,255,255,0.1)")
                .set("top", "-100px")
                .set("right", "-100px");

        Div circle2 = new Div();
        circle2.getStyle()
                .set("position", "absolute")
                .set("width", "200px")
                .set("height", "200px")
                .set("border-radius", "50%")
                .set("background", "rgba(255,255,255,0.05)")
                .set("bottom", "-50px")
                .set("left", "100px");

        Div content = new Div();
        content.getStyle()
                .set("position", "relative")
                .set("z-index", "1");

        HorizontalLayout headerContent = new HorizontalLayout();
        headerContent.setWidthFull();
        headerContent.setJustifyContentMode(JustifyContentMode.BETWEEN);
        headerContent.setAlignItems(Alignment.CENTER);

        VerticalLayout titleSection = new VerticalLayout();
        titleSection.setPadding(false);
        titleSection.setSpacing(false);
        titleSection.getStyle().set("gap", "8px");

        Span badge = new Span("PANNEAU ADMINISTRATEUR");
        badge.getStyle()
                .set("display", "inline-block")
                .set("padding", "6px 14px")
                .set("background", "rgba(255,255,255,0.2)")
                .set("color", "white")
                .set("border-radius", "8px")
                .set("font-size", "0.75rem")
                .set("font-weight", "700")
                .set("letter-spacing", "1.5px")
                .set("margin-bottom", "4px");

        H1 title = new H1("Centre de Contrôle");
        title.getStyle()
                .set("color", "white")
                .set("font-size", "2.75rem")
                .set("font-weight", "800")
                .set("margin", "0 0 8px 0")
                .set("letter-spacing", "-1px");

        Span subtitle = new Span("Gestion complète et analyse de la plateforme Event Booking");
        subtitle.getStyle()
                .set("color", "rgba(255,255,255,0.9)")
                .set("font-size", "1.0625rem");

        titleSection.add(badge, title, subtitle);

        HorizontalLayout quickStats = new HorizontalLayout();
        quickStats.setSpacing(false);
        quickStats.getStyle().set("gap", "24px");

        long totalUsers = userService.countAll();
        long totalEvents = eventService.countAll();

        quickStats.add(
                createHeaderStat(String.valueOf(totalUsers), "Utilisateurs", VaadinIcon.USERS),
                createHeaderStat(String.valueOf(totalEvents), "Événements", VaadinIcon.CALENDAR)
        );

        headerContent.add(titleSection, quickStats);
        content.add(headerContent);
        header.add(circle1, circle2, content);
        return header;
    }

    private Div createHeaderStat(String value, String label, VaadinIcon iconType) {
        Div stat = new Div();
        stat.getStyle()
                .set("padding", "16px 24px")
                .set("background", "rgba(255,255,255,0.15)")
                .set("border-radius", "12px")
                .set("backdrop-filter", "blur(10px)")
                .set("border", "1px solid rgba(255,255,255,0.2)");
        HorizontalLayout layout = new HorizontalLayout();
        layout.setAlignItems(Alignment.CENTER);
        layout.setSpacing(false);
        layout.getStyle().set("gap", "12px");

        Icon icon = new Icon(iconType);
        icon.setSize("24px");
        icon.getStyle().set("color", "white");

        VerticalLayout textContent = new VerticalLayout();
        textContent.setPadding(false);
        textContent.setSpacing(false);
        textContent.getStyle().set("gap", "2px");

        Span valueSpan = new Span(value);
        valueSpan.getStyle()
                .set("color", "white")
                .set("font-size", "1.5rem")
                .set("font-weight", "800")
                .set("line-height", "1");

        Span labelSpan = new Span(label);
        labelSpan.getStyle()
                .set("color", "rgba(255,255,255,0.85)")
                .set("font-size", "0.8125rem")
                .set("font-weight", "600");

        textContent.add(valueSpan, labelSpan);
        layout.add(icon, textContent);
        stat.add(layout);
        return stat;
    }

    private FlexLayout createKPISection() {
        FlexLayout grid = new FlexLayout();
        grid.setWidthFull();
        grid.setFlexWrap(FlexLayout.FlexWrap.WRAP);
        grid.getStyle()
                .set("gap", "20px")
                .set("margin-bottom", "32px");

        long totalReservations = reservationService.countAll();
        double totalRevenue = reservationService.calculateTotalRevenue();
        long activeEvents = eventService.countPublished();
        long pendingReservations = reservationService.countPending();

        grid.add(
                createKPICard("Réservations", String.valueOf(totalReservations), VaadinIcon.TICKET, "var(--primary-color)", "+24%"),
                createKPICard("Revenus", String.format("%.0f DH", totalRevenue), VaadinIcon.DOLLAR, "var(--success-color, #10b981)", "+15%"),
                createKPICard("Événements Actifs", String.valueOf(activeEvents), VaadinIcon.CHECK_CIRCLE, "var(--warning-color, #f59e0b)", "+8%"),
                createKPICard("En Attente", String.valueOf(pendingReservations), VaadinIcon.CLOCK, "var(--error-color, #ef4444)", "-5%")
        );

        return grid;
    }

    private Div createKPICard(String label, String value, VaadinIcon iconType, String color, String trend) {
        Div card = new Div();
        card.getStyle()
                .set("flex", "1")
                .set("min-width", "260px")
                .set("background", "var(--card-bg)")
                .set("border", "1px solid var(--border-color)")
                .set("border-radius", "16px")
                .set("padding", "24px")
                .set("box-shadow", "var(--shadow-sm)")
                .set("transition", "all 0.3s ease")
                .set("position", "relative")
                .set("overflow", "hidden");

        card.getElement().setAttribute("onmouseover", "this.style.transform='translateY(-6px)'; this.style.boxShadow='0 12px 24px rgba(0, 0, 0, 0.1)';");
        card.getElement().setAttribute("onmouseout", "this.style.transform='translateY(0)'; this.style.boxShadow='var(--shadow-sm)';");

        Div iconBg = new Div();
        iconBg.getStyle()
                .set("position", "absolute")
                .set("top", "-20px")
                .set("right", "-20px")
                .set("width", "100px")
                .set("height", "100px")
                .set("background", color + "10")
                .set("border-radius", "50%");

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
                .set("color", "var(--text-secondary)")
                .set("text-transform", "uppercase")
                .set("letter-spacing", "0.5px");

        Div iconContainer = new Div();
        iconContainer.getStyle()
                .set("width", "48px")
                .set("height", "48px")
                .set("border-radius", "12px")
                .set("background", color)
                .set("display", "flex")
                .set("align-items", "center")
                .set("justify-content", "center")
                .set("box-shadow", "0 4px 12px " + color + "40");

        Icon icon = new Icon(iconType);
        icon.setSize("24px");
        icon.getStyle().set("color", "white");
        iconContainer.add(icon);

        header.add(labelSpan, iconContainer);

        H2 valueText = new H2(value);
        valueText.getStyle()
                .set("font-size", "2.5rem")
                .set("font-weight", "800")
                .set("color", "var(--text-primary)")
                .set("margin", "0 0 12px 0")
                .set("letter-spacing", "-1px");

        HorizontalLayout trendLayout = new HorizontalLayout();
        trendLayout.setSpacing(false);
        trendLayout.getStyle().set("gap", "6px");
        trendLayout.setAlignItems(Alignment.CENTER);

        boolean isPositive = trend.startsWith("+");
        Icon trendIcon = new Icon(isPositive ? VaadinIcon.ARROW_UP : VaadinIcon.ARROW_DOWN);
        trendIcon.setSize("14px");
        trendIcon.getStyle().set("color", isPositive ? "var(--success-color, #10b981)" : "var(--error-color, #ef4444)");

        Span trendText = new Span(trend + " vs mois dernier");
        trendText.getStyle()
                .set("font-size", "0.8125rem")
                .set("font-weight", "600")
                .set("color", isPositive ? "var(--success-color, #10b981)" : "var(--error-color, #ef4444)");

        trendLayout.add(trendIcon, trendText);
        content.add(header, valueText, trendLayout);
        card.add(iconBg, content);
        return card;
    }

    private HorizontalLayout createAnalyticsSection() {
        HorizontalLayout section = new HorizontalLayout();
        section.setWidthFull();
        section.setSpacing(false);
        section.getStyle().set("gap", "24px").set("margin-bottom", "32px").set("flex-wrap", "wrap");
        section.add(createUserDistribution(), createRevenueAnalysis());
        return section;
    }

    private Div createUserDistribution() {
        Div card = new Div();
        card.getStyle()
                .set("flex", "1")
                .set("min-width", "450px")
                .set("background", "var(--card-bg)")
                .set("border", "1px solid var(--border-color)")
                .set("border-radius", "16px")
                .set("padding", "28px")
                .set("box-shadow", "var(--shadow-sm)");

        HorizontalLayout titleBar = new HorizontalLayout();
        titleBar.setWidthFull();
        titleBar.setJustifyContentMode(JustifyContentMode.BETWEEN);
        titleBar.setAlignItems(Alignment.CENTER);
        titleBar.getStyle().set("margin-bottom", "24px");

        H3 title = new H3("Distribution des Utilisateurs");
        title.getStyle().set("font-size", "1.25rem").set("font-weight", "700").set("color", "var(--text-primary)").set("margin", "0");

        Span total = new Span(userService.countAll() + " total");
        total.getStyle().set("font-size", "0.875rem").set("font-weight", "600").set("color", "var(--text-secondary)").set("padding", "6px 12px").set("background", "var(--bg-secondary)").set("border-radius", "8px");
        titleBar.add(title, total);

        VerticalLayout distribution = new VerticalLayout();
        distribution.setPadding(false);
        distribution.setSpacing(false);
        distribution.getStyle().set("gap", "20px");

        long totalUsers = userService.countAll();
        long clients = userService.countByRole(Role.CLIENT);
        long organizers = userService.countByRole(Role.ORGANIZER);
        long admins = userService.countByRole(Role.ADMIN);

        distribution.add(
                createUserDistBar("Clients", clients, totalUsers, "#3b82f6"),
                createUserDistBar("Organisateurs", organizers, totalUsers, "#f59e0b"),
                createUserDistBar("Administrateurs", admins, totalUsers, "#8b5cf6")
        );
        card.add(titleBar, distribution);
        return card;
    }

    private Div createUserDistBar(String role, long count, long total, String color) {
        Div container = new Div();
        container.getStyle()
                .set("padding", "20px")
                .set("background", "var(--bg-secondary)")
                .set("border-radius", "12px")
                .set("border", "1px solid var(--border-color)");

        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setJustifyContentMode(JustifyContentMode.BETWEEN);
        header.setAlignItems(Alignment.CENTER);
        header.getStyle().set("margin-bottom", "12px");

        HorizontalLayout leftSide = new HorizontalLayout();
        leftSide.setSpacing(false);
        leftSide.getStyle().set("gap", "10px");
        leftSide.setAlignItems(Alignment.CENTER);

        Div colorDot = new Div();
        colorDot.getStyle()
                .set("width", "12px")
                .set("height", "12px")
                .set("border-radius", "50%")
                .set("background", color);

        Span roleLabel = new Span(role);
        roleLabel.getStyle().set("font-size", "0.9375rem").set("font-weight", "700").set("color", "var(--text-primary)");

        leftSide.add(colorDot, roleLabel);

        Span countSpan = new Span(count + " utilisateurs");
        countSpan.getStyle().set("font-size", "0.8125rem").set("font-weight", "600").set("color", "var(--text-secondary)");
        header.add(leftSide, countSpan);

        Div progressContainer = new Div();
        progressContainer.getStyle()
                .set("width", "100%")
                .set("height", "12px")
                .set("background", "var(--border-color)")
                .set("border-radius", "6px")
                .set("overflow", "hidden")
                .set("margin-bottom", "8px");

        double percentage = total > 0 ? (count * 100.0 / total) : 0;
        Div progressBar = new Div();
        progressBar.getStyle()
                .set("width", String.format("%.1f%%", percentage))
                .set("height", "100%")
                .set("background", color)
                .set("border-radius", "6px")
                .set("transition", "width 2s cubic-bezier(0.4, 0, 0.2, 1)");

        progressContainer.add(progressBar);
        Span percentSpan = new Span(String.format("%.1f%%", percentage));
        percentSpan.getStyle().set("font-size", "0.875rem").set("font-weight", "800").set("color", color);

        container.add(header, progressContainer, percentSpan);
        return container;
    }

    private Div createRevenueAnalysis() {
        Div card = new Div();
        card.getStyle()
                .set("flex", "1")
                .set("min-width", "450px")
                .set("background", "var(--card-bg)")
                .set("border", "1px solid var(--border-color)")
                .set("border-radius", "16px")
                .set("padding", "28px")
                .set("box-shadow", "var(--shadow-sm)");

        HorizontalLayout titleBar = new HorizontalLayout();
        titleBar.setWidthFull();
        titleBar.setJustifyContentMode(JustifyContentMode.BETWEEN);
        titleBar.setAlignItems(Alignment.CENTER);
        titleBar.getStyle().set("margin-bottom", "24px");

        H3 title = new H3("Analyse des Revenus");
        title.getStyle().set("font-size", "1.25rem").set("font-weight", "700").set("color", "var(--text-primary)").set("margin", "0");

        Span revenue = new Span(String.format("%.0f DH", reservationService.calculateTotalRevenue()));
        revenue.getStyle().set("font-size", "0.875rem").set("font-weight", "600").set("color", "var(--success-color, #10b981)").set("padding", "6px 12px").set("background", "var(--success-bg, #d1fae5)").set("border-radius", "8px");
        titleBar.add(title, revenue);

        VerticalLayout bars = new VerticalLayout();
        bars.setPadding(false);
        bars.setSpacing(false);
        bars.getStyle().set("gap", "16px");

        String[] categories = {"Concerts", "Théâtre", "Conférences", "Sports", "Autres"};
        int[] values = {85, 72, 68, 55, 45};
        String[] colors = {"#3b82f6", "#8b5cf6", "#f59e0b", "#10b981", "#64748b"};

        for (int i = 0; i < categories.length; i++) {
            bars.add(createRevenueBar(categories[i], values[i], colors[i]));
        }
        card.add(titleBar, bars);
        return card;
    }

    private HorizontalLayout createRevenueBar(String category, int value, String color) {
        HorizontalLayout bar = new HorizontalLayout();
        bar.setWidthFull();
        bar.setAlignItems(Alignment.CENTER);
        bar.setSpacing(false);
        bar.getStyle().set("gap", "16px");

        Span label = new Span(category);
        label.getStyle().set("width", "110px").set("font-size", "0.875rem").set("font-weight", "600").set("color", "var(--text-primary)");

        Div barContainer = new Div();
        barContainer.getStyle()
                .set("flex", "1")
                .set("height", "32px")
                .set("background", "var(--bg-secondary)")
                .set("border-radius", "8px")
                .set("overflow", "hidden");

        Div barFill = new Div();
        barFill.getStyle()
                .set("width", value + "%")
                .set("height", "100%")
                .set("background", "linear-gradient(90deg, " + color + ", " + color + "cc)")
                .set("border-radius", "8px")
                .set("transition", "width 2s cubic-bezier(0.4, 0, 0.2, 1)");

        barContainer.add(barFill);

        Span percentSpan = new Span(value + "%");
        percentSpan.getStyle().set("width", "60px").set("text-align", "right").set("font-size", "0.875rem").set("font-weight", "800").set("color", color);

        bar.add(label, barContainer, percentSpan);
        return bar;
    }

    private HorizontalLayout createManagementSection() {
        HorizontalLayout section = new HorizontalLayout();
        section.setWidthFull();
        section.setSpacing(false);
        section.getStyle().set("gap", "24px").set("flex-wrap", "wrap");
        section.add(createRecentActivity(), createQuickAccess());
        return section;
    }

    private Div createRecentActivity() {
        Div card = new Div();
        card.getStyle().set("flex", "2").set("min-width", "500px").set("background", "var(--card-bg)").set("border", "1px solid var(--border-color)").set("border-radius", "16px").set("padding", "28px").set("box-shadow", "var(--shadow-sm)");
        HorizontalLayout titleBar = new HorizontalLayout();
        titleBar.setWidthFull();
        titleBar.setJustifyContentMode(JustifyContentMode.BETWEEN);
        titleBar.setAlignItems(Alignment.CENTER);
        titleBar.getStyle().set("margin-bottom", "24px");

        H3 title = new H3("Activité Récente");
        title.getStyle().set("font-size", "1.25rem").set("font-weight", "700").set("color", "var(--text-primary)").set("margin", "0");

        Span liveBadge = new Span("● En direct");
        liveBadge.getStyle().set("font-size", "0.8125rem").set("font-weight", "600").set("color", "var(--success-color, #10b981)");
        titleBar.add(title, liveBadge);

        VerticalLayout activities = new VerticalLayout();
        activities.setPadding(false);
        activities.setSpacing(false);
        activities.getStyle().set("gap", "12px");

        List<User> recentUsers = userService.findAll().stream().sorted((u1, u2) -> u2.getDateInscription().compareTo(u1.getDateInscription())).limit(3).collect(Collectors.toList());
        List<Event> recentEvents = eventService.findAll().stream().sorted((e1, e2) -> e2.getDateCreation().compareTo(e1.getDateCreation())).limit(2).collect(Collectors.toList());

        for (User user : recentUsers) {
            activities.add(createActivityItem("Nouvel utilisateur inscrit", user.getFullName() + " vient de créer un compte", VaadinIcon.USER_CHECK, "var(--success-color, #10b981)", "Il y a quelques instants"));
        }
        for (Event event : recentEvents) {
            activities.add(createActivityItem("Événement publié", event.getTitre() + " par " + event.getOrganisateur().getFullName(), VaadinIcon.CALENDAR, "var(--warning-color, #f59e0b)", "Récemment"));
        }
        card.add(titleBar, activities);
        return card;
    }

    private Div createActivityItem(String title, String description, VaadinIcon iconType, String color, String time) {
        Div item = new Div();
        item.getStyle()
                .set("padding", "16px")
                .set("border", "1px solid var(--border-color)")
                .set("border-radius", "12px")
                .set("transition", "all 0.2s ease")
                .set("cursor", "pointer");
        item.getElement().setAttribute("onmouseover", "this.style.backgroundColor='var(--bg-secondary)'; this.style.borderColor=" + color + ";");
        item.getElement().setAttribute("onmouseout", "this.style.backgroundColor='transparent'; this.style.borderColor='var(--border-color)';");

        HorizontalLayout layout = new HorizontalLayout();
        layout.setWidthFull();
        layout.setAlignItems(Alignment.CENTER);
        layout.setSpacing(false);
        layout.getStyle().set("gap", "16px");

        Div iconContainer = new Div();
        iconContainer.getStyle()
                .set("width", "40px")
                .set("height", "40px")
                .set("border-radius", "10px")
                .set("background", color + "15")
                .set("display", "flex")
                .set("align-items", "center")
                .set("justify-content", "center")
                .set("flex-shrink", "0");

        Icon icon = new Icon(iconType);
        icon.setSize("20px");
        icon.getStyle().set("color", color);
        iconContainer.add(icon);

        VerticalLayout content = new VerticalLayout();
        content.setPadding(false);
        content.setSpacing(false);
        content.getStyle().set("gap", "4px").set("flex", "1");

        Span titleSpan = new Span(title);
        titleSpan.getStyle().set("font-size", "0.9375rem").set("font-weight", "700").set("color", "var(--text-primary)");

        Span descSpan = new Span(description);
        descSpan.getStyle().set("font-size", "0.8125rem").set("color", "var(--text-secondary)");

        content.add(titleSpan, descSpan);

        Span timeSpan = new Span(time);
        timeSpan.getStyle().set("font-size", "0.75rem").set("font-weight", "600").set("color", "var(--text-secondary)").set("white-space", "nowrap");

        layout.add(iconContainer, content, timeSpan);
        item.add(layout);
        return item;
    }

    private Div createQuickAccess() {
        Div card = new Div();
        card.getStyle().set("flex", "1").set("min-width", "350px").set("background", "var(--card-bg)").set("border", "1px solid var(--border-color)").set("border-radius", "16px").set("padding", "28px").set("box-shadow", "var(--shadow-sm)");

        H3 title = new H3("Gestion Rapide");
        title.getStyle().set("font-size", "1.25rem").set("font-weight", "700").set("color", "var(--text-primary)").set("margin", "0 0 24px 0");

        VerticalLayout actions = new VerticalLayout();
        actions.setPadding(false);
        actions.setSpacing(false);
        actions.getStyle().set("gap", "12px");

        actions.add(
                createManagementButton("Gérer Utilisateurs", "Voir tous les utilisateurs", VaadinIcon.USERS, "var(--primary-color)", "dashboard"),
                createManagementButton("Explorer Événements", "Parcourir tous les événements", VaadinIcon.CALENDAR_BRIEFCASE, "var(--warning-color, #f59e0b)", "events"),
                createManagementButton("Mon Dashboard", "Retour au tableau de bord", VaadinIcon.DASHBOARD, "var(--success-color, #10b981)", "dashboard")
        );
        card.add(title, actions);
        return card;
    }

    private Div createManagementButton(String title, String description, VaadinIcon iconType, String color, String route) {
        Div button = new Div();
        button.getStyle().set("padding", "18px").set("border", "2px solid var(--border-color)").set("border-radius", "12px").set("cursor", "pointer").set("transition", "all 0.25s ease");
        button.getElement().setAttribute("onmouseover", "this.style.borderColor=" + color + "; this.style.backgroundColor='var(--bg-secondary)'; this.style.transform='translateX(6px)';");
        button.getElement().setAttribute("onmouseout", "this.style.borderColor='var(--border-color)'; this.style.backgroundColor='transparent'; this.style.transform='translateX(0)';");
        button.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate(route)));

        HorizontalLayout layout = new HorizontalLayout();
        layout.setWidthFull();
        layout.setAlignItems(Alignment.CENTER);
        layout.setSpacing(false);
        layout.getStyle().set("gap", "16px");

        Div iconContainer = new Div();
        iconContainer.getStyle().set("width", "48px").set("height", "48px").set("border-radius", "12px").set("background", color).set("display", "flex").set("align-items", "center").set("justify-content", "center").set("flex-shrink", "0").set("box-shadow", "0 4px 12px " + color + "30");

        Icon icon = new Icon(iconType);
        icon.setSize("24px");
        icon.getStyle().set("color", "white");
        iconContainer.add(icon);

        VerticalLayout textContent = new VerticalLayout();
        textContent.setPadding(false);
        textContent.setSpacing(false);
        textContent.getStyle().set("gap", "4px").set("flex", "1");

        Span titleSpan = new Span(title);
        titleSpan.getStyle().set("font-size", "1rem").set("font-weight", "700").set("color", "var(--text-primary)");

        Span descSpan = new Span(description);
        descSpan.getStyle().set("font-size", "0.8125rem").set("color", "var(--text-secondary)");

        textContent.add(titleSpan, descSpan);

        Icon arrow = new Icon(VaadinIcon.ARROW_RIGHT);
        arrow.setSize("20px");
        arrow.getStyle().set("color", color).set("flex-shrink", "0");
        layout.add(iconContainer, textContent, arrow);
        button.add(layout);
        return button;
    }
}