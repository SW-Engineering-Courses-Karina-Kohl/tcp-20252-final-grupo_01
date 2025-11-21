package org.tcp.grupo01.services.pairing;

import org.tcp.grupo01.models.Match;
import org.tcp.grupo01.models.competitors.Competitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * League (round-robin) with round generation using Berger's algorithm.
 * Supports single round-robin or double round-robin, and handles an odd number of
 * participants with a "bye".
 */
public class League<T extends Competitor> implements Pairing<T> {
    private final boolean doubleRound;
    private final BiFunction<T, T, Match<T>> createMatch;

    public League(boolean doubleRound, BiFunction<T, T, Match<T>> createMatch) {
        this.doubleRound = doubleRound;
        this.createMatch = createMatch;
    }

    @Override
    public List<List<Match<T>>> generateRounds(List<T> participants, List<List<Match<T>>> previousRounds) {
        if (previousRounds != null && !previousRounds.isEmpty()) {
            return previousRounds;
        }

        if (participants == null || participants.isEmpty()) {
            return List.of();
        }

        return generateAllMatches(participants);
    }

    private List<List<Match<T>>> generateAllMatches(List<T> participants) {
        if (participants == null || participants.isEmpty()) {
            return List.of();
        }

        List<T> players = preparePlayers(participants);

        List<List<Match<T>>> firstLeg = generateFirstLeg(players);
        Collections.shuffle(firstLeg);

        if (!doubleRound) {
            return firstLeg;
        }

        List<List<Match<T>>> secondLeg = generateSecondLeg(firstLeg);
        Collections.shuffle(secondLeg);

        List<List<Match<T>>> all = new ArrayList<>(firstLeg);
        all.addAll(secondLeg);
        return all;
    }

    /**
     * Ensures the list has an even number of competitors (inserting a "bye" if necessary).
     */
    private List<T> preparePlayers(List<T> participants) {
        List<T> players = new ArrayList<>(participants);
        if (players.size() % 2 != 0) {
            players.add(null); // bye
        }
        return players;
    }

    /**
     * Generates the first leg (single round) using Berger's algorithm.
     */
    private List<List<Match<T>>> generateFirstLeg(List<T> players) {
        int n = players.size();
        int totalRounds = n - 1;

        List<List<Match<T>>> rounds = new ArrayList<>(totalRounds);

        List<T> rotating = new ArrayList<>(players);

        for (int round = 0; round < totalRounds; round++) {
            rounds.add(generateSingleRound(rotating));
            rotatePlayers(rotating);
        }

        return rounds;
    }

    /**
     * Generates a single round for the Berger algorithm.
     */
    private List<Match<T>> generateSingleRound(List<T> players) {
        List<Match<T>> matches = new ArrayList<>();
        int n = players.size();

        for (int i = 0; i < n / 2; i++) {
            T a = players.get(i);
            T b = players.get(n - 1 - i);

            if (a != null && b != null) {
                matches.add(createMatch.apply(a, b));
            }
        }

        return matches;
    }

    /**
     * Classic rotation for Berger's algorithm (fixing the first player).
     */
    private void rotatePlayers(List<T> players) {
        T fixed = players.getFirst();
        List<T> rotated = new ArrayList<>(players.subList(1, players.size()));
        Collections.rotate(rotated, 1);

        players.clear();
        players.add(fixed);
        players.addAll(rotated);
    }

    /**
     * Generates the second leg (inverting the match pairings).
     */
    private List<List<Match<T>>> generateSecondLeg(List<List<Match<T>>> firstLeg) {
        return firstLeg.stream()
                .map(round -> round.stream()
                        .map(m -> createMatch.apply(m.getCompetitorB(), m.getCompetitorA()))
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());
    }
}