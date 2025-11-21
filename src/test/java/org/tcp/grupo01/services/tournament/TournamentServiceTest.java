package org.tcp.grupo01.services.tournament;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.tcp.grupo01.models.EventStatus;
import org.tcp.grupo01.models.Match;
import org.tcp.grupo01.models.Tournament;
import org.tcp.grupo01.models.competitors.Person;
import org.tcp.grupo01.services.pairing.League;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TournamentServiceIMTest {

    private TournamentServiceIM service;
    private Tournament<Person> newTournament;
    private UUID initialId;

    @BeforeEach
    void setUp() {
        this.service = new TournamentServiceIM();

        List<Person> participants = List.of(new Person("Jubileu"), new Person("Zé"));

        this.newTournament = Tournament.createForPeople(
                "Copa Teste Unitário",
                new League<>(true, Match::betweenPeople),
                new ArrayList<>(participants)
        );
        this.initialId = this.newTournament.getId();
    }

    @Test
    void testAddShouldGenerateIdAndPersist() {
        assertNull(initialId, "The created object must start with a null ID.");

        UUID generatedId = service.add(newTournament);

        assertNotNull(generatedId, "The Service must generate a UUID.");

        Tournament<?> persisted = service.getById(generatedId);
        assertNotNull(persisted, "The tournament must be found by the generated ID.");
        assertEquals(generatedId, persisted.getId(), "The persisted object's ID must match the generated ID.");

        assertNull(newTournament.getId(), "The ORIGINAL object (newTournament) must remain without an ID.");
        assertNotSame(newTournament, persisted, "The Service must return a NEW instance (Immutability).");
    }

    @Test
    void testGetAllShouldReturnAllPersistedTournaments() {
        assertTrue(service.getAll().isEmpty(), "The list must start empty.");

        service.add(newTournament);
        assertEquals(1, service.getAll().size(), "The list must contain the added tournament.");
    }

    @Test
    void testGetByIdShouldReturnNullForNonexistentId() {
        UUID nonExistentId = UUID.randomUUID();
        assertNull(service.getById(nonExistentId), "Should return null for a non-existent ID.");
    }

    @Test
    void testUpdateStatusShouldChangeStatus() {
        UUID id = service.add(newTournament);
        Tournament<?> persisted = service.getById(id);
        assertEquals(EventStatus.PLANNING, persisted.getStatus(), "The initial status must be PLANNING.");

        service.updateStatus(id, EventStatus.RUNNING);

        assertEquals(EventStatus.RUNNING, persisted.getStatus(), "The status must have been updated to RUNNING.");
    }

    @Test
    void testUpdateStatusShouldThrowExceptionForNonexistentId() {
        UUID nonExistentId = UUID.randomUUID();

        assertThrows(
                IllegalArgumentException.class,
                () -> service.updateStatus(nonExistentId, EventStatus.FINISHED),
                "Should throw an exception when trying to update the status of a non-existent tournament."
        );
    }

    @Test
    void testDeleteShouldRemoveTournament() {
        UUID id = service.add(newTournament);
        assertEquals(1, service.getAll().size(), "The tournament must exist before deletion.");

        service.delete(id);

        assertNull(service.getById(id), "The tournament should not be found after deletion.");
        assertTrue(service.getAll().isEmpty(), "The list of all tournaments must be empty.");
    }
}