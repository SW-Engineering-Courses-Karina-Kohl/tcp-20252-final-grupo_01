package org.tcp.grupo01.services.pairings;


import org.tcp.grupo01.models.Match;
import org.tcp.grupo01.models.competitors.Competitor;

import java.util.List;

public interface Pairing<T extends Competitor> {
    /**
     * Gera uma ou mais rodadas de partidas, com base nos participantes e nas partidas já realizadas.
     *
     * @param participants lista de competidores ainda ativos no torneio
     * @param previousRounds lista de rodadas anteriores (cada rodada = lista de partidas)
     * @return lista de rodadas atualizada com os novos confrontos
     */
    List<List<Match<T>>> generateRounds(
            List<T> participants,
            List<List<Match<T>>> previousRounds
    );
}
