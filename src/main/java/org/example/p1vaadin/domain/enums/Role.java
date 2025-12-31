package org.example.p1vaadin.domain.enums;

public enum Role {
    ADMIN("Administrateur", "error"),
    ORGANIZER("Organisateur", "success"),
    CLIENT("Client", "contrast");

    private final String label;
    private final String badgeVariant;

    Role(String label, String badgeVariant) {
        this.label = label;
        this.badgeVariant = badgeVariant;
    }

    public String getLabel() { return label; }
    public String getBadgeVariant() { return badgeVariant; }
}