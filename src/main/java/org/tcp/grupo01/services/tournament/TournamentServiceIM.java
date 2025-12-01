package org.tcp.grupo01.services.tournament;

import org.tcp.grupo01.models.EventStatus;
import org.tcp.grupo01.models.Tournament;

import java.util.*;

public class TournamentServiceIM implements TournamentService {
    private static TournamentServiceIM instance;
    private final Map<UUID, Tournament<?>> dataStore = new HashMap<>();

    public static TournamentServiceIM getInstance() {
        if (instance == null) {
            instance = new TournamentServiceIM();
        }
        return instance;
    }

    public void clear() {
        this.dataStore.clear();
    }

    @Override
    public List<Tournament<?>> getAll() {
        return new ArrayList<>(dataStore.values());
    }

    @Override
    public Tournament<?> getById(UUID id) {
        return dataStore.get(id);
    }

    @Override
    public UUID add(Tournament<?> tournament) {
        UUID newId = UUID.randomUUID();
        Tournament<?> identifiedTournament = tournament.withId(newId);

        dataStore.put(newId, identifiedTournament);
        return newId;
    }

    @Override
    public void delete(UUID id) {
        dataStore.remove(id);
    }

    @Override
    public void updateStatus(UUID id, EventStatus newStatus) {
        Tournament<?> t = dataStore.get(id);
        if (t != null) {
            t.setStatus(newStatus);
        } else {
            throw new IllegalArgumentException("Torneio não encontrado: " + id);
        }
    }
}