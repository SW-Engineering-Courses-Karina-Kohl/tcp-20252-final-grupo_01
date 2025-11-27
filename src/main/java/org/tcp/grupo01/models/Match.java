package org.tcp.grupo01.models;

import org.tcp.grupo01.models.competitors.Competitor;
import org.tcp.grupo01.models.competitors.Person;
import org.tcp.grupo01.models.competitors.Team;

import java.time.LocalDateTime;

// Partida entre competidores do mesmo tipo, Construtor privado
public final class Match<T extends Competitor> {

    private static int nextId = 1;
    private final int id = nextId++;
    private final T competitorA;
    private final T competitorB;
    private int scoreA;
    private int scoreB;
    private LocalDateTime startTime;
    private T winner;
    private EventStatus status = EventStatus.PLANNING;
    private Place place;

    private Match(T a, T b) {
        this.competitorA = a;
        this.competitorB = b;
    }

    public static Match<Person> betweenPeople(Person a, Person b) {
        return new Match<>(a, b);
    }
    public static Match<Team> betweenTeams(Team a, Team b) {
        return new Match<>(a, b);
    }

    public int getId() { return id; }
    public T getCompetitorA() { return competitorA; }
    public T getCompetitorB() { return competitorB; }

    public int getScoreA() { return scoreA; }
    public int getScoreB() { return scoreB; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public T getWinner() { return winner; }

    public EventStatus getStatus() { return status; }

    public Place getPlace() { return place; }
    public void setPlace(Place place) { this.place = place; }

    public void updateResult(int newScoreA, int newScoreB, EventStatus newStatus) {
        if (this.status == EventStatus.FINISHED) {
            throw new IllegalStateException("Não é possível alterar uma partida já finalizada.");
        }

        if (newScoreA < 0 || newScoreB < 0) {
            throw new IllegalArgumentException("O placar não pode ser negativo.");
        }

        if (newStatus == EventStatus.PLANNING && (newScoreA != 0 || newScoreB != 0)) {
            throw new IllegalArgumentException("Não é possível definir pontuação para uma partida que não iniciou.");
        }

        // 4. No tie
        if (newStatus == EventStatus.FINISHED && newScoreA == newScoreB) {
            throw new IllegalArgumentException("Empates não são permitidos para finalizar a partida.");
        }

        this.scoreA = newScoreA;
        this.scoreB = newScoreB;
        this.status = newStatus;

        if (this.status == EventStatus.FINISHED) {
            this.winner = (scoreA > scoreB) ? competitorA : competitorB;
        }
    }
}
