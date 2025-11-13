package services.pairings;

import models.Match;
import models.competitors.Competitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.function.BiFunction;

/**
 * Implementação de Pairing para formato de Liga (pontos corridos).
 * Gera todos os confrontos possíveis entre competidores.
 * Pode ter turno único ou turno e returno.
 */
public class League<T extends Competitor> implements Pairing<T> {
    private final boolean doubleRound; // true = turno e returno
    private final BiFunction<T, T, Match<T>> createMatch; // função que cria partidas do tipo certo

    public League(boolean doubleRound, BiFunction<T, T, Match<T>> createMatch) {
        this.doubleRound = doubleRound;
        this.createMatch = createMatch;
    }

    @Override
    public ArrayList<Match<T>> generateNextRound(ArrayList<T> participants, ArrayList<Match<T>> previousMatches) {
        ArrayList<Match<T>> firstRound = new ArrayList<>();
        ArrayList<Match<T>> secondRound = new ArrayList<>();

        ArrayList<T> shuffled = new ArrayList<>(participants);
        Collections.shuffle(shuffled);

        // Gera todas as combinações de confrontos (round-robin)
        for (int i = 0; i < shuffled.size(); i++) {
            for (int j = i + 1; j < shuffled.size(); j++) {
                T a = shuffled.get(i);
                T b = shuffled.get(j);

                // Jogo de ida
                firstRound.add(createMatch.apply(a, b));

                // Jogo de volta (somente se doubleRound = true)
                if (doubleRound) {
                    secondRound.add(createMatch.apply(b, a));
                }
            }
        }

        // Embaralha separadamente os turnos
        Collections.shuffle(firstRound);
        Collections.shuffle(secondRound);

        // Junta tudo: primeiro todas as partidas do turno 1, depois do turno 2
        ArrayList<Match<T>> allMatches = new ArrayList<>(firstRound);
        if (doubleRound) {
            allMatches.addAll(secondRound);
        }

        return allMatches;
    }
}
