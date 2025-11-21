package org.tcp.grupo01.services.pairing;


import org.tcp.grupo01.models.Match;
import org.tcp.grupo01.models.competitors.Competitor;

import java.util.List;

public interface Pairing<T extends Competitor> {
    /**
     * Generates one or more rounds of matches, based on the participants
     * and the matches already played.
     *
     * @param participants list of competitors in the tournament
     * @param previousRounds list of previous rounds (each round = list of matches)
     * @return updated list of rounds with the new pairings
     */
    List<List<Match<T>>> generateRounds(
            List<T> participants,
            List<List<Match<T>>> previousRounds
    );
}