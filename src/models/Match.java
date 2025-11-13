package models;

import models.competitors.Competitor;
import models.competitors.Person;
import models.competitors.Team;

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

    // Factories
    public static Match<Person> betweenPeople(Person a, Person b) {
        return new Match<>(a, b);
    }
    public static Match<Team> betweenTeams(Team a, Team b) {
        return new Match<>(a, b);
    }

    public int getId() { return id; }
    public T getCompetitorA() { return competitorA; }
    // public void setCompetitorA(T competitorA) { this.competitorA = competitorA; }
    public T getCompetitorB() { return competitorB; }
    // public void setCompetitorB(T competitorB) { this.competitorB = competitorB; }

    public int getScoreA() { return scoreA; }
    public int getScoreB() { return scoreB; }
    public void setScoreA(int scoreA) { this.scoreA = scoreA; }
    public void setScoreB(int scoreB) { this.scoreB = scoreB; }
    
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public T getWinner() { return winner; }
    public void setWinner(T winner) { this.winner = winner; }

    public EventStatus getStatus() { return status; }
    public void setStatus(EventStatus status) { this.status = status; }

    public Place getPlace() { return place; }
    public void setPlace(Place place) { this.place = place; }

    public void decideWinnerByScore() {
        // if (scoreA == scoreB) throw new IllegalStateException("tie score, cannot decide");
        winner = scoreA > scoreB ? competitorA : competitorB;
        status = EventStatus.FINISHED;
    }
}
