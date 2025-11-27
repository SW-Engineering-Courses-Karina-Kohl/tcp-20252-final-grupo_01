package org.tcp.grupo01.services.pairing;

import org.tcp.grupo01.models.Match;
import org.tcp.grupo01.models.competitors.Competitor;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class Knockout<T extends Competitor> implements Pairing<T> {

    private final BiFunction<T, T, Match<T>> createMatch;

    public Knockout(BiFunction<T, T, Match<T>> createMatch) {
        this.createMatch = createMatch;
    }

    private void ensureAllMatchesCompleted(List<List<Match<T>>> previousRounds) {
        for (int roundIndex = 0; roundIndex < previousRounds.size(); roundIndex++) {
            List<Match<T>> round = previousRounds.get(roundIndex);
            for (Match<T> match : round) {
                if (match.getWinner() == null) {
                    throw new IllegalStateException(
                            "Não é possível gerar a próxima rodada. "
                            + "A rodada " + (roundIndex + 1) + " ainda possui partidas sem resultado."
                    );
                }
            }
        }
    }

    private List<Match<T>> flatten(List<List<Match<T>>> rounds) {
        return rounds.stream().flatMap(List::stream).collect(Collectors.toList());
    }

    private List<Match<T>> generateNextRound(List<T> participants, List<Match<T>> previousMatches) {
        List<Match<T>> newMatches = new ArrayList<>();
        for (int i = 0; i<previousMatches.size(); i+=2)
            newMatches.add(createMatch.apply(previousMatches.get(i).getWinner(), previousMatches.get(i+1).getWinner()));
        return newMatches;
    }

    @Override
    public List<List<Match<T>>> generateRounds(List<T> participants, List<List<Match<T>>> previousRounds) {

        validateParticipants(participants);
        ensureAllMatchesCompleted(previousRounds);

        List<Match<T>> flattened = flatten(previousRounds);
        List<Match<T>> newRound = generateNextRound(participants, flattened);

        if (newRound.isEmpty()) return previousRounds;

        List<List<Match<T>>> updated = new ArrayList<>(previousRounds);
        updated.add(newRound);
        return updated;
    }

    public int[] getRecordOf(T player, List<List<Match<T>>> rounds) {
        int wins = 0, losses = 0;

        for (List<Match<T>> r : rounds) {
            for (Match<T> m : r) {

                if (m.getWinner() == null) continue;

                if (m.getCompetitorA().equals(player)) {
                    if (m.getWinner().equals(player)) wins++; else losses++;
                }

                if (m.getCompetitorB().equals(player)) {
                    if (m.getWinner().equals(player)) wins++; else losses++;
                }
            }
        }
        return new int[]{wins, losses};
    }

    private void validateParticipants(List<T> participants) {
        if (participants == null || participants.isEmpty())
            throw new IllegalArgumentException("A lista de participantes está vazia.");

        int size = participants.size();
        Set<T> set = new HashSet<>(participants);
        if (set.size() != size)
            throw new IllegalArgumentException("Existem participantes duplicados.");

        if ((size & -size) != size)
            throw new IllegalArgumentException("O número de participantes deve ser potência de 2.");
    }

}
