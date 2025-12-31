package org.example.p1vaadin. views;

import com.vaadin.flow.component.applayout. AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component. dependency.CssImport;
import com.vaadin.flow. component.html. Div;
import com.vaadin.flow.component.html.H4;
import com.vaadin. flow.component.html.Span;
import com.vaadin. flow.component.icon.Icon;
import com.vaadin.flow.component.icon. VaadinIcon;
import com.vaadin.flow.component. orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout. VerticalLayout;
import com.vaadin.flow.router.RouterLink;
import org.example. p1vaadin.domain.User;
import org.example.p1vaadin.domain.enums.Role;
import org. example.p1vaadin.security.UserPrincipal;
import org.example.p1vaadin.service. ThemeService;
import org.example.p1vaadin.views.admin.*;
import org.example.p1vaadin.views.auth. LoginView;
import org.example. p1vaadin.views.client.*;
import org.example.p1vaadin.views.organizer.*;
import org.example.p1vaadin.views.publics.*;
import org.springframework.security.core.context.SecurityContextHolder;

@CssImport("./styles/styles.css")
public class MainLayout extends AppLayout {

    private User currentUser;
    private final ThemeService themeService;
    private Button themeButton;

    private static final String FONT_FAMILY = "-apple-system, BlinkMacSystemFont, 'Segoe UI', 'Inter', sans-serif";

    public MainLayout(ThemeService themeService) {
        this.themeService = themeService;
        this.currentUser = getCurrentUser();

        getElement().getStyle()
                .set("--vaadin-app-layout-drawer-width", "280px")
                .set("font-family", FONT_FAMILY);

        // Initialiser le thème
        themeService.initializeTheme();

        createHeader();
        createDrawer();
    }

    private void createHeader() {
        // Logo
        Div logoContainer = new Div();
        logoContainer.getStyle()
                .set("display", "flex")
                .set("align-items", "center")
                .set("gap", "10px")
                .set("cursor", "pointer");

        Span logoIcon = new Span("📅");
        logoIcon.getStyle().set("font-size", "1.5rem");

        Span logoText = new Span("Event Booking");
        logoText.getStyle()
                .set("font-size", "1.25rem")
                .set("font-weight", "700")
                .set("letter-spacing", "-0.5px");

        logoContainer.add(logoIcon, logoText);
        logoContainer.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("")));

        DrawerToggle toggle = new DrawerToggle();

        HorizontalLayout header = new HorizontalLayout(toggle, logoContainer);
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.setWidthFull();
        header.getStyle()
                .set("padding", "0 24px")
                .set("height", "64px");

        Div spacer = new Div();
        header.add(spacer);
        header.setFlexGrow(1, spacer);

        // ✅ Bouton de thème
        themeButton = createThemeButton();
        header.add(themeButton);

        if (currentUser != null) {
            header.add(createUserSection());
        } else {
            header.add(createLoginButton());
        }

        addToNavbar(header);
    }

    private Button createThemeButton() {
        Button btn = new Button();
        updateThemeButtonIcon(btn);

        btn.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ICON);
        btn.getStyle()
                .set("width", "40px")
                .set("height", "40px")
                .set("border-radius", "10px")
                .set("margin-right", "12px");

        btn.setTooltipText(themeService.isDarkMode() ? "Mode clair" : "Mode sombre");

        btn.addClickListener(e -> {
            themeService.toggleTheme();
            updateThemeButtonIcon(btn);
            btn.setTooltipText(themeService. isDarkMode() ? "Mode clair" : "Mode sombre");

            // Rafraîchir la page pour appliquer complètement le thème
            getUI().ifPresent(ui -> ui.getPage().reload());
        });

        return btn;
    }

    private void updateThemeButtonIcon(Button btn) {
        boolean isDark = themeService.isDarkMode();
        btn.setIcon(new Icon(isDark ? VaadinIcon.SUN_O : VaadinIcon.MOON_O));

        if (isDark) {
            btn.getStyle()
                    .set("background", "#374151")
                    .set("color", "#fbbf24");
        } else {
            btn.getStyle()
                    . set("background", "#fef3c7")
                    . set("color", "#f59e0b");
        }
    }

    private HorizontalLayout createUserSection() {
        HorizontalLayout userSection = new HorizontalLayout();
        userSection.setAlignItems(FlexComponent. Alignment.CENTER);
        userSection.setSpacing(true);
        userSection.getStyle().set("gap", "16px");

        Div avatar = new Div();
        String initials = currentUser.getPrenom().substring(0, 1) +
                currentUser.getNom().substring(0, 1);
        avatar.setText(initials. toUpperCase());
        avatar.getStyle()
                .set("width", "38px")
                .set("height", "38px")
                .set("border-radius", "10px")
                .set("background", "var(--primary-color)")
                .set("color", "white")
                .set("display", "flex")
                .set("align-items", "center")
                .set("justify-content", "center")
                .set("font-weight", "600")
                .set("font-size", "0.875rem");

        Div userInfo = new Div();
        userInfo.getStyle()
                .set("display", "flex")
                .set("flex-direction", "column")
                .set("gap", "2px");

        Span userName = new Span(currentUser. getFullName());
        userName.getStyle()
                .set("font-weight", "600")
                .set("font-size", "0.875rem");

        Span userRole = new Span(currentUser. getRole().getLabel());
        userRole.getStyle()
                .set("font-size", "0.75rem")
                .set("opacity", "0.7");

        userInfo.add(userName, userRole);

        Button logoutBtn = new Button();
        logoutBtn.setIcon(new Icon(VaadinIcon. SIGN_OUT));
        logoutBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ICON);
        logoutBtn.setTooltipText("Déconnexion");
        logoutBtn.addClickListener(e -> {
            SecurityContextHolder.clearContext();
            getUI().ifPresent(ui -> ui.getPage().setLocation("/login"));
        });

        userSection.add(avatar, userInfo, logoutBtn);
        return userSection;
    }

    private Button createLoginButton() {
        Button loginBtn = new Button("Connexion");
        loginBtn.setIcon(new Icon(VaadinIcon.SIGN_IN));
        loginBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        loginBtn.getStyle()
                .set("border-radius", "8px")
                .set("font-weight", "600");
        loginBtn.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate(LoginView.class)));
        return loginBtn;
    }

    private void createDrawer() {
        VerticalLayout drawer = new VerticalLayout();
        drawer.setPadding(false);
        drawer.setSpacing(false);
        drawer.setSizeFull();
        drawer.getStyle().set("padding", "20px 0");

        if (currentUser != null) {
            drawer.add(createUserCard());
        }

        drawer.add(createNavigationSection());
        addToDrawer(drawer);
    }

    private Div createUserCard() {
        Div card = new Div();
        card.addClassName("card");
        card.getStyle()
                .set("border-radius", "12px")
                .set("padding", "16px")
                .set("margin", "0 12px 20px 12px");

        Div avatar = new Div();
        String initials = currentUser.getPrenom().substring(0, 1) +
                currentUser.getNom().substring(0, 1);
        avatar.setText(initials.toUpperCase());
        avatar.getStyle()
                .set("width", "52px")
                .set("height", "52px")
                .set("border-radius", "12px")
                .set("background", "var(--primary-color)")
                .set("color", "white")
                .set("display", "flex")
                .set("align-items", "center")
                .set("justify-content", "center")
                .set("font-weight", "700")
                .set("font-size", "1.125rem")
                .set("margin-bottom", "12px");

        Div name = new Div(currentUser.getFullName());
        name.getStyle()
                .set("font-weight", "700")
                .set("font-size", "0.9375rem")
                .set("margin-bottom", "4px");

        Div email = new Div(currentUser.getEmail());
        email.getStyle()
                .set("font-size", "0.8125rem")
                .set("opacity", "0.7")
                .set("margin-bottom", "10px");

        Span roleBadge = new Span(currentUser.getRole().getLabel());
        roleBadge.getStyle()
                .set("display", "inline-block")
                .set("background", "var(--primary-color)")
                .set("color", "white")
                .set("padding", "4px 10px")
                .set("border-radius", "6px")
                .set("font-size", "0.6875rem")
                .set("font-weight", "700");

        card.add(avatar, name, email, roleBadge);
        return card;
    }

    private VerticalLayout createNavigationSection() {
        VerticalLayout nav = new VerticalLayout();
        nav.setPadding(false);
        nav.setSpacing(false);

        nav.add(createMenuSection("Navigation",
                createNavLink(VaadinIcon.HOME, "Accueil", HomeView.class),
                createNavLink(VaadinIcon. CALENDAR, "Événements", EventListView.class)
        ));

        if (currentUser != null) {
            nav.add(createMenuSection("Mon Espace",
                    createNavLink(VaadinIcon.DASHBOARD, "Dashboard", DashboardView.class),
                    createNavLink(VaadinIcon.TICKET, "Mes Réservations", MyReservationsView.class),
                    createNavLink(VaadinIcon.USER, "Profil", ProfileView.class)
            ));

            if (currentUser.getRole() == Role.ORGANIZER || currentUser.getRole() == Role.ADMIN) {
                nav.add(createMenuSection("Organisateur",
                        createNavLink(VaadinIcon.CHART, "Dashboard Org.", OrganizerDashboardView. class),
                        createNavLink(VaadinIcon.CALENDAR_O, "Mes Événements", MyEventsView.class),
                        createNavLink(VaadinIcon.PLUS, "Créer Événement", EventFormView.class)
                ));
            }

            if (currentUser.getRole() == Role.ADMIN) {
                nav.add(createMenuSection("Administration",
                        createNavLink(VaadinIcon.COG, "Admin Dashboard", AdminDashboardView.class),
                        createNavLink(VaadinIcon.USERS, "Utilisateurs", UserManagementView.class),
                        createNavLink(VaadinIcon.CALENDAR_BRIEFCASE, "Tous Événements", AllEventsManagementView.class),
                        createNavLink(VaadinIcon.LIST, "Réservations", AllReservationsView.class)
                ));
            }
        }

        return nav;
    }

    private VerticalLayout createMenuSection(String title, RouterLink... links) {
        VerticalLayout section = new VerticalLayout();
        section.setPadding(false);
        section.setSpacing(false);
        section.getStyle().set("margin-bottom", "20px");

        H4 header = new H4(title);
        header.getStyle()
                .set("font-size", "0.6875rem")
                .set("font-weight", "700")
                .set("text-transform", "uppercase")
                .set("letter-spacing", "1px")
                .set("margin", "0 0 8px 0")
                .set("padding", "0 16px")
                .set("opacity", "0.6");

        section.add(header);
        for (RouterLink link : links) {
            section.add(link);
        }
        return section;
    }

    private RouterLink createNavLink(VaadinIcon iconType, String text, Class<? > viewClass) {
        RouterLink link = new RouterLink();
        link.setRoute(viewClass. asSubclass(com.vaadin.flow.component.Component.class));

        Icon icon = new Icon(iconType);
        icon.getStyle()
                .set("width", "18px")
                .set("height", "18px");

        Span label = new Span(text);
        label.getStyle()
                .set("font-weight", "600")
                .set("font-size", "0.875rem");

        HorizontalLayout content = new HorizontalLayout(icon, label);
        content.setAlignItems(FlexComponent. Alignment.CENTER);
        content.getStyle()
                .set("padding", "10px 16px")
                .set("border-radius", "10px")
                .set("margin", "2px 12px")
                .set("cursor", "pointer");

        content.getElement().setAttribute("onmouseover",
                "this.style.background='var(--hover-bg)'; this.style.transform='translateX(4px)';");
        content.getElement().setAttribute("onmouseout",
                "this.style.background='transparent'; this.style.transform='translateX(0)';");

        link.add(content);
        return link;
    }

    private User getCurrentUser() {
        try {
            Object principal = SecurityContextHolder.getContext()
                    .getAuthentication().getPrincipal();
            if (principal instanceof UserPrincipal userPrincipal) {
                return userPrincipal.getUser();
            }
        } catch (Exception ignored) {}
        return null;
    }
}