package model;

import java.util.ArrayList;

public final class Team extends Competitor {
    private final ArrayList<Person> players = new ArrayList<>();
    private Person coach;

    private Team(String name) {
        super(name);
    }

    // public static Team of(String name) {
    //     return new Team(name);
    // }

    public ArrayList<Person> getPlayers() { return new ArrayList<>(players); }

    public void addPlayer(Person p) {
        if (!players.contains(p)) players.add(p);
    }

    public void removePlayer(Person p) { players.remove(p); }

    public Person getCoach() { return coach; }
    public void setCoach(Person coach) { this.coach = coach; }
}
