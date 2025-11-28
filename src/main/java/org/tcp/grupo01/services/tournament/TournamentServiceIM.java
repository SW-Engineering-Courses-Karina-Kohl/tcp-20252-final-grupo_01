package org.tcp.grupo01.services.tournament;

import org.tcp.grupo01.models.EventStatus;
import org.tcp.grupo01.models.Match;
import org.tcp.grupo01.models.Tournament;
import org.tcp.grupo01.models.competitors.Person;
import org.tcp.grupo01.services.pairing.Knockout;
import org.tcp.grupo01.services.pairing.League;
import org.tcp.grupo01.services.pairing.Swiss;

import java.util.*;

public class TournamentServiceIM implements TournamentService {
    private static TournamentServiceIM instance;
    private final Map<UUID, Tournament<?>> dataStore = new HashMap<>();

    public TournamentServiceIM() {
        initializeDummyData();
    }

    public static TournamentServiceIM getInstance() {
        if (instance == null) {
            instance = new TournamentServiceIM();
        }
        return instance;
    }

    public void initializeDummyData() {
        ArrayList<Person> players = new ArrayList<>();
        players.add(new Person("Alice"));
        players.add(new Person("Bob"));
        players.add(new Person("Carol"));
        players.add(new Person("David"));
        players.add(new Person("Bob"));
        players.add(new Person("Carol"));
        players.add(new Person("David"));
        players.add(new Person("Bob"));
        players.add(new Person("Carol"));
        players.add(new Person("David"));
        players.add(new Person("Bob"));
        players.add(new Person("Carol"));
        players.add(new Person("David"));
        players.add(new Person("John"));
        players.add(new Person("John"));
        players.add(new Person("John"));


        League<Person> league = new League<>(true, Match::betweenPeople);
        this.add(Tournament.createForPeople("Pontos Corridos", league, players));

        Knockout<Person> knockout = new Knockout<>(Match::betweenPeople);
        this.add(Tournament.createForPeople("Mata-mata", knockout, players));

        Swiss<Person> swiss = new Swiss<>(3, 3, Match::betweenPeople);
        this.add(Tournament.createForPeople("Suíço", swiss, players));
    }

    public void clear() {
        this.dataStore.clear();
    }

    @Override
    public List<Tournament<?>> getAll() {
        return new ArrayList<>(dataStore.values());
    }

    @Override
    public Tournament<?> getById(UUID id) {
        return dataStore.get(id);
    }

    @Override
    public UUID add(Tournament<?> tournament) {
        UUID newId = UUID.randomUUID();
        Tournament<?> identifiedTournament = tournament.withId(newId);

        dataStore.put(newId, identifiedTournament);
        return newId;
    }

    @Override
    public void delete(UUID id) {
        dataStore.remove(id);
    }

    @Override
    public void updateStatus(UUID id, EventStatus newStatus) {
        Tournament<?> t = dataStore.get(id);
        if (t != null) {
            t.setStatus(newStatus);
        } else {
            throw new IllegalArgumentException("Torneio não encontrado: " + id);
        }
    }
}