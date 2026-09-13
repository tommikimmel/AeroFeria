package com.aeroferia.api.entity.enums;

public enum PublicationStatus {
    PENDING,        // Pendiente de moderación
    ACTIVE,         // Activa y visible en catálogo
    REJECTED,       // Rechazada por moderador
    SOLD,           // Marcada como vendida
    PAUSED,         // Pausada temporalmente por vendedor
    DELETED         // Eliminada
}
