package org.tcp.grupo01.services.pairings;

import org.tcp.grupo01.models.Match;
import org.tcp.grupo01.models.competitors.Competitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * Liga (pontos corridos) com geração de rodadas usando algoritmo de Berger.
 * Suporta turno único ou turno e returno, e trata número ímpar de participantes com "bye".
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

        // 2. GERAÇÃO (CASO DE BORDA):
        // Se a lista de participantes estiver vazia, retorna lista vazia.
        if (participants == null || participants.isEmpty()) {
            return List.of();
        }

        return generateAllMatches(participants);
    }

    /**
     * Gera todas as rodadas (turno e opcionalmente returno).
     */
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
     * Garante que a lista tenha número par de competidores (inserindo "bye" caso necessário).
     */
    private List<T> preparePlayers(List<T> participants) {
        List<T> players = new ArrayList<>(participants);
        if (players.size() % 2 != 0) {
            players.add(null); // bye
        }
        return players;
    }

    /**
     * Gera apenas o turno único usando o algoritmo de Berger.
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
     * Gera uma rodada do algoritmo de Berger.
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
     * Rotação clássica do algoritmo de Berger (fixando o primeiro jogador).
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
     * Gera o returno (inversão de mandos das partidas).
     */
    private List<List<Match<T>>> generateSecondLeg(List<List<Match<T>>> firstLeg) {
        return firstLeg.stream()
                .map(round -> round.stream()
                        .map(m -> createMatch.apply(m.getCompetitorB(), m.getCompetitorA()))
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());
    }
}
