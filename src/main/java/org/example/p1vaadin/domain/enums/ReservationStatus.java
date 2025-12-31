package org.example.p1vaadin.domain.enums;

public enum ReservationStatus {
    EN_ATTENTE("En attente", "contrast"),
    CONFIRMEE("Confirmée", "success"),
    ANNULEE("Annulée", "error");

    private final String label;
    private final String badgeVariant;

    ReservationStatus(String label, String badgeVariant) {
        this.label = label;
        this.badgeVariant = badgeVariant;
    }

    public String getLabel() { return label; }
    public String getBadgeVariant() { return badgeVariant; }
}