package org.example.p1vaadin.views.auth;

import com.vaadin.flow.component. button.Button;
import com. vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component. html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin. flow.component.icon. VaadinIcon;
import com.vaadin.flow.component. notification.Notification;
import com.vaadin.flow.component. notification.NotificationVariant;
import com.vaadin.flow. component.orderedlayout. VerticalLayout;
import com. vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component. textfield.PasswordField;
import com.vaadin.flow. component.textfield.TextField;
import com.vaadin.flow. router.PageTitle;
import com.vaadin.flow.router. Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.example.p1vaadin. domain.User;
import org. example.p1vaadin.domain.enums.Role;
import org.example.p1vaadin.service.UserService;

@Route("register")
@PageTitle("Inscription | Event Booking")
@AnonymousAllowed
public class RegisterView extends VerticalLayout {

    private final UserService userService;

    private final TextField nom = new TextField("Nom");
    private final TextField prenom = new TextField("Prénom");
    private final EmailField email = new EmailField("Email");
    private final PasswordField password = new PasswordField("Mot de passe");
    private final PasswordField confirmPassword = new PasswordField("Confirmer le mot de passe");
    private final TextField telephone = new TextField("Téléphone");

    private static final String FONT_FAMILY = "-apple-system, BlinkMacSystemFont, 'Segoe UI', 'Inter', sans-serif";
    private static final String PRIMARY_COLOR = "#2563eb";
    private static final String TEXT_PRIMARY = "#0f172a";
    private static final String TEXT_SECONDARY = "#64748b";
    private static final String BORDER_COLOR = "#e2e8f0";

    public RegisterView(UserService userService) {
        this.userService = userService;

        setSizeFull();
        setPadding(false);
        setSpacing(false);
        setAlignItems(Alignment. CENTER);
        setJustifyContentMode(JustifyContentMode. CENTER);
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
        H1 title = new H1("Créer un compte");
        title.getStyle()
                .set("font-size", "1.75rem")
                .set("font-weight", "800")
                .set("color", TEXT_PRIMARY)
                .set("margin", "0 0 8px 0")
                .set("text-align", "center")
                .set("letter-spacing", "-0.5px");

        // Subtitle
        Paragraph subtitle = new Paragraph("Rejoignez Event Booking dès aujourd'hui");
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
        configureTextField(nom, VaadinIcon. USER, true);
        configureTextField(prenom, VaadinIcon.USER, true);
        configureEmailField(email, true);
        configurePasswordField(password, true);
        configurePasswordField(confirmPassword, false);
        configureTextField(telephone, VaadinIcon.PHONE, false);

        telephone.setHelperText("Optionnel");
        password.setHelperText("Minimum 8 caractères");

        form.add(nom, prenom, email, password, confirmPassword, telephone);

        // Register button
        Button registerBtn = new Button("Créer mon compte");
        registerBtn.setIcon(new Icon(VaadinIcon.USER_CHECK));
        registerBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE);
        registerBtn.setWidthFull();
        registerBtn.getStyle()
                .set("background", PRIMARY_COLOR)
                .set("border-radius", "10px")
                .set("font-weight", "700")
                .set("font-size", "1rem")
                .set("padding", "14px")
                .set("margin-top", "8px")
                .set("cursor", "pointer")
                .set("transition", "all 0.2s ease");

        registerBtn.getElement().setAttribute("onmouseover",
                "this.style.background='#1d4ed8'; this.style.transform='translateY(-1px)'; this.style.boxShadow='0 4px 12px rgba(37, 99, 235, 0.3)';");
        registerBtn.getElement().setAttribute("onmouseout",
                "this.style. background='" + PRIMARY_COLOR + "'; this.style.transform='translateY(0)'; this.style.boxShadow='none';");

        registerBtn.addClickListener(e -> register());

        // Login link container
        Div loginLinkContainer = new Div();
        loginLinkContainer.getStyle()
                .set("text-align", "center")
                .set("margin-top", "24px")
                .set("padding-top", "24px")
                .set("border-top", "1px solid " + BORDER_COLOR);

        Span linkText = new Span("Déjà un compte ?  ");
        linkText.getStyle()
                .set("color", TEXT_SECONDARY)
                .set("font-size", "0.9375rem");

        Anchor loginLink = new Anchor("/login", "Connectez-vous");
        loginLink. getStyle()
                .set("color", PRIMARY_COLOR)
                .set("font-weight", "700")
                .set("text-decoration", "none")
                .set("font-size", "0.9375rem");

        loginLinkContainer.add(linkText, loginLink);

        container.add(logoIcon, title, subtitle, form, registerBtn, loginLinkContainer);
        add(container);
    }

    private void configureTextField(TextField field, VaadinIcon icon, boolean required) {
        field.setRequired(required);
        field.setWidthFull();
        field.setPrefixComponent(new Icon(icon));
        field.getStyle()
                .set("--vaadin-input-field-border-radius", "10px");
    }

    private void configureEmailField(EmailField field, boolean required) {
        field.setRequired(required);
        field.setWidthFull();
        field.setPrefixComponent(new Icon(VaadinIcon. ENVELOPE));
        field.getStyle()
                .set("--vaadin-input-field-border-radius", "10px");
        field.setErrorMessage("Veuillez entrer une adresse email valide");
    }

    private void configurePasswordField(PasswordField field, boolean isMainPassword) {
        field.setRequired(true);
        field.setWidthFull();
        field.setPrefixComponent(new Icon(VaadinIcon. LOCK));
        field.getStyle()
                .set("--vaadin-input-field-border-radius", "10px");

        if (isMainPassword) {
            field. setMinLength(8);
        }
    }

    private void register() {
        // Validation
        if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showNotification("Veuillez remplir tous les champs obligatoires", NotificationVariant.LUMO_ERROR);
            return;
        }

        if (!password.getValue().equals(confirmPassword.getValue())) {
            showNotification("Les mots de passe ne correspondent pas", NotificationVariant.LUMO_ERROR);
            confirmPassword.setInvalid(true);
            confirmPassword.setErrorMessage("Les mots de passe ne correspondent pas");
            return;
        }

        if (password. getValue().length() < 8) {
            showNotification("Le mot de passe doit contenir au moins 8 caractères", NotificationVariant.LUMO_ERROR);
            password.setInvalid(true);
            return;
        }

        // Email validation
        if (!email. getValue().contains("@")) {
            showNotification("Veuillez entrer une adresse email valide", NotificationVariant. LUMO_ERROR);
            email.setInvalid(true);
            return;
        }

        try {
            User user = User.builder()
                    .nom(nom. getValue().trim())
                    .prenom(prenom.getValue().trim())
                    .email(email. getValue().trim().toLowerCase())
                    .password(password.getValue())
                    .telephone(telephone.getValue() != null && ! telephone.isEmpty() ? telephone.getValue().trim() : null)
                    .role(Role.CLIENT)
                    .build();

            userService.register(user);

            showNotification("✅ Inscription réussie !  Vous pouvez maintenant vous connecter.", NotificationVariant. LUMO_SUCCESS);

            // Redirect to login after 1 second
            getUI().ifPresent(ui ->
                    ui.getPage().executeJs("setTimeout(() => window.location.href = '/login', 1000)")
            );

        } catch (Exception ex) {
            showNotification("❌ " + ex.getMessage(), NotificationVariant. LUMO_ERROR);
        }
    }

    private void showNotification(String message, NotificationVariant variant) {
        Notification notification = new Notification();
        notification.addThemeVariants(variant);
        notification. setDuration(4000);
        notification.setPosition(Notification.Position.TOP_CENTER);

        Div text = new Div();
        text.setText(message);
        text.getStyle()
                .set("font-weight", "600")
                .set("font-size", "0.9375rem");

        notification. add(text);
        notification. open();
    }
}