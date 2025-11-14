package services.pairings;

import models.Match;
import models.competitors.Competitor;

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
    private boolean matchesGenerated = false;
    private final BiFunction<T, T, Match<T>> createMatch;

    public League(boolean doubleRound, BiFunction<T, T, Match<T>> createMatch) {
        this.doubleRound = doubleRound;
        this.createMatch = createMatch;
    }


    @Override
    public List<List<Match<T>>> generateRounds(List<T> participants, List<List<Match<T>>> previousRounds) {
        if (this.matchesGenerated) return previousRounds;

        this.matchesGenerated = true;
        return this.generateAllMatches(participants);
    }

    /**
     * Gera todas as rodadas da liga.
     * @return lista de rodadas (cada rodada é uma lista de partidas)
     */
    private List<List<Match<T>>> generateAllMatches(List<T> participants) {
        if (participants == null || participants.isEmpty()) return new ArrayList<>();

        ArrayList<T> players = new ArrayList<>(participants);
        if (players.size() % 2 != 0) players.add(null);

        int n = players.size();
        int totalRounds = n - 1;
        List<List<Match<T>>> firstLeg = new ArrayList<>(totalRounds);

        for (int round = 0; round < totalRounds; round++) {
            List<Match<T>> current = new ArrayList<>(n / 2);

            for (int i = 0; i < n / 2; i++) {
                T a = players.get(i);
                T b = players.get(n - 1 - i);
                if (a == null || b == null) continue;
                current.add(this.createMatch.apply(a, b));
            }

            firstLeg.add(current);

            // Rotaciona (mantém o primeiro fixo)
            T fixed = players.getFirst();
            List<T> rotated = new ArrayList<>(players.subList(1, n));
            Collections.rotate(rotated, 1);
            players.clear();
            players.add(fixed);
            players.addAll(rotated);
        }

        Collections.shuffle(firstLeg);

        List<List<Match<T>>> allRounds = new ArrayList<>(firstLeg);

        if (!this.doubleRound) return allRounds;

        List<List<Match<T>>> secondLeg = firstLeg.stream()
                .map(round -> round.stream()
                        .map(match -> this.createMatch.apply(match.getCompetitorB(), match.getCompetitorA()))
                        .collect(Collectors.toList())
                )
                .collect(Collectors.toList());

        Collections.shuffle(secondLeg);
        allRounds.addAll(secondLeg);

        return allRounds;
    }
}