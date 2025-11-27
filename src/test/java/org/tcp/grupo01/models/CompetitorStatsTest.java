package org.tcp.grupo01.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.tcp.grupo01.models.competitors.Competitor;
import org.tcp.grupo01.models.competitors.Team; // ou Person
import org.tcp.grupo01.models.stats.CompetitorStats;

import static org.junit.jupiter.api.Assertions.*;

class CompetitorStatsTest {

    private CompetitorStats stats;

    @BeforeEach
    void setUp() {
        Competitor competitor = new Team("Test Competitor");
        stats = new CompetitorStats(competitor);
    }

    @Test
    @DisplayName("Should initialize with zero stats")
    void shouldInitializeWithZeroStats() {
        assertEquals(0, stats.getPoints());
        assertEquals(0, stats.getMatchesPlayed());
        assertEquals(0, stats.getWins());
        assertEquals(0, stats.getLosses());
        assertEquals(0, stats.getGoalDifference());
    }

    @Test
    @DisplayName("Should calculate points and stats correctly for a win")
    void shouldCalculateWinCorrectly() {
        // Act: Venceu 3-1
        stats.addWin(3, 1);

        // Assert
        assertEquals(3, stats.getPoints()); // 3 pts por vitória
        assertEquals(1, stats.getWins());
        assertEquals(1, stats.getMatchesPlayed());
        assertEquals(2, stats.getGoalDifference()); // 3 - 1 = 2
    }

    @Test
    @DisplayName("Should calculate stats correctly for a loss")
    void shouldCalculateLossCorrectly() {
        // Act: Perdeu 0-2
        stats.addLoss(0, 2);

        // Assert
        assertEquals(0, stats.getPoints());
        assertEquals(1, stats.getLosses());
        assertEquals(1, stats.getMatchesPlayed());
        assertEquals(-2, stats.getGoalDifference()); // 0 - 2 = -2
    }

    @Test
    @DisplayName("Should rank higher points first")
    void shouldRankHigherPointsFirst() {
        CompetitorStats leader = new CompetitorStats(new Team("Leader"));
        leader.addWin(1, 0); // 3 pts

        CompetitorStats trailer = new CompetitorStats(new Team("Trailer"));
        trailer.addLoss(0, 1); // 0 pts

        // compareTo < 0 significa que "leader" vem antes de "trailer" na lista ordenada
        assertTrue(leader.compareTo(trailer) < 0, "Leader with more points should come first");
    }

    @Test
    @DisplayName("Should break tie by goal difference when points and wins are equal")
    void shouldBreakTieByGoalDifference() {
        // Ambos ganharam 1 jogo (3 pontos, 1 vitória)
        CompetitorStats teamHighGD = new CompetitorStats(new Team("High GD"));
        teamHighGD.addWin(5, 0); // SG +5

        CompetitorStats teamLowGD = new CompetitorStats(new Team("Low GD"));
        teamLowGD.addWin(1, 0); // SG +1

        // Quem tem maior saldo vem primeiro (compareTo < 0)
        assertTrue(teamHighGD.compareTo(teamLowGD) < 0);
    }
}