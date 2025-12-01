package org.tcp.grupo01.services.competitors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.tcp.grupo01.models.competitors.Competitor;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CompetitorServiceIMTest {

    private CompetitorServiceIM<DummyCompetitor> service;

    static class DummyCompetitor extends Competitor {
        DummyCompetitor(String name) { super(name); }
    }

    @BeforeEach
    void setup() {
        service = new CompetitorServiceIM<>();
    }

    @Test
    @DisplayName("Should assign an ID and store a competitor when added without ID")
    void shouldAssignIdAndStoreCompetitorWhenAddingWithoutId() {
        DummyCompetitor competitor = new DummyCompetitor("Alice");

        UUID id = service.add(competitor);

        assertNotNull(id, "The service must assign an ID if the competitor has none.");
        assertEquals(competitor, service.get(id), "The competitor must be stored after being added.");
    }

    @Test
    @DisplayName("Should store a competitor using its existing predefined ID")
    void shouldStoreCompetitorWithExistingId() {
        DummyCompetitor competitor = new DummyCompetitor("Bob");
        UUID predefinedId = UUID.randomUUID();
        competitor.setId(predefinedId);

        UUID returnedId = service.add(competitor);

        assertEquals(predefinedId, returnedId, "The service must use the competitor's existing ID.");
        assertEquals(competitor, service.get(predefinedId), "The competitor must be stored under its predefined ID.");
    }

    @Test
    @DisplayName("Should update an existing competitor keeping the same ID")
    void shouldUpdateExistingCompetitor() {
        DummyCompetitor competitor = new DummyCompetitor("Charlie");
        UUID id = service.add(competitor);

        DummyCompetitor updated = new DummyCompetitor("Charles");
        updated.setId(id);

        service.update(updated);

        assertEquals("Charles", service.get(id).getName(), "The service must replace the old competitor with the updated one.");
    }

    @Test
    @DisplayName("Should remove a competitor by its ID")
    void shouldRemoveCompetitorById() {
        DummyCompetitor competitor = new DummyCompetitor("Diana");
        UUID id = service.add(competitor);

        service.remove(id);

        assertNull(service.get(id), "The competitor must no longer exist after being removed.");
    }

    @Test
    @DisplayName("Should return a competitor by its ID")
    void shouldReturnCompetitorById() {
        DummyCompetitor competitor = new DummyCompetitor("Eve");
        UUID id = service.add(competitor);

        DummyCompetitor result = service.get(id);

        assertEquals(competitor, result, "The service must return the correct competitor by ID.");
    }

    @Test
    @DisplayName("Should return all stored competitors")
    void shouldReturnAllStoredCompetitors() {
        DummyCompetitor c1 = new DummyCompetitor("A");
        DummyCompetitor c2 = new DummyCompetitor("B");

        service.add(c1);
        service.add(c2);

        List<DummyCompetitor> all = service.getAll();

        assertEquals(2, all.size(), "The service must return all stored competitors.");
        assertTrue(all.contains(c1), "The list must contain the first competitor.");
        assertTrue(all.contains(c2), "The list must contain the second competitor.");
    }
}
