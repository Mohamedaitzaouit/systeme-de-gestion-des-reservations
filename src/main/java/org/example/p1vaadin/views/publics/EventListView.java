package org.example.p1vaadin.views.publics;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component. button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin. flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow. component.icon.VaadinIcon;
import com.vaadin. flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com. vaadin.flow.component.textfield.TextField;
import com. vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow. server.auth.AnonymousAllowed;
import org.example. p1vaadin.domain.Event;
import org.example.p1vaadin.domain.enums.EventCategory;
import org. example.p1vaadin.service.EventService;
import org. example.p1vaadin.views.MainLayout;

import java. time.format.DateTimeFormatter;
import java.util.List;

@Route(value = "events", layout = MainLayout.class)
@PageTitle("Événements | Event Booking")
@AnonymousAllowed
public class EventListView extends VerticalLayout {

    private final EventService eventService;
    private final Grid<Event> grid = new Grid<>(Event.class, false);
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy");

    private final ComboBox<EventCategory> categoryFilter = new ComboBox<>("Catégorie");
    private final TextField villeFilter = new TextField("Ville");
    private final TextField keywordFilter = new TextField("Rechercher");
    private final NumberField minPriceFilter = new NumberField("Prix min");
    private final NumberField maxPriceFilter = new NumberField("Prix max");

    private static final String FONT_FAMILY = "-apple-system, BlinkMacSystemFont, 'Segoe UI', 'Inter', sans-serif";

    public EventListView(EventService eventService) {
        this.eventService = eventService;

        setSizeFull();
        setPadding(false);
        setSpacing(false);
        getStyle()
                .set("background", "var(--bg-gradient)")
                .set("font-family", FONT_FAMILY);

        Div container = new Div();
        container.getStyle()
                .set("max-width", "1400px")
                .set("width", "100%")
                .set("margin", "0 auto")
                .set("padding", "40px 24px");

        container. add(createHeader());
        container.add(createFiltersSection());
        container.add(createGridSection());

        add(container);
        refreshGrid();
    }

    private VerticalLayout createHeader() {
        VerticalLayout header = new VerticalLayout();
        header.setPadding(false);
        header.setSpacing(false);
        header.getStyle().set("margin-bottom", "32px");

        H1 title = new H1("Tous les événements");
        title.getStyle()
                .set("font-size", "2rem")
                .set("font-weight", "800")
                .set("color", "var(--text-primary)")
                .set("margin", "0 0 8px 0")
                .set("letter-spacing", "-0.5px");

        Span subtitle = new Span("Découvrez et filtrez parmi notre sélection d'événements");
        subtitle.getStyle()
                .set("font-size", "1rem")
                .set("color", "var(--text-secondary)");

        header.add(title, subtitle);
        return header;
    }

    private Div createFiltersSection() {
        Div filtersContainer = new Div();
        filtersContainer. getStyle()
                .set("background", "var(--card-bg)")
                .set("border", "1px solid var(--border-color)")
                .set("border-radius", "16px")
                .set("padding", "24px")
                .set("margin-bottom", "24px")
                .set("box-shadow", "var(--shadow-sm)");

        // Configure filters
        categoryFilter.setItems(EventCategory.values());
        categoryFilter. setItemLabelGenerator(EventCategory::getLabel);
        categoryFilter.setClearButtonVisible(true);
        categoryFilter.setPlaceholder("Toutes");
        styleField(categoryFilter);

        villeFilter.setClearButtonVisible(true);
        villeFilter.setPlaceholder("Toutes les villes");
        villeFilter.setPrefixComponent(new Icon(VaadinIcon. LOCATION_ARROW));
        styleField(villeFilter);

        keywordFilter. setClearButtonVisible(true);
        keywordFilter.setPlaceholder("Titre de l'événement.. .");
        keywordFilter.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        styleField(keywordFilter);

        minPriceFilter. setMin(0);
        minPriceFilter.setPlaceholder("0");
        Span minSuffix = new Span("DH");
        minSuffix.getStyle().set("color", "var(--text-secondary)");
        minPriceFilter.setSuffixComponent(minSuffix);
        styleField(minPriceFilter);

        maxPriceFilter.setMin(0);
        maxPriceFilter.setPlaceholder("∞");
        Span maxSuffix = new Span("DH");
        maxSuffix.getStyle().set("color", "var(--text-secondary)");
        maxPriceFilter.setSuffixComponent(maxSuffix);
        styleField(maxPriceFilter);

        // Buttons
        Button searchBtn = new Button("Rechercher");
        searchBtn.setIcon(new Icon(VaadinIcon.SEARCH));
        searchBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        searchBtn.getStyle()
                .set("background", "var(--primary-color)")
                .set("border-radius", "10px")
                .set("font-weight", "600")
                .set("padding", "10px 20px");
        searchBtn.addClickListener(e -> refreshGrid());

        Button resetBtn = new Button("Réinitialiser");
        resetBtn.setIcon(new Icon(VaadinIcon. REFRESH));
        resetBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        resetBtn.getStyle()
                .set("border-radius", "10px")
                .set("font-weight", "600")
                .set("color", "var(--text-secondary)");
        resetBtn.addClickListener(e -> {
            categoryFilter.clear();
            villeFilter.clear();
            keywordFilter.clear();
            minPriceFilter.clear();
            maxPriceFilter.clear();
            refreshGrid();
        });

        // Layout
        HorizontalLayout row1 = new HorizontalLayout(categoryFilter, villeFilter, keywordFilter);
        row1.setWidthFull();
        row1.setFlexGrow(1, keywordFilter);
        row1.getStyle().set("gap", "16px").set("margin-bottom", "16px");

        HorizontalLayout row2 = new HorizontalLayout(minPriceFilter, maxPriceFilter, searchBtn, resetBtn);
        row2.setAlignItems(Alignment.END);
        row2.getStyle().set("gap", "16px");

        VerticalLayout filtersLayout = new VerticalLayout(row1, row2);
        filtersLayout.setPadding(false);
        filtersLayout.setSpacing(false);

        filtersContainer.add(filtersLayout);
        return filtersContainer;
    }

    private void styleField(com.vaadin.flow.component. Component field) {
        field.getElement().getStyle()
                .set("border-radius", "10px")
                .set("font-family", FONT_FAMILY);
    }

    private Div createGridSection() {
        Div gridContainer = new Div();
        gridContainer.getStyle()
                .set("background", "var(--card-bg)")
                .set("border", "1px solid var(--border-color)")
                .set("border-radius", "16px")
                .set("overflow", "hidden")
                .set("box-shadow", "var(--shadow-sm)");

        createGrid();
        gridContainer.add(grid);
        return gridContainer;
    }

    private void createGrid() {
        // Category column
        grid.addComponentColumn(event -> {
            Span badge = new Span(event.getCategorie().getIcon() + " " + event.getCategorie().getLabel());
            badge.getStyle()
                    .set("background", "var(--bg-secondary)")
                    .set("color", "var(--text-primary)")
                    .set("padding", "4px 10px")
                    .set("border-radius", "6px")
                    .set("font-size", "0.8125rem")
                    .set("font-weight", "600");
            return badge;
        }).setHeader("Catégorie").setAutoWidth(true).setFlexGrow(0);

        // Title column
        grid.addComponentColumn(event -> {
            Span title = new Span(event. getTitre());
            title. getStyle()
                    .set("color", "var(--text-primary)")
                    .set("font-weight", "600");
            return title;
        }).setHeader("Titre").setAutoWidth(true).setFlexGrow(1);

        // Date column
        grid.addComponentColumn(event -> {
            Span date = new Span(event. getDateDebut().format(formatter));
            date.getStyle().set("color", "var(--text-secondary)");
            return date;
        }).setHeader("Date").setAutoWidth(true).setFlexGrow(0);

        // Location column
        grid.addComponentColumn(event -> {
            Div location = new Div();
            location.getStyle()
                    .set("display", "flex")
                    . set("align-items", "center")
                    .set("gap", "6px")
                    .set("color", "var(--text-secondary)");
            Icon icon = new Icon(VaadinIcon.LOCATION_ARROW);
            icon.setSize("14px");
            icon.getStyle().set("color", "var(--text-secondary)");
            Span ville = new Span(event.getVille());
            location.add(icon, ville);
            return location;
        }).setHeader("Ville").setAutoWidth(true).setFlexGrow(0);

        // Price column
        grid.addComponentColumn(event -> {
            Span price = new Span(event.getPrixUnitaire() + " DH");
            price.getStyle()
                    .set("font-weight", "700")
                    .set("color", "var(--primary-color)");
            return price;
        }).setHeader("Prix").setAutoWidth(true).setFlexGrow(0);

        // Seats column
        grid.addComponentColumn(event -> {
            int available = eventService.getAvailableSeats(event.getId());
            Span seats = new Span(available + " / " + event. getCapaciteMax());
            if (available == 0) {
                seats.getStyle().set("color", "var(--error-color)");
            } else if (available < 10) {
                seats.getStyle().set("color", "var(--warning-color)");
            } else {
                seats.getStyle().set("color", "var(--success-color)");
            }
            seats.getStyle().set("font-weight", "600");
            return seats;
        }).setHeader("Places").setAutoWidth(true).setFlexGrow(0);

        // Actions column
        grid.addComponentColumn(event -> {
            Button btn = new Button("Voir détails");
            btn.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_PRIMARY);
            btn.getStyle()
                    .set("background", "var(--primary-color)")
                    .set("border-radius", "8px")
                    . set("font-weight", "600");
            btn.addClickListener(e ->
                    getUI().ifPresent(ui -> ui.navigate(EventDetailView.class, event.getId()))
            );
            return btn;
        }).setHeader("Actions").setAutoWidth(true).setFlexGrow(0);

        // Grid styling
        grid.setAllRowsVisible(true);
        grid.getStyle()
                .set("border", "none")
                .set("font-family", FONT_FAMILY);

        grid.getElement().getStyle()
                .set("--lumo-font-family", FONT_FAMILY);

        // Apply theme-aware styles to grid
        applyGridThemeStyles();
    }

    private void applyGridThemeStyles() {
        grid.getElement().executeJs(
                "this.style.setProperty('--lumo-base-color', 'var(--card-bg)');" +
                        "this.style. setProperty('--lumo-body-text-color', 'var(--text-primary)');" +
                        "this.style.setProperty('--lumo-secondary-text-color', 'var(--text-secondary)');" +
                        "this.style.setProperty('--lumo-contrast-5pct', 'var(--bg-secondary)');" +
                        "this.style.setProperty('--lumo-contrast-10pct', 'var(--border-color)');"
        );
    }

    private void refreshGrid() {
        List<Event> events = eventService. search(
                categoryFilter.getValue(),
                villeFilter.getValue(),
                keywordFilter.getValue(),
                minPriceFilter.getValue(),
                maxPriceFilter.getValue()
        );
        grid.setItems(events);
    }
}