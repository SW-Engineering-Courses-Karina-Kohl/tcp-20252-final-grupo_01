package org.tcp.grupo01.models;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.tcp.grupo01.models.competitors.Person;
import org.tcp.grupo01.models.competitors.Team;
import org.tcp.grupo01.services.pairing.League;
import org.tcp.grupo01.services.pairing.Pairing;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TournamentTest {

    private Pairing<Person> createRealLeague() {
        return new League<>(false, Match::betweenPeople);
    }

    // --- Factory & Initialization Tests ---

    @Test
    @DisplayName("Should create tournament for People correctly")
    void shouldCreateForPeople() {
        String name = "Chess Open";
        ArrayList<Person> people = new ArrayList<>();
        people.add(new Person("Alice"));
        Pairing<Person> league = createRealLeague();

        // Act
        Tournament<Person> tournament = Tournament.createForPeople(name, league, people);

        // Assert (Tournament State only)
        assertNull(tournament.getId());
        assertEquals(name, tournament.getName());
        assertEquals(EventStatus.PLANNING, tournament.getStatus());
        assertEquals(1, tournament.getParticipants().size());
    }

    @Test
    @DisplayName("Should create tournament for Teams correctly")
    void shouldCreateForTeams() {
        // Arrange
        ArrayList<Team> teams = new ArrayList<>();
        teams.add(new Team("Red Team"));
        // Using real League for teams
        Pairing<Team> league = new League<>(true, Match::betweenTeams);

        // Act
        Tournament<Team> tournament = Tournament.createForTeams("Soccer Cup", league, teams);

        // Assert
        assertNotNull(tournament);
        assertEquals("Soccer Cup", tournament.getName());
    }

    // --- State Management Tests (The Core of Tournament Logic) ---

    @Test
    @DisplayName("generateNextMatches() should switch status to RUNNING when rounds are created")
    void shouldSwitchStatusToRunning() {
        // Arrange
        ArrayList<Person> participants = new ArrayList<>();
        participants.add(new Person("Alice"));
        participants.add(new Person("Bob")); // Enough for at least 1 match

        Tournament<Person> tournament = Tournament.createForPeople("Cup", createRealLeague(), participants);

        // Pre-assertion
        assertEquals(EventStatus.PLANNING, tournament.getStatus());
        assertTrue(tournament.getRounds().isEmpty());

        // Act
        tournament.generateNextMatches();

        // Assert - We only check if Tournament updated its OWN state
        assertEquals(EventStatus.RUNNING, tournament.getStatus(), "Tournament status should update to RUNNING");
        assertFalse(tournament.getRounds().isEmpty(), "Tournament should hold rounds after generation");
    }

    @Test
    @DisplayName("generateNextMatches() should NOT crash or change status if participants are empty")
    void shouldHandleEmptyParticipantsGracefully() {
        // Arrange
        ArrayList<Person> emptyList = new ArrayList<>();
        Tournament<Person> tournament = Tournament.createForPeople("Empty Cup", createRealLeague(), emptyList);

        // Act
        tournament.generateNextMatches();

        // Assert
        assertEquals(EventStatus.PLANNING, tournament.getStatus(), "Status should remain PLANNING");
        assertEquals(0, tournament.getRoundCount(), "No rounds should be generated");
    }

    @Test
    @DisplayName("generateNextMatches() should respect existing rounds (Idempotency via Pairing interaction)")
    void shouldRespectExistingRounds() {
        ArrayList<Person> participants = new ArrayList<>();
        participants.add(new Person("A"));
        participants.add(new Person("B"));

        Tournament<Person> tournament = Tournament.createForPeople("Cup", createRealLeague(), participants);

        tournament.generateNextMatches();
        List<List<Match<Person>>> roundsRef1 = tournament.getRounds();

        tournament.generateNextMatches();
        List<List<Match<Person>>> roundsRef2 = tournament.getRounds();

        // Assert
        // We are testing that Tournament correctly passed 'previousRounds' to the pairing logic
        // and updated its internal state with the result (which should be the same list).
        // Note: League implementation returns the same list instance if rounds exist.
        assertEquals(roundsRef1.size(), roundsRef2.size());
        assertEquals(roundsRef1, roundsRef2, "Tournament should keep the current rounds structure if Pairing decides so");
    }

    // --- Immutability & Encapsulation Tests ---

    @Test
    @DisplayName("withId() should create a copy with the new ID")
    void withIdShouldCreateCopy() {
        // Arrange
        Tournament<Person> original = Tournament.createForPeople("Original", createRealLeague(), new ArrayList<>());
        UUID newId = UUID.randomUUID();

        // Act
        Tournament<Person> copy = original.withId(newId);

        // Assert
        assertNotSame(original, copy);
        assertEquals(newId, copy.getId());
        assertEquals(original.getName(), copy.getName());
    }

    @Test
    @DisplayName("withId() should return same instance if ID is already set")
    void withIdShouldReturnSelfIfIdSet() {
        // Arrange
        UUID id = UUID.randomUUID();
        Tournament<Person> original = Tournament.createForPeople("Original", createRealLeague(), new ArrayList<>())
                .withId(id);

        // Act
        Tournament<Person> same = original.withId(UUID.randomUUID());

        // Assert
        assertSame(original, same);
        assertEquals(id, same.getId());
    }

    @Test
    @DisplayName("getParticipants() list should be unmodifiable")
    void participantsShouldBeUnmodifiable() {
        // Arrange
        Tournament<Person> tournament = Tournament.createForPeople("Test", createRealLeague(), new ArrayList<>());
        List<Person> list = tournament.getParticipants();

        // Act & Assert
        assertThrows(UnsupportedOperationException.class, () -> list.add(new Person("Invader")));
    }

    @Test
    @DisplayName("replaceParticipants() should update internal state")
    void replaceParticipantsShouldWork() {
        // Arrange
        ArrayList<Person> initial = new ArrayList<>();
        initial.add(new Person("A"));
        Tournament<Person> tournament = Tournament.createForPeople("Test", createRealLeague(), initial);

        ArrayList<Person> newList = new ArrayList<>();
        newList.add(new Person("B"));

        // Act
        tournament.replaceParticipants(newList);

        // Assert
        assertEquals(1, tournament.getParticipants().size());
        assertEquals("B", tournament.getParticipants().get(0).getName());
    }
}