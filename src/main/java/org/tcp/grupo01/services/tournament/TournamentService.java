package org.tcp.grupo01.services.tournament;

import org.tcp.grupo01.models.EventStatus;
import org.tcp.grupo01.models.Tournament;

import java.util.List;
import java.util.UUID;


public interface TournamentService {
    List<Tournament<?>> getAll();
    UUID add(Tournament<?> tournament);
    Tournament<?> getById(UUID id);
    void delete(UUID id);
    void updateStatus(UUID id, EventStatus newStatus);
}