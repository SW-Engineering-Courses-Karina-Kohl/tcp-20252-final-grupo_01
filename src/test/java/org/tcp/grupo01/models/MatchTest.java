package org.tcp.grupo01.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.tcp.grupo01.models.competitors.Person;
import org.tcp.grupo01.models.competitors.Team;

import static org.junit.jupiter.api.Assertions.*;

class MatchTest {

    private Team teamA;
    private Team teamB;
    private Match<Team> match;

    @BeforeEach
    void setUp() {
        teamA = new Team("Time A");
        teamB = new Team("Time B");
        match = Match.betweenTeams(teamA, teamB);
    }

    @Test
    @DisplayName("Should initialize in PLANNING state with zero scores")
    void shouldInitializeCorrectly() {
        assertEquals(EventStatus.PLANNING, match.getStatus());
        assertEquals(0, match.getScoreA());
        assertEquals(0, match.getScoreB());
        assertEquals(teamA, match.getCompetitorA());
        assertEquals(teamB, match.getCompetitorB());
        assertNull(match.getWinner(), "Winner should be null initially");
    }

    @Test
    @DisplayName("Should create Match between People correctly")
    void shouldCreateMatchBetweenPeople() {
        Person p1 = new Person("Alice");
        Person p2 = new Person("Bob");
        Match<Person> matchP = Match.betweenPeople(p1, p2);

        assertNotNull(matchP);
        assertEquals("Alice", matchP.getCompetitorA().getName());
        assertEquals(EventStatus.PLANNING, matchP.getStatus());
    }

    @Test
    @DisplayName("Should update status to RUNNING")
    void shouldUpdateStatusToRunning() {
        match.updateResult(0, 0, EventStatus.RUNNING);
        assertEquals(EventStatus.RUNNING, match.getStatus());
    }

    @Test
    @DisplayName("Should update scores while RUNNING")
    void shouldUpdateScoresWhileRunning() {
        match.updateResult(0, 0, EventStatus.RUNNING); // Start
        match.updateResult(1, 0, EventStatus.RUNNING); // Goal A
        match.updateResult(1, 1, EventStatus.RUNNING); // Goal B

        assertEquals(1, match.getScoreA());
        assertEquals(1, match.getScoreB());
        assertEquals(EventStatus.RUNNING, match.getStatus());
        assertNull(match.getWinner(), "Winner should still be null while running");
    }

    @Test
    @DisplayName("Should finish match and set Competitor A as winner")
    void shouldFinishMatchWithWinnerA() {
        match.updateResult(2, 1, EventStatus.FINISHED);

        assertEquals(EventStatus.FINISHED, match.getStatus());
        assertEquals(2, match.getScoreA());
        assertEquals(1, match.getScoreB());
        assertEquals(teamA, match.getWinner());
    }

    @Test
    @DisplayName("Should finish match and set Competitor B as winner")
    void shouldFinishMatchWithWinnerB() {
        match.updateResult(0, 3, EventStatus.FINISHED);

        assertEquals(EventStatus.FINISHED, match.getStatus());
        assertEquals(teamB, match.getWinner());
    }

    @Test
    @DisplayName("Should throw exception when scores are negative")
    void shouldThrowExceptionForNegativeScores() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            match.updateResult(-1, 0, EventStatus.RUNNING);
        });

        assertEquals("O placar não pode ser negativo.", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when trying to finish with a tie")
    void shouldThrowExceptionForTieOnFinish() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            match.updateResult(1, 1, EventStatus.FINISHED);
        });

        assertEquals("Empates não são permitidos para finalizar a partida.", exception.getMessage());

        assertEquals(EventStatus.PLANNING, match.getStatus());
    }

    @Test
    @DisplayName("Should allow tie if status is NOT finished")
    void shouldAllowTieWhileRunning() {
        assertDoesNotThrow(() -> {
            match.updateResult(2, 2, EventStatus.RUNNING);
        });
        assertEquals(2, match.getScoreA());
        assertEquals(2, match.getScoreB());
    }

    @Test
    @DisplayName("Should throw exception when trying to edit an already FINISHED match")
    void shouldThrowExceptionWhenEditingFinishedMatch() {
        match.updateResult(1, 0, EventStatus.FINISHED);
        assertEquals(EventStatus.FINISHED, match.getStatus());

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            match.updateResult(2, 0, EventStatus.FINISHED);
        });

        assertEquals("Não é possível alterar uma partida já finalizada.", exception.getMessage());
    }


    @Test
    @DisplayName("Should throw exception when setting score for PLANNING match")
    void shouldThrowExceptionWhenSettingScoreForPlanningMatch() {
        // Tenta definir 1x0 mas manter o status como A CONFIRMAR (PLANNING)
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            match.updateResult(1, 0, EventStatus.PLANNING);
        });

        assertEquals("Não é possível definir pontuação para uma partida que não iniciou.", exception.getMessage());
    }

    @Test
    @DisplayName("Should allow 0x0 for PLANNING match")
    void shouldAllowZeroZeroForPlanningMatch() {
        assertDoesNotThrow(() -> {
            match.updateResult(0, 0, EventStatus.PLANNING);
        });
        assertEquals(EventStatus.PLANNING, match.getStatus());
        assertEquals(0, match.getScoreA());
    }

    @Test
    @DisplayName("Should allow setting score if changing status to RUNNING")
    void shouldAllowScoreIfStatusIsRunning() {
        assertDoesNotThrow(() -> {
            match.updateResult(1, 0, EventStatus.RUNNING);
        });
        assertEquals(EventStatus.RUNNING, match.getStatus());
        assertEquals(1, match.getScoreA());
    }
}