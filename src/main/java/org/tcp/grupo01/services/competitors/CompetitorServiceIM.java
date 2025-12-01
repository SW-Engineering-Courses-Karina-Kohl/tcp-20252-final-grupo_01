package org.tcp.grupo01.services.competitors;

import org.tcp.grupo01.models.competitors.Competitor;

import java.util.*;

public class CompetitorServiceIM<T extends Competitor> implements CompetitorService<T> {

    private final Map<UUID, T> storage = new HashMap<>();

    @Override
    public UUID add(T competitor) {
        if (competitor.getId() == null) {
            competitor.setId(UUID.randomUUID());
        }

        storage.put(competitor.getId(), competitor);
        return competitor.getId();
    }

    @Override
    public void update(T competitor) {
        if (competitor.getId() != null) {
            storage.put(competitor.getId(), competitor);
        }
    }

    @Override
    public void remove(UUID id) {
        storage.remove(id);
    }

    @Override
    public T get(UUID id) {
        return storage.get(id);
    }

    @Override
    public List<T> getAll() {
        return new ArrayList<>(storage.values());
    }
}
