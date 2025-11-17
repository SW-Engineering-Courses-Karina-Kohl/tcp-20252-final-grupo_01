package org.tcp.grupo01.services.pairings;

import org.junit.jupiter.api.Test;
import org.tcp.grupo01.models.Match;
import org.tcp.grupo01.models.competitors.Person;

import java.util.List;
import java.util.function.BiFunction;

import static org.junit.jupiter.api.Assertions.*;

public class LeagueTest {

    /**
     * Factory function used by League to create matches between competitors.
     */
    private final BiFunction<Person, Person, Match<Person>> matchFactory =
            Match::betweenPeople;

    @Test
    void generatesCorrectNumberOfRoundsForEvenParticipants_singleRound() {
        // 4 participants → round-robin → (n - 1) = 3 rounds
        List<Person> players = List.of(
                new Person("A"),
                new Person("B"),
                new Person("C"),
                new Person("D")
        );

        League<Person> league = new League<>(false, matchFactory);

        List<List<Match<Person>>> rounds = league.generateRounds(players, List.of());

        assertEquals(3, rounds.size(), "Should generate n-1 rounds");
        rounds.forEach(round -> assertEquals(2, round.size(), "Each round should have n/2 matches"));
    }

    @Test
    void generatesCorrectNumberOfRoundsForOddParticipants_includesBye() {
        // 5 participants → add 1 bye → effectively 6 → rounds = 5
        List<Person> players = List.of(
                new Person("A"),
                new Person("B"),
                new Person("C"),
                new Person("D"),
                new Person("E")
        );

        League<Person> league = new League<>(false, matchFactory);

        List<List<Match<Person>>> rounds = league.generateRounds(players, List.of());

        assertEquals(5, rounds.size(), "Odd number should still produce n rounds after bye insertion");

        // With a bye (one null), each round has (n/2) matches = 3 matches (one will be skipped)
        rounds.forEach(round -> assertTrue(
                round.size() == 2 || round.size() == 3,
                "Rounds may have fewer matches due to byes"
        ));
    }

    @Test
    void doubleRoundRobin_generatesMirroredMatches() {
        List<Person> players = List.of(
                new Person("A"),
                new Person("B"),
                new Person("C"),
                new Person("D")
        );

        League<Person> league = new League<>(true, matchFactory);

        List<List<Match<Person>>> rounds = league.generateRounds(players, List.of());

        // First leg: 3 rounds ; Second leg: 3 rounds ; total = 6
        assertEquals(6, rounds.size(), "Double round should double the number of rounds");

        // Collect first leg matches
        List<List<Match<Person>>> firstLeg = rounds.subList(0, 3);
        List<List<Match<Person>>> secondLeg = rounds.subList(3, 6);

        // Mirror check: every match A vs B must appear as B vs A
        boolean allMirrored = firstLeg.stream()
                .flatMap(List::stream)
                .allMatch(m1 -> secondLeg.stream()
                        .flatMap(List::stream)
                        .anyMatch(m2 ->
                                m1.getCompetitorA().equals(m2.getCompetitorB()) &&
                                        m1.getCompetitorB().equals(m2.getCompetitorA())
                        )
                );

        assertTrue(allMirrored, "Second leg must contain mirrored matches (B vs A)");
    }

    @Test
    void generateRounds_shouldReturnPreviousRounds_ifTheyExist() {
        List<Person> players = List.of(
                new Person("A"),
                new Person("B"),
                new Person("C"),
                new Person("D")
        );

        League<Person> league = new League<>(false, matchFactory);
        List<List<Match<Person>>> firstCall = league.generateRounds(players, List.of());
        List<List<Match<Person>>> secondCall = league.generateRounds(players, firstCall);

        assertSame(firstCall, secondCall, "League should return the existing rounds, not regenerate");

        assertFalse(firstCall.isEmpty(), "First call should have generated matches");
    }

    @Test
    void generatedMatchesContainCorrectCompetitors() {
        List<Person> players = List.of(
                new Person("A"),
                new Person("B"),
                new Person("C"),
                new Person("D")
        );

        League<Person> league = new League<>(false, matchFactory);

        List<List<Match<Person>>> rounds = league.generateRounds(players, List.of());

        // Flatten matches
        List<Match<Person>> allMatches = rounds.stream()
                .flatMap(List::stream)
                .toList();

        // Ensure no match has equal competitors
        assertTrue(
                allMatches.stream().noneMatch(m ->
                        m.getCompetitorA().equals(m.getCompetitorB())
                ),
                "No match should have the same competitor on both sides"
        );

        // Ensure all competitors belong to the original list
        assertTrue(
                allMatches.stream().allMatch(m ->
                        players.contains(m.getCompetitorA()) &&
                                players.contains(m.getCompetitorB())
                ),
                "All matches should involve only provided competitors"
        );
    }
}