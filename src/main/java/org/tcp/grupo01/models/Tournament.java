package org.tcp.grupo01.models;

import org.tcp.grupo01.models.competitors.Competitor;
import org.tcp.grupo01.models.competitors.Person;
import org.tcp.grupo01.models.competitors.Team;
import org.tcp.grupo01.services.pairing.Pairing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public final class Tournament<T extends Competitor> {

    private final UUID id;
    private String name;
    private ArrayList<T> participants;
    private final Pairing<T> pairing;
    private EventStatus status = EventStatus.PLANNING;

    private final List<List<Match<T>>> rounds = new ArrayList<>();

    private Tournament(UUID id, String name, Pairing<T> pairing, ArrayList<T> participants) {
        this.id = id;
        this.name = name;
        this.pairing = pairing;
        this.participants = new ArrayList<>(participants);
    }

    public static Tournament<Person> createForPeople(String name, Pairing<Person> pairing, ArrayList<Person> participants) {
        return new Tournament<>(null, name, pairing, participants);
    }

    public static Tournament<Team> createForTeams(String name, Pairing<Team> pairing, ArrayList<Team> participants) {
        return new Tournament<>(null, name, pairing, participants);
    }

    public Tournament<T> withId(UUID id) {
        if (this.id != null) return this;

        Tournament<T> copy = new Tournament<>(id, this.name, this.pairing, this.participants);
        copy.setStatus(this.status);
        copy.rounds.addAll(this.rounds);
        return copy;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public EventStatus getStatus() {
        return status;
    }

    public void setStatus(EventStatus status) {
        this.status = status;
    }

    public Pairing<T> getPairing() {
        return pairing;
    }

    public List<T> getParticipants() {
        return Collections.unmodifiableList(participants);
    }

    public List<List<Match<T>>> getRounds() {
        return Collections.unmodifiableList(rounds);
    }

    public void generateNextMatches() {
        List<List<Match<T>>> generatedRounds =
                pairing.generateRounds(participants, rounds);

        this.rounds.clear();
        this.rounds.addAll(generatedRounds);

        if (status == EventStatus.PLANNING && !rounds.isEmpty()) {
            status = EventStatus.RUNNING;
        }
    }

    public int getRoundCount() {
        return rounds.size();
    }

    public void replaceParticipants(List<? extends T> newList) {
        this.participants = new ArrayList<>(newList);
    }
}
