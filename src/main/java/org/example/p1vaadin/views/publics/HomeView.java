package org.example.p1vaadin.views.publics;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.example.p1vaadin.domain.Event;
import org.example.p1vaadin.service.EventService;
import org.example.p1vaadin.views.MainLayout;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Route(value = "", layout = MainLayout.class)
@PageTitle("Accueil | Event Booking")
@AnonymousAllowed
public class HomeView extends VerticalLayout {

    private final EventService eventService;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy");

    private static final String FONT_FAMILY = "-apple-system, BlinkMacSystemFont, 'Segoe UI', 'Inter', 'Roboto', sans-serif";

    private static final Map<String, CategoryStyle> CATEGORY_STYLES = Map.of(
            "CONCERT", new CategoryStyle("linear-gradient(135deg, #667eea 0%, #764ba2 100%)", "#667eea", "🎵"),
            "THEATRE", new CategoryStyle("linear-gradient(135deg, #f093fb 0%, #f5576c 100%)", "#f093fb", "🎭"),
            "CONFERENCE", new CategoryStyle("linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)", "#4facfe", "💼"),
            "SPORT", new CategoryStyle("linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)", "#43e97b", "⚽"),
            "AUTRE", new CategoryStyle("linear-gradient(135deg, #fa709a 0%, #fee140 100%)", "#fa709a", "🎪")
    );

    private record CategoryStyle(String gradient, String accentColor, String icon) {}

    public HomeView(EventService eventService) {
        this.eventService = eventService;
        setSizeFull();
        setPadding(false);
        setSpacing(false);

        getStyle()
                .set("background", "var(--lumo-base-color)")
                .set("font-family", FONT_FAMILY)
                .set("min-height", "100vh")
                .set("overflow-x", "hidden");

        add(
                createFloatingBackgroundElements(),
                createHeroSection(),
                createFeaturedSection(),
                createFooter()
        );

        injectCustomStyles();
    }

    private Div createFloatingBackgroundElements() {
        Div bgContainer = new Div();
        bgContainer.getStyle()
                .set("position", "fixed")
                .set("top", "0")
                .set("left", "0")
                .set("width", "100vw")
                .set("height", "100vh")
                .set("pointer-events", "none")
                .set("z-index", "0")
                .set("overflow", "hidden");

        // Orbes flottants adaptés au thème
        for (int i = 0; i < 3; i++) {
            Div orb = new Div();
            orb.addClassName("floating-orb-" + i);
            orb.getStyle()
                    .set("position", "absolute")
                    .set("border-radius", "50%")
                    .set("filter", "blur(80px)")
                    .set("opacity", "0.4")
                    .set("animation", "float-orb " + (20 + i * 5) + "s infinite ease-in-out alternate");

            if (i == 0) {
                orb.getStyle()
                        .set("width", "500px").set("height", "500px")
                        .set("top", "-150px").set("right", "-100px")
                        .set("background", "radial-gradient(circle, #667eea, transparent 70%)");
            } else if (i == 1) {
                orb.getStyle()
                        .set("width", "400px").set("height", "400px")
                        .set("bottom", "-100px").set("left", "-80px")
                        .set("background", "radial-gradient(circle, #f093fb, transparent 70%)");
            } else {
                orb.getStyle()
                        .set("width", "450px").set("height", "450px")
                        .set("top", "30%").set("left", "50%")
                        .set("transform", "translateX(-50%)")
                        .set("background", "radial-gradient(circle, #4facfe, transparent 70%)");
            }

            bgContainer.add(orb);
        }

        return bgContainer;
    }

    private VerticalLayout createHeroSection() {
        VerticalLayout hero = new VerticalLayout();
        hero.setPadding(false);
        hero.setSpacing(false);
        hero.setAlignItems(Alignment.CENTER);

        hero.getStyle()
                .set("position", "relative")
                .set("z-index", "1")
                .set("padding", "80px 24px 60px 24px")
                .set("text-align", "center")
                .set("max-width", "1200px")
                .set("margin", "0 auto")
                .set("width", "100%");

        // Badge supérieur avec thème adaptatif
        Div badge = new Div();
        badge.addClassName("hero-badge");
        badge.getStyle()
                .set("display", "inline-flex")
                .set("align-items", "center")
                .set("gap", "8px")
                .set("padding", "10px 24px")
                .set("border-radius", "50px")
                .set("margin-bottom", "32px")
                .set("animation", "fade-in-up 0.6s ease-out");

        Span sparkle = new Span("✨");
        sparkle.getStyle().set("font-size", "1.2rem");

        Span badgeText = new Span("Nouvelle plateforme événementielle");
        badgeText.getStyle()
                .set("font-weight", "600")
                .set("font-size", "0.95rem");

        badge.add(sparkle, badgeText);

        // Titre principal
        H1 mainTitle = new H1("Découvrez l'événement");
        mainTitle.getStyle()
                .set("font-size", "clamp(2.5rem, 5vw, 4rem)")
                .set("font-weight", "900")
                .set("margin", "0")
                .set("line-height", "1.1")
                .set("color", "var(--lumo-header-text-color)")
                .set("animation", "fade-in-up 0.7s ease-out 0.1s backwards");

        // Titre avec gradient - VERSION CORRIGÉE
        H1 gradientTitle = new H1("qui vous passionne");
        gradientTitle.addClassName("gradient-text");
        gradientTitle.getStyle()
                .set("font-size", "clamp(2.5rem, 5vw, 4rem)")
                .set("font-weight", "900")
                .set("margin", "0 0 24px 0")
                .set("line-height", "1.1")
                .set("animation", "fade-in-up 0.8s ease-out 0.2s backwards");

        // Sous-titre
        Paragraph subtitle = new Paragraph("Concerts, théâtres, conférences, événements sportifs et bien plus. Réservez en quelques clics et vivez des expériences inoubliables.");
        subtitle.getStyle()
                .set("font-size", "1.2rem")
                .set("color", "var(--lumo-secondary-text-color)")
                .set("max-width", "700px")
                .set("margin", "0 auto 40px auto")
                .set("line-height", "1.6")
                .set("font-weight", "400")
                .set("animation", "fade-in-up 0.9s ease-out 0.3s backwards");

        // Boutons CTA
        HorizontalLayout ctaButtons = new HorizontalLayout();
        ctaButtons.setSpacing(true);
        ctaButtons.getStyle()
                .set("gap", "16px")
                .set("justify-content", "center")
                .set("flex-wrap", "wrap")
                .set("animation", "fade-in-up 1s ease-out 0.4s backwards");

        Button primaryCta = new Button("Explorer les événements", new Icon(VaadinIcon.ARROW_RIGHT));
        primaryCta.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE);
        primaryCta.setIconAfterText(true);
        primaryCta.addClassName("primary-cta-btn");
        primaryCta.getStyle()
                .set("border-radius", "16px")
                .set("padding", "16px 32px")
                .set("font-weight", "700")
                .set("font-size", "1.05rem")
                .set("cursor", "pointer");

        primaryCta.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("events")));

        Button secondaryCta = new Button("En savoir plus", new Icon(VaadinIcon.INFO_CIRCLE));
        secondaryCta.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_LARGE);
        secondaryCta.getStyle()
                .set("border-radius", "16px")
                .set("padding", "16px 32px")
                .set("font-weight", "600")
                .set("font-size", "1.05rem")
                .set("transition", "all 0.3s ease");

        ctaButtons.add(primaryCta, secondaryCta);

        hero.add(badge, mainTitle, gradientTitle, subtitle, ctaButtons);
        return hero;
    }

    private VerticalLayout createFeaturedSection() {
        VerticalLayout section = new VerticalLayout();
        section.setPadding(false);
        section.setSpacing(false);

        section.getStyle()
                .set("position", "relative")
                .set("z-index", "1")
                .set("padding", "60px 24px")
                .set("max-width", "1400px")
                .set("margin", "0 auto")
                .set("width", "100%");

        // En-tête de section
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        header.setAlignItems(Alignment.CENTER);
        header.getStyle()
                .set("margin-bottom", "40px")
                .set("flex-wrap", "wrap")
                .set("gap", "16px");

        Div titleGroup = new Div();
        H2 sectionTitle = new H2("✨ Événements en vedette");
        sectionTitle.getStyle()
                .set("font-size", "clamp(1.8rem, 3vw, 2.5rem)")
                .set("font-weight", "900")
                .set("margin", "0 0 8px 0")
                .set("color", "var(--lumo-header-text-color)");

        Paragraph sectionSubtitle = new Paragraph("Les expériences les plus populaires du moment");
        sectionSubtitle.getStyle()
                .set("margin", "0")
                .set("color", "var(--lumo-secondary-text-color)")
                .set("font-size", "1.05rem");

        titleGroup.add(sectionTitle, sectionSubtitle);

        Button viewAllBtn = new Button("Voir tout", new Icon(VaadinIcon.ARROW_RIGHT));
        viewAllBtn.setIconAfterText(true);
        viewAllBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        viewAllBtn.getStyle()
                .set("font-weight", "700")
                .set("color", "var(--lumo-primary-text-color)")
                .set("padding", "12px 24px")
                .set("border-radius", "12px")
                .set("transition", "all 0.3s ease");
        viewAllBtn.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("events")));

        header.add(titleGroup, viewAllBtn);

        // Grille d'événements
        FlexLayout eventsGrid = new FlexLayout();
        eventsGrid.setWidthFull();
        eventsGrid.setFlexWrap(FlexLayout.FlexWrap.WRAP);
        eventsGrid.getStyle()
                .set("gap", "32px")
                .set("justify-content", "center");

        List<Event> events = eventService.findPopularEvents(6);
        int delay = 0;
        for (Event event : events) {
            eventsGrid.add(createEventCard(event, delay));
            delay += 80;
        }

        section.add(header, eventsGrid);
        return section;
    }

    private Div createEventCard(Event event, int delay) {
        CategoryStyle style = CATEGORY_STYLES.getOrDefault(
                event.getCategorie().name(),
                new CategoryStyle("linear-gradient(135deg, #667eea 0%, #764ba2 100%)", "#667eea", "🎫")
        );

        Div card = new Div();
        card.addClassName("event-card");
        card.getStyle()
                .set("flex", "1 1 350px")
                .set("min-width", "320px")
                .set("max-width", "400px")
                .set("background", "var(--lumo-base-color)")
                .set("border-radius", "24px")
                .set("overflow", "hidden")
                .set("cursor", "pointer")
                .set("animation", "fade-in-up 0.8s ease-out " + (0.6 + delay/1000.0) + "s backwards");

        // En-tête de carte avec dégradé
        Div cardHeader = new Div();
        cardHeader.getStyle()
                .set("background", style.gradient)
                .set("padding", "48px 24px 32px 24px")
                .set("position", "relative")
                .set("overflow", "hidden");

        // Badge catégorie
        Span categoryBadge = new Span(style.icon + " " + event.getCategorie().getLabel());
        categoryBadge.addClassName("category-badge");
        categoryBadge.getStyle()
                .set("display", "inline-block")
                .set("padding", "8px 18px")
                .set("border-radius", "12px")
                .set("font-weight", "800")
                .set("font-size", "0.85rem")
                .set("letter-spacing", "0.5px")
                .set("text-transform", "uppercase");

        cardHeader.add(categoryBadge);

        // Corps de carte
        Div cardBody = new Div();
        cardBody.getStyle()
                .set("padding", "28px 24px 24px 24px");

        H3 eventTitle = new H3(event.getTitre());
        eventTitle.getStyle()
                .set("font-size", "1.35rem")
                .set("font-weight", "700")
                .set("margin", "0 0 20px 0")
                .set("color", "var(--lumo-header-text-color)")
                .set("line-height", "1.3");

        // Informations de l'événement
        VerticalLayout infoContainer = new VerticalLayout();
        infoContainer.setPadding(false);
        infoContainer.setSpacing(false);
        infoContainer.getStyle()
                .set("gap", "12px")
                .set("margin-bottom", "24px");

        infoContainer.add(
                createInfoRow("📅", "Date", event.getDateDebut().format(dateFormatter)),
                createInfoRow("📍", "Lieu", event.getVille()),
                createInfoRow("💰", "Prix", event.getPrixUnitaire() + " DH")
        );

        // Bouton d'action
        Button actionButton = new Button("Réserver maintenant");
        actionButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        actionButton.addClassName("event-action-btn");
        actionButton.getElement().setAttribute("data-gradient", style.gradient);
        actionButton.getStyle()
                .set("width", "100%")
                .set("background", style.gradient)
                .set("border", "none")
                .set("border-radius", "14px")
                .set("padding", "14px")
                .set("font-weight", "700")
                .set("font-size", "1rem")
                .set("transition", "all 0.3s ease")
                .set("cursor", "pointer");

        actionButton.addClickListener(e ->
                getUI().ifPresent(ui -> ui.navigate(EventDetailView.class, event.getId()))
        );

        cardBody.add(eventTitle, infoContainer, actionButton);
        card.add(cardHeader, cardBody);

        return card;
    }

    private HorizontalLayout createInfoRow(String icon, String label, String value) {
        HorizontalLayout row = new HorizontalLayout();
        row.setSpacing(false);
        row.setAlignItems(Alignment.CENTER);
        row.getStyle()
                .set("gap", "12px");

        Span iconSpan = new Span(icon);
        iconSpan.getStyle()
                .set("font-size", "1.3rem")
                .set("line-height", "1");

        Div textContainer = new Div();

        Span labelSpan = new Span(label);
        labelSpan.getStyle()
                .set("font-size", "0.8rem")
                .set("color", "var(--lumo-secondary-text-color)")
                .set("font-weight", "600")
                .set("text-transform", "uppercase")
                .set("letter-spacing", "0.5px")
                .set("display", "block")
                .set("margin-bottom", "2px");

        Span valueSpan = new Span(value);
        valueSpan.getStyle()
                .set("font-size", "0.95rem")
                .set("color", "var(--lumo-body-text-color)")
                .set("font-weight", "600")
                .set("display", "block");

        textContainer.add(labelSpan, valueSpan);
        row.add(iconSpan, textContainer);

        return row;
    }

    private Div createFooter() {
        Div footer = new Div();
        footer.getStyle()
                .set("position", "relative")
                .set("z-index", "1")
                .set("text-align", "center")
                .set("padding", "60px 24px 40px 24px")
                .set("margin-top", "60px")
                .set("border-top", "1px solid var(--lumo-contrast-10pct)");

        Paragraph footerText = new Paragraph("Event Booking © 2024 — Conçu avec passion pour des expériences inoubliables 🎉");
        footerText.getStyle()
                .set("margin", "0")
                .set("color", "var(--lumo-secondary-text-color)")
                .set("font-size", "0.95rem")
                .set("font-weight", "500");

        footer.add(footerText);
        return footer;
    }

    private void injectCustomStyles() {
        getElement().executeJs(
                "const style = document.createElement('style');" +
                        "style.textContent = `" +
                        "/* Animations de base */" +
                        "@keyframes fade-in-up {" +
                        "  from { opacity: 0; transform: translateY(40px); }" +
                        "  to { opacity: 1; transform: translateY(0); }" +
                        "}" +
                        "@keyframes float-orb {" +
                        "  0%, 100% { transform: translate(0, 0) scale(1); }" +
                        "  25% { transform: translate(30px, -30px) scale(1.1); }" +
                        "  50% { transform: translate(-20px, 20px) scale(0.95); }" +
                        "  75% { transform: translate(20px, 30px) scale(1.05); }" +
                        "}" +
                        "" +
                        "/* Texte gradient - Compatible thème clair/sombre */" +
                        ".gradient-text {" +
                        "  background: linear-gradient(135deg, #667eea 0%, #764ba2 50%, #f093fb 100%);" +
                        "  -webkit-background-clip: text;" +
                        "  -webkit-text-fill-color: transparent;" +
                        "  background-clip: text;" +
                        "  display: inline-block !important;" +
                        "}" +
                        "" +
                        "/* Badge hero - Thème adaptatif */" +
                        ".hero-badge {" +
                        "  background: rgba(102, 126, 234, 0.1);" +
                        "  backdrop-filter: blur(10px);" +
                        "  border: 1px solid rgba(102, 126, 234, 0.2);" +
                        "  box-shadow: 0 4px 24px rgba(102, 126, 234, 0.15);" +
                        "  color: var(--lumo-body-text-color);" +
                        "}" +
                        "" +
                        "/* Bouton CTA principal */" +
                        ".primary-cta-btn {" +
                        "  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%) !important;" +
                        "  box-shadow: 0 8px 32px rgba(102, 126, 234, 0.35);" +
                        "  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);" +
                        "}" +
                        ".primary-cta-btn:hover {" +
                        "  transform: translateY(-4px) scale(1.02);" +
                        "  box-shadow: 0 16px 48px rgba(102, 126, 234, 0.5) !important;" +
                        "}" +
                        "" +
                        "/* Cards événements - Thème adaptatif */" +
                        ".event-card {" +
                        "  border: 1px solid var(--lumo-contrast-10pct);" +
                        "  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.08);" +
                        "  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);" +
                        "}" +
                        ".event-card:hover {" +
                        "  transform: translateY(-12px) scale(1.02);" +
                        "  box-shadow: 0 24px 64px rgba(102, 126, 234, 0.25);" +
                        "  border-color: #667eea;" +
                        "}" +
                        "" +
                        "/* Badge catégorie - Adaptatif thème */" +
                        ".category-badge {" +
                        "  background: rgba(255, 255, 255, 0.95) !important;" +
                        "  color: #1a202c !important;" +
                        "  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);" +
                        "}" +
                        "[class*='theme-dark'] .category-badge {" +
                        "  background: rgba(255, 255, 255, 0.9) !important;" +
                        "  color: #1a202c !important;" +
                        "}" +
                        "" +
                        "/* Boutons d'action événements */" +
                        ".event-action-btn {" +
                        "  box-shadow: 0 4px 16px rgba(102, 126, 234, 0.3);" +
                        "}" +
                        ".event-action-btn:hover {" +
                        "  transform: translateY(-2px);" +
                        "  box-shadow: 0 8px 24px rgba(102, 126, 234, 0.4) !important;" +
                        "}" +
                        "" +
                        "/* Orbes - Adaptés au thème */" +
                        "[class*='theme-dark'] .floating-orb-0," +
                        "[class*='theme-dark'] .floating-orb-1," +
                        "[class*='theme-dark'] .floating-orb-2 {" +
                        "  opacity: 0.2 !important;" +
                        "}" +
                        "" +
                        "/* Responsive */" +
                        "@media (max-width: 768px) {" +
                        "  .gradient-text { font-size: 2rem !important; }" +
                        "}" +
                        "`;" +
                        "document.head.appendChild(style);"
        );
    }
}