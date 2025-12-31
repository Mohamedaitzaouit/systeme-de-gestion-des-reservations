package org.example.p1vaadin.domain. enums;

public enum EventStatus {
    BROUILLON("Brouillon", "contrast"),
    PUBLIE("Publié", "success"),
    ANNULE("Annulé", "error"),
    TERMINE("Terminé", "primary");

    private final String label;
    private final String badgeVariant;

    EventStatus(String label, String badgeVariant) {
        this.label = label;
        this.badgeVariant = badgeVariant;
    }

    public String getLabel() { return label; }
    public String getBadgeVariant() { return badgeVariant; }
}