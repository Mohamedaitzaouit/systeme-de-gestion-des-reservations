package org.example.p1vaadin.views.auth;

import com.vaadin.flow.component. button.Button;
import com. vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component. html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin. flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification. Notification;
import com.vaadin.flow.component.notification. NotificationVariant;
import com.vaadin.flow.component. orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield. EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.router. BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth. AnonymousAllowed;
import org.example.p1vaadin.service.UserService;

@Route("login")
@PageTitle("Connexion | Event Booking")
@AnonymousAllowed
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

    private final UserService userService;

    private final EmailField email = new EmailField("Email");
    private final PasswordField password = new PasswordField("Mot de passe");

    private static final String FONT_FAMILY = "-apple-system, BlinkMacSystemFont, 'Segoe UI', 'Inter', sans-serif";
    private static final String PRIMARY_COLOR = "#2563eb";
    private static final String TEXT_PRIMARY = "#0f172a";
    private static final String TEXT_SECONDARY = "#64748b";
    private static final String BORDER_COLOR = "#e2e8f0";

    public LoginView(UserService userService) {
        this.userService = userService;

        setSizeFull();
        setPadding(false);
        setSpacing(false);
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        getStyle()
                .set("background", "linear-gradient(135deg, #dbeafe 0%, #e0e7ff 100%)")
                .set("font-family", FONT_FAMILY)
                .set("padding", "40px 20px");

        // Container card
        Div container = new Div();
        container.getStyle()
                .set("background", "#ffffff")
                .set("border", "1px solid " + BORDER_COLOR)
                .set("border-radius", "20px")
                .set("padding", "48px")
                .set("max-width", "480px")
                .set("width", "100%")
                .set("box-shadow", "0 20px 60px rgba(0, 0, 0, 0.08)");

        // Logo/Icon
        Span logoIcon = new Span("🎭");
        logoIcon.getStyle()
                .set("font-size", "3.5rem")
                .set("display", "block")
                .set("text-align", "center")
                .set("margin-bottom", "16px");

        // Title
        H1 title = new H1("Bon retour !");
        title.getStyle()
                .set("font-size", "1.75rem")
                .set("font-weight", "800")
                .set("color", TEXT_PRIMARY)
                .set("margin", "0 0 8px 0")
                .set("text-align", "center")
                .set("letter-spacing", "-0.5px");

        // Subtitle
        Paragraph subtitle = new Paragraph("Connectez-vous à votre compte Event Booking");
        subtitle.getStyle()
                .set("font-size", "0.9375rem")
                .set("color", TEXT_SECONDARY)
                .set("margin", "0 0 32px 0")
                .set("text-align", "center")
                .set("line-height", "1.5");

        // Form fields
        VerticalLayout form = new VerticalLayout();
        form.setPadding(false);
        form.setSpacing(false);
        form.setWidthFull();
        form.getStyle().set("gap", "16px");

        // Configure fields
        configureEmailField(email);
        configurePasswordField(password);

        form.add(email, password);

        // Login button
        Button loginBtn = new Button("Se connecter");
        loginBtn.setIcon(new Icon(VaadinIcon.SIGN_IN));
        loginBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant. LUMO_LARGE);
        loginBtn.setWidthFull();
        loginBtn.getStyle()
                .set("background", PRIMARY_COLOR)
                .set("border-radius", "10px")
                .set("font-weight", "700")
                .set("font-size", "1rem")
                .set("padding", "14px")
                .set("margin-top", "8px")
                .set("cursor", "pointer")
                .set("transition", "all 0.2s ease");

        loginBtn.getElement().setAttribute("onmouseover",
                "this. style.background='#1d4ed8'; this.style.transform='translateY(-1px)'; this.style.boxShadow='0 4px 12px rgba(37, 99, 235, 0.3)';");
        loginBtn.getElement().setAttribute("onmouseout",
                "this.style.background='" + PRIMARY_COLOR + "'; this.style.transform='translateY(0)'; this.style.boxShadow='none';");

        loginBtn.addClickListener(e -> login());

        // Register link container
        Div registerLinkContainer = new Div();
        registerLinkContainer. getStyle()
                .set("text-align", "center")
                .set("margin-top", "24px")
                .set("padding-top", "24px")
                .set("border-top", "1px solid " + BORDER_COLOR);

        Span linkText = new Span("Pas encore de compte ? ");
        linkText.getStyle()
                .set("color", TEXT_SECONDARY)
                .set("font-size", "0.9375rem");

        Anchor registerLink = new Anchor("/register", "Inscrivez-vous");
        registerLink.getStyle()
                .set("color", PRIMARY_COLOR)
                .set("font-weight", "700")
                .set("text-decoration", "none")
                .set("font-size", "0.9375rem");

        registerLinkContainer.add(linkText, registerLink);

        container.add(logoIcon, title, subtitle, form, loginBtn, registerLinkContainer);
        add(container);
    }

    private void configureEmailField(EmailField field) {
        field.setRequired(true);
        field.setWidthFull();
        field.setPrefixComponent(new Icon(VaadinIcon.ENVELOPE));
        field.getStyle()
                .set("--vaadin-input-field-border-radius", "10px");
        field.setErrorMessage("Veuillez entrer une adresse email valide");
    }

    private void configurePasswordField(PasswordField field) {
        field.setRequired(true);
        field.setWidthFull();
        field.setPrefixComponent(new Icon(VaadinIcon.LOCK));
        field.getStyle()
                .set("--vaadin-input-field-border-radius", "10px");
    }

    private void login() {
        // Validation
        if (email.isEmpty() || password.isEmpty()) {
            showNotification("Veuillez remplir tous les champs", NotificationVariant.LUMO_ERROR);
            return;
        }

        if (!email.getValue().contains("@")) {
            showNotification("Veuillez entrer une adresse email valide", NotificationVariant.LUMO_ERROR);
            email.setInvalid(true);
            return;
        }

        try {
            // Rediriger vers Spring Security pour l'authentification
            // Spring Security gère automatiquement la connexion via le formulaire
            getUI().ifPresent(ui -> {
                // Utiliser l'action du formulaire Spring Security
                ui.getPage().executeJs(
                        "const form = document.createElement('form');" +
                                "form. method = 'POST';" +
                                "form.action = '/login';" +
                                "const emailInput = document.createElement('input');" +
                                "emailInput.type = 'hidden';" +
                                "emailInput.name = 'username';" +
                                "emailInput.value = $0;" +
                                "const passwordInput = document.createElement('input');" +
                                "passwordInput.type = 'hidden';" +
                                "passwordInput.name = 'password';" +
                                "passwordInput. value = $1;" +
                                "form. appendChild(emailInput);" +
                                "form.appendChild(passwordInput);" +
                                "document.body.appendChild(form);" +
                                "form.submit();",
                        email.getValue().trim().toLowerCase(),
                        password.getValue()
                );
            });

        } catch (Exception ex) {
            showNotification("❌ " + ex.getMessage(), NotificationVariant.LUMO_ERROR);
        }
    }

    private void showNotification(String message, NotificationVariant variant) {
        Notification notification = new Notification();
        notification.addThemeVariants(variant);
        notification.setDuration(4000);
        notification.setPosition(Notification.Position.TOP_CENTER);

        Div text = new Div();
        text.setText(message);
        text.getStyle()
                .set("font-weight", "600")
                .set("font-size", "0.9375rem");

        notification.add(text);
        notification.open();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (event.getLocation().getQueryParameters().getParameters().containsKey("error")) {
            showNotification("❌ Email ou mot de passe incorrect", NotificationVariant.LUMO_ERROR);
        }
    }
}