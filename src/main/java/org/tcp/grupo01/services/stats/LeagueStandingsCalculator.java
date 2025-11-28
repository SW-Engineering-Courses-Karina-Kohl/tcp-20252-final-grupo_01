package org.tcp.grupo01.services.stats;

import org.tcp.grupo01.models.EventStatus;
import org.tcp.grupo01.models.Match;
import org.tcp.grupo01.models.Tournament;
import org.tcp.grupo01.models.competitors.Competitor;
import org.tcp.grupo01.models.stats.CompetitorStats;

import java.util.*;

public class LeagueStandingsCalculator {

    public List<CompetitorStats> calculate(Tournament<?> tournament) {
        Map<Competitor, CompetitorStats> statsMap = new HashMap<>();

        for (Competitor c : tournament.getParticipants()) {
            statsMap.put(c, new CompetitorStats(c));
        }

        for (List<? extends Match<?>> round : tournament.getRounds()) {
            for (Match<?> match : round) {
                if (match.getStatus() != EventStatus.FINISHED) continue;

                CompetitorStats statA = statsMap.get(match.getCompetitorA());
                CompetitorStats statB = statsMap.get(match.getCompetitorB());

                if (statA == null || statB == null) continue;

                int scoreA = match.getScoreA();
                int scoreB = match.getScoreB();

                if (scoreA > scoreB) {
                    statA.addWin(scoreA, scoreB);
                    statB.addLoss(scoreB, scoreA);
                } else if (scoreB > scoreA) {
                    statB.addWin(scoreB, scoreA);
                    statA.addLoss(scoreA, scoreB);
                }
            }
        }

        List<CompetitorStats> sortedStats = new ArrayList<>(statsMap.values());
        Collections.sort(sortedStats);
        return sortedStats;
    }
}