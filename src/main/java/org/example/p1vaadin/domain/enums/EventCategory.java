package org.example.p1vaadin.domain.enums;

public enum EventCategory {
    CONCERT("Concert", "🎵"),
    THEATRE("Théâtre", "🎭"),
    CONFERENCE("Conférence", "🎤"),
    SPORT("Sport", "⚽"),
    AUTRE("Autre", "📌");

    private final String label;
    private final String icon;

    EventCategory(String label, String icon) {
        this.label = label;
        this.icon = icon;
    }

    public String getLabel() { return label; }
    public String getIcon() { return icon; }
}