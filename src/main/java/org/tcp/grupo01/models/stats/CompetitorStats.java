package org.tcp.grupo01.models.stats;

import org.tcp.grupo01.models.competitors.Competitor;

public class CompetitorStats implements Comparable<CompetitorStats> {
    private final Competitor competitor;
    private int matchesPlayed;
    private int wins;
    private int losses;
    private int pointsFor;     // GP
    private int pointsAgainst; // GC

    public CompetitorStats(Competitor competitor) {
        this.competitor = competitor;
    }

    public void addWin(int scored, int suffered) {
        matchesPlayed++;
        wins++;
        pointsFor += scored;
        pointsAgainst += suffered;
    }

    public void addLoss(int scored, int suffered) {
        matchesPlayed++;
        losses++;
        pointsFor += scored;
        pointsAgainst += suffered;
    }

    public int getPoints() { return wins * 3; }
    public int getGoalDifference() { return pointsFor - pointsAgainst; }

    public Competitor getCompetitor() { return competitor; }
    public int getMatchesPlayed() { return matchesPlayed; }
    public int getWins() { return wins; }
    public int getLosses() { return losses; }
    public int getPointsFor() { return pointsFor; }
    public int getPointsAgainst() { return pointsAgainst; }

    @Override
    public int compareTo(CompetitorStats other) {
        if (this.getPoints() != other.getPoints()) return other.getPoints() - this.getPoints();
        if (this.getWins() != other.getWins()) return other.getWins() - this.getWins();
        if (this.getGoalDifference() != other.getGoalDifference()) return other.getGoalDifference() - this.getGoalDifference();
        return other.getPointsFor() - this.getPointsFor();
    }
}