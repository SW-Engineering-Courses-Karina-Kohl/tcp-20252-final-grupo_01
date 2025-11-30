package org.tcp.grupo01.services.pairing;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.tcp.grupo01.models.EventStatus;
import org.tcp.grupo01.models.Match;
import org.tcp.grupo01.models.competitors.Person;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

class KnockoutTest {

    private Knockout<Person> createKnockout() {
        return new Knockout<>(Match::betweenPeople);
    }

    private List<Person> generateParticipants(int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> new Person("Player " + (i + 1)))
                .collect(Collectors.toList());
    }


    private void setWinnersForRound(List<Match<Person>> round) {
        for (Match<Person> m : round) {
            m.updateResult(1, 0, EventStatus.FINISHED);
        }
    }

    @Test
    @DisplayName("Should throw exception if participant count is not a power of 2")
    void shouldValidatePowerOfTwo() {
        Knockout<Person> knockout = createKnockout();

        List<Person> invalidList = generateParticipants(3);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            knockout.generateRounds(invalidList, new ArrayList<>());
        });

        assertTrue(ex.getMessage().contains("potência de 2"), "Should explicitly mention power of 2 requirement");
    }

    @Test
    @DisplayName("Should throw exception if duplicates exist")
    void shouldValidateDuplicates() {
        Knockout<Person> knockout = createKnockout();
        List<Person> participants = generateParticipants(4);
        participants.set(1, participants.get(0));

        assertThrows(IllegalArgumentException.class, () -> {
            knockout.generateRounds(participants, new ArrayList<>());
        });
    }

    @Test
    @DisplayName("Should throw exception if previous round is incomplete")
    void shouldBlockGenerationIfRoundIncomplete() {
        Knockout<Person> knockout = createKnockout();
        List<Person> participants = generateParticipants(4);

        List<List<Match<Person>>> rounds = knockout.generateRounds(participants, new ArrayList<>());

        assertThrows(IllegalStateException.class, () -> {
            knockout.generateRounds(participants, rounds);
        }, "Cannot generate next round if current matches have no winners");
    }


    @Test
    @DisplayName("Should generate first round correctly (N/2 matches)")
    void shouldGenerateFirstRound() {
        Knockout<Person> knockout = createKnockout();
        List<Person> participants = generateParticipants(8);

        List<List<Match<Person>>> result = knockout.generateRounds(participants, new ArrayList<>());

        assertEquals(1, result.size());
        List<Match<Person>> round1 = result.get(0);
        assertEquals(4, round1.size(), "8 players should result in 4 matches");

        assertEquals(participants.get(0), round1.get(0).getCompetitorA());
        assertEquals(participants.get(1), round1.get(0).getCompetitorB());
    }

    @Test
    @DisplayName("Should advance winners to the next round correctly")
    void shouldAdvanceWinners() {
        Knockout<Person> knockout = createKnockout();
        List<Person> participants = generateParticipants(4);

        List<List<Match<Person>>> rounds = knockout.generateRounds(participants, new ArrayList<>());
        List<Match<Person>> semis = rounds.get(0);

        semis.get(0).updateResult(1, 0, EventStatus.FINISHED);
        Person winner1 = semis.get(0).getWinner();

        semis.get(1).updateResult(0, 1, EventStatus.FINISHED);
        Person winner2 = semis.get(1).getWinner();

        rounds = knockout.generateRounds(participants, rounds);

        assertEquals(2, rounds.size());
        List<Match<Person>> finalRound = rounds.get(1);
        assertEquals(1, finalRound.size(), "Should have exactly 1 match (The Final)");

        Match<Person> finalMatch = finalRound.get(0);

        assertEquals(winner1, finalMatch.getCompetitorA());
        assertEquals(winner2, finalMatch.getCompetitorB());
    }

    @Test
    @DisplayName("Should stop generating rounds when the Champion is decided")
    void shouldStopAfterFinal() {
        Knockout<Person> knockout = createKnockout();
        List<Person> participants = generateParticipants(2);

        List<List<Match<Person>>> rounds = knockout.generateRounds(participants, new ArrayList<>());
        Match<Person> finalMatch = rounds.get(0).get(0);

        finalMatch.updateResult(1, 0, EventStatus.FINISHED);

        List<List<Match<Person>>> result = knockout.generateRounds(participants, rounds);

        assertEquals(1, result.size(), "Should not add new rounds after the final match has a winner");
        assertSame(rounds, result, "Should return the same list instance");
    }

    @Test
    @DisplayName("Integration: Full 8-Player Bracket Simulation")
    void shouldCompleteFullBracket() {
        Knockout<Person> knockout = createKnockout();
        List<Person> participants = generateParticipants(8);
        List<List<Match<Person>>> rounds = new ArrayList<>();

        rounds = knockout.generateRounds(participants, rounds);
        assertEquals(4, rounds.get(0).size());
        setWinnersForRound(rounds.get(0));

        rounds = knockout.generateRounds(participants, rounds);
        assertEquals(2, rounds.get(1).size());
        setWinnersForRound(rounds.get(1));

        rounds = knockout.generateRounds(participants, rounds);
        assertEquals(1, rounds.get(2).size());
        setWinnersForRound(rounds.get(2));

        List<List<Match<Person>>> finalState = knockout.generateRounds(participants, rounds);
        assertEquals(3, finalState.size(), "8 Players = log2(8) = 3 Rounds");
    }


}