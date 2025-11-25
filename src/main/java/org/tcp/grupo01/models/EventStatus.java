package org.tcp.grupo01.models;

public enum EventStatus {
    PLANNING("Planejando"),
    RUNNING("Em andamento"),
    FINISHED("Finalizado");

    private final String description;

    EventStatus(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }
}