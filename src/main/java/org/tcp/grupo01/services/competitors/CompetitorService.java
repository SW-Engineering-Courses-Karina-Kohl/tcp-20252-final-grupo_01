package org.tcp.grupo01.services.competitors;

import org.tcp.grupo01.models.competitors.Competitor;

import java.util.List;
import java.util.UUID;

public interface CompetitorService<T extends Competitor> {
    UUID add(T competitor);
    void update(T competitor);
    void remove(UUID id);
    T get(UUID id);
    List<T> getAll();
}
