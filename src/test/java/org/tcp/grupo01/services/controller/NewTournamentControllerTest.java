package org.tcp.grupo01.services.controller;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.tcp.grupo01.controller.NewTournamentController;
import org.tcp.grupo01.models.Tournament;
import org.tcp.grupo01.models.competitors.Person;
import org.tcp.grupo01.models.competitors.Team;

public class NewTournamentControllerTest {

    @Test
    void testBuildTournament_LeaguePlayersSingleRound() {
        NewTournamentController c = new NewTournamentController();

        Tournament<?> t = c.buildTournament(
                "Champ A",
                "Pontos Corridos",
                "Jogadores",
                "Turno"
        );

        assertEquals("Champ A", t.getName());
        assertEquals(4, t.getParticipants().size());
        assertEquals(Person.class, t.getParticipants().get(0).getClass());
    }

    @Test
    void testBuildTournament_LeagueTeams_DoubleRound() {
        NewTournamentController c = new NewTournamentController();

        Tournament<?> t = c.buildTournament(
                "Champ B",
                "Pontos Corridos",
                "Times",
                "Turno e Returno"
        );

        assertEquals(2, t.getParticipants().size());
        assertEquals(Team.class, t.getParticipants().get(0).getClass());
    }

    @Test
    void testBuildTournament_SwissPlayers() {
        NewTournamentController c = new NewTournamentController();

        Tournament<?> t = c.buildTournament(
                "Swiss Test",
                "Suíço",
                "Jogadores",
                "Confronto Direto"
        );

        assertEquals(4, t.getParticipants().size());
        assertEquals(Person.class, t.getParticipants().get(0).getClass());
    }

    @Test
    void testBuildTournament_SwissTeams() {
        NewTournamentController c = new NewTournamentController();

        Tournament<?> t = c.buildTournament(
                "Swiss Team Test",
                "Suíço",
                "Times",
                "Mata-Mata"
        );

        assertEquals(2, t.getParticipants().size());
        assertEquals(Team.class, t.getParticipants().get(0).getClass());
    }

    @Test
    void testBuildTournament_InvalidCompetition() {
        NewTournamentController c = new NewTournamentController();

        assertThrows(IllegalArgumentException.class, () ->
                c.buildTournament("X", "Inexistente", "Jogadores", "Turno")
        );
    }

    @Test
    void testBuildTournament_InvalidCompetitorType() {
        NewTournamentController c = new NewTournamentController();

        assertThrows(IllegalArgumentException.class, () ->
                c.buildTournament("Teste", "Pontos Corridos", "Robôs", "Turno")
        );
    }

    @Test
    void testBuildTournament_NullFields_NoException() {
        NewTournamentController c = new NewTournamentController();

        // UI deveria bloquear isso → aqui NÃO deve lançar exceção
        assertDoesNotThrow(() ->
                c.buildTournament(null, "Pontos Corridos", "Jogadores", "Turno")
        );
    }

    @Test
    void testBuildTournament_NullFormat_NoException() {
        NewTournamentController c = new NewTournamentController();

        assertDoesNotThrow(() ->
                c.buildTournament("Nome", "Pontos Corridos", "Jogadores", null)
        );
    }
}
