package services.pairings;

import models.competitors.Competitor;
import models.Match;

import java.util.ArrayList;

public interface Pairing<T extends Competitor> {
    public ArrayList<Match<T>> generateNextRound(ArrayList<T> participants, ArrayList<Match<T>> previousMatches);
}
