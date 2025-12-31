package org.example.p1vaadin.service;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.stereotype.Service;

@Service
public class ThemeService {

    private static final String THEME_ATTRIBUTE = "user-theme";
    private static final String DARK_THEME = "dark";
    private static final String LIGHT_THEME = "light";

    public void toggleTheme() {
        String currentTheme = getCurrentTheme();
        String newTheme = DARK_THEME. equals(currentTheme) ? LIGHT_THEME : DARK_THEME;
        setTheme(newTheme);
    }

    public void setTheme(String theme) {
        VaadinSession.getCurrent().setAttribute(THEME_ATTRIBUTE, theme);
        applyTheme(theme);
    }

    public String getCurrentTheme() {
        String theme = (String) VaadinSession.getCurrent().getAttribute(THEME_ATTRIBUTE);
        return theme != null ? theme : LIGHT_THEME;
    }

    public boolean isDarkMode() {
        return DARK_THEME.equals(getCurrentTheme());
    }

    private void applyTheme(String theme) {
        UI.getCurrent().getPage().executeJs(
                "document.documentElement.setAttribute('theme', $0);" +
                        "localStorage.setItem('app-theme', $0);" +

                        // Force le re-render de tous les styles inline
                        "document.querySelectorAll('*').forEach(el => {" +
                        "  if (el.style. cssText) {" +
                        "    const style = el.style. cssText;" +
                        "    el.style.cssText = '';" +
                        "    setTimeout(() => el.style.cssText = style, 0);" +
                        "  }" +
                        "});",
                theme
        );
    }

    public void initializeTheme() {
        UI.getCurrent().getPage().executeJs(
                "const savedTheme = localStorage.getItem('app-theme') || 'light';" +
                        "document.documentElement.setAttribute('theme', savedTheme);" +
                        "return savedTheme;"
        ).then(String. class, theme -> {
            VaadinSession.getCurrent().setAttribute(THEME_ATTRIBUTE, theme);
        });
    }
}