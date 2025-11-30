package org.tcp.grupo01.services.pairing;

import org.tcp.grupo01.models.Match;
import org.tcp.grupo01.models.competitors.Competitor;

import java.util.*;
import java.util.function.BiFunction;

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

    @Override
    public List<List<Match<T>>> generateRounds(List<T> participants, List<List<Match<T>>> previousRounds) {

        validateParticipants(participants);
        
        if (previousRounds == null || previousRounds.isEmpty()) {
            List<Match<T>> firstRound = new ArrayList<>();
            for (int i = 0; i < participants.size(); i += 2)
                firstRound.add(createMatch.apply(participants.get(i), participants.get(i + 1)));
    
            List<List<Match<T>>> result = new ArrayList<>();
            result.add(firstRound);
            return result;
        }
    
        ensureAllMatchesCompleted(previousRounds);
    
        List<Match<T>> lastRound = previousRounds.get(previousRounds.size() - 1);
        if (lastRound.size() == 1 && lastRound.get(0).getWinner() != null) {
            return previousRounds;
        }

        List<Match<T>> newRound = new ArrayList<>();
        for (int i = 0; i < lastRound.size(); i += 2)
            newRound.add(createMatch.apply(lastRound.get(i).getWinner(), lastRound.get(i+1).getWinner()));

        if (newRound.isEmpty()) return previousRounds;

        List<List<Match<T>>> updated = new ArrayList<>(previousRounds);
        updated.add(newRound);
        return updated;
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
