package model;

import java.util.ArrayList;
import java.util.Collections;

// competidores do mesmo tipo, Construtor privado

public final class Tournament<T extends Competitor> {

    private static int nextId = 1;
    private final int id = nextId++;
    private String name;
    private final ArrayList<T> participants;
    private final Pairing<T> pairing;
    private TournamentStatus status = TournamentStatus.PLANNING;
    private final ArrayList<Match<T>> matches = new ArrayList<>();

    private Tournament(String name, Pairing<T> pairing, ArrayList<T> participants) {
        this.name = name;
        this.pairing = pairing;
        this.participants = new ArrayList<>(participants);
    }

    // Factories
    public static Tournament<Person> createForPeople(String name, Pairing<Person> pairing, ArrayList<Person> participants) {
        return new Tournament<>(name, pairing, participants);
    }
    public static Tournament<Team> createForTeams(String name, Pairing<Team> pairing, ArrayList<Team> participants) {
        return new Tournament<>(name, pairing, participants);
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public TournamentStatus getStatus() { return status; }
    public void setStatus(TournamentStatus status) { this.status = status; }
    public ArrayList<T> getParticipants() { return Collections.unmodifiableList(participants); }
    public ArrayList<Match<T>> getMatches() { return Collections.unmodifiableList(matches); }

    public void nextRound() {
        ArrayList<Match<T>> round = pairing.generateNextRound(participants, matches);
        matches.addAll(round);
        status = TournamentStatus.RUNNING;
    }
}
