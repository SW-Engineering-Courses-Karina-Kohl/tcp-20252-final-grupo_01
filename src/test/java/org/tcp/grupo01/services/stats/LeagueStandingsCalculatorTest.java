package org.tcp.grupo01.services.stats;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.tcp.grupo01.models.EventStatus;
import org.tcp.grupo01.models.Match;
import org.tcp.grupo01.models.Tournament;
import org.tcp.grupo01.models.competitors.Team;
import org.tcp.grupo01.models.stats.CompetitorStats;
import org.tcp.grupo01.services.pairing.League;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LeagueStandingsCalculatorTest {

    @Test
    @DisplayName("Integration: Should calculate standings correctly ignoring unfinished matches")
    void shouldCalculateStandingsIgnoringUnfinishedMatches() {
        // 1. ARRANGE: Criar Torneio e Times
        Team teamA = new Team("Team A");
        Team teamB = new Team("Team B");
        ArrayList<Team> participants = new ArrayList<>(List.of(teamA, teamB));

        League<Team> leaguePairing = new League<>(true, Match::betweenTeams);
        Tournament<Team> tournament = Tournament.createForTeams("Integration Cup", leaguePairing, participants);

        // Gera as partidas (Cria 1 jogo: A vs B)
        tournament.generateNextMatches();

        List<Match<Team>> round1 = tournament.getRounds().get(0);
        Match<Team> match = round1.get(0);

        // Cenário 1: Partida em Planejamento (PLANNING)
        // Não deve contar pontos
        LeagueStandingsCalculator calculator = new LeagueStandingsCalculator();
        List<CompetitorStats> standingsPlanning = calculator.calculate(tournament);

        assertEquals(0, standingsPlanning.get(0).getMatchesPlayed(), "Should ignore PLANNING matches");
        assertEquals(0, standingsPlanning.get(0).getPoints());

        // Cenário 2: Partida em Andamento (RUNNING)
        // Digamos que está 2x0, mas não acabou. Não deve contar.
        match.updateResult(2, 0, EventStatus.RUNNING);

        List<CompetitorStats> standingsRunning = calculator.calculate(tournament);

        assertEquals(0, standingsRunning.get(0).getMatchesPlayed(), "Should ignore RUNNING matches");
        assertEquals(0, standingsRunning.get(0).getPoints());

        // Cenário 3: Partida Finalizada (FINISHED)
        // Agora deve contar!
        match.updateResult(3, 1, EventStatus.FINISHED);

        List<CompetitorStats> standingsFinished = calculator.calculate(tournament);

        // Valida Team A (Vencedor)
        CompetitorStats statsA = standingsFinished.stream()
                .filter(s -> s.getCompetitor().equals(teamA))
                .findFirst().orElseThrow();

        assertEquals(1, statsA.getMatchesPlayed(), "Should count FINISHED matches");
        assertEquals(3, statsA.getPoints());
        assertEquals(2, statsA.getGoalDifference()); // 3 - 1

        // Valida Team B (Perdedor)
        CompetitorStats statsB = standingsFinished.stream()
                .filter(s -> s.getCompetitor().equals(teamB))
                .findFirst().orElseThrow();

        assertEquals(1, statsB.getMatchesPlayed());
        assertEquals(0, statsB.getPoints());
    }
}