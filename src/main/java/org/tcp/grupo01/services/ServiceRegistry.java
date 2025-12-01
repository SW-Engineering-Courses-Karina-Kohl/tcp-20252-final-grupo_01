package org.tcp.grupo01.services;

import org.tcp.grupo01.models.Match;
import org.tcp.grupo01.models.Tournament;
import org.tcp.grupo01.models.competitors.Person;
import org.tcp.grupo01.models.competitors.Team;
import org.tcp.grupo01.services.competitors.CompetitorService;
import org.tcp.grupo01.services.competitors.CompetitorServiceIM;
import org.tcp.grupo01.services.pairing.Knockout;
import org.tcp.grupo01.services.pairing.League;
import org.tcp.grupo01.services.pairing.Swiss;
import org.tcp.grupo01.services.tournament.TournamentService;
import org.tcp.grupo01.services.tournament.TournamentServiceIM;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public final class ServiceRegistry {

    private static final CompetitorService<Person> personService = new CompetitorServiceIM<>();
    private static final CompetitorService<Team> teamService = new CompetitorServiceIM<>();
    private static final TournamentService tournamentService = TournamentServiceIM.getInstance();

    private ServiceRegistry() {}

    public static CompetitorService<Person> persons() { return personService; }
    public static CompetitorService<Team> teams() { return teamService; }
    public static TournamentService tournaments() { return tournamentService; }

    public static void initializeDummyData() {
        List<Person> players = initDummyPersons();
        List<Team> teams = initDummyTeams(players);
        initDummyTournaments(players, teams);
    }

    private static List<Person> initDummyPersons() {

        Person p1  = new Person("Alice Johnson",   "123.456.789-01", "(11) 91234-1111", LocalDate.of(1994, 3, 12));
        Person p2  = new Person("Bob Martinez",    "987.654.321-00", "(11) 99811-2222", LocalDate.of(1990, 7, 22));
        Person p3  = new Person("Carla Ribeiro",   "321.654.987-44", "(21) 99122-3333", LocalDate.of(1998, 1, 5));
        Person p4  = new Person("Daniel Costa",    "654.987.321-55", "(41) 99566-4444", LocalDate.of(1988, 12, 15));
        Person p5  = new Person("Evelyn Tanaka",   "159.753.486-20", "(31) 99244-5555", LocalDate.of(1996, 5, 3));
        Person p6  = new Person("Frank O’Connor",  "753.951.258-30", "(51) 99888-6666", LocalDate.of(1987, 9, 17));
        Person p7  = new Person("Gustavo Lima",    "111.222.333-44", "(41) 99977-7777", LocalDate.of(1999, 4, 20));
        Person p8  = new Person("Hana Suzuki",     "222.333.444-55", "(21) 99455-8888", LocalDate.of(1995, 2, 28));
        Person p9  = new Person("Igor Petrov",     "333.444.555-66", "(31) 98777-9999", LocalDate.of(1989, 11, 30));
        Person p10 = new Person("Julia Mendes",    "444.555.666-77", "(11) 99333-0000", LocalDate.of(2000, 6, 10));
        Person p11 = new Person("Kevin Hart",      "555.666.777-88", "(71) 99111-1212", LocalDate.of(1991, 3, 14));
        Person p12 = new Person("Letícia Duarte",  "666.777.888-99", "(48) 99444-3434", LocalDate.of(1997, 10, 18));
        Person p13 = new Person("Marcos Albuquerque", "777.888.999-00", "(61) 99123-4545", LocalDate.of(1993, 8, 25));
        Person p14 = new Person("Natalia Fernandez",  "888.999.000-11", "(62) 99234-5656", LocalDate.of(1994, 12, 2));
        Person p15 = new Person("Oliver Schmidt",     "999.000.111-22", "(51) 99789-6767", LocalDate.of(1986, 4, 11));

        List<Person> players = new ArrayList<>(List.of(
                p1, p2, p3, p4, p5,
                p6, p7, p8, p9, p10,
                p11, p12, p12, p13, p14, p15
        ));

        players.forEach(personService::add);

        return players;
    }

    private static List<Team> initDummyTeams(List<Person> players) {

        Team sunsetTigers = new Team("Sunset Tigers");
        sunsetTigers.addPlayer(players.get(0));
        sunsetTigers.addPlayer(players.get(1));
        sunsetTigers.addPlayer(players.get(2));

        Team northDragons = new Team("North Dragons");
        northDragons.addPlayer(players.get(3));
        northDragons.addPlayer(players.get(4));
        northDragons.addPlayer(players.get(5));

        Team ironWolves = new Team("Iron Wolves");
        ironWolves.addPlayer(players.get(6));
        ironWolves.addPlayer(players.get(7));
        ironWolves.addPlayer(players.get(8));

        Team crimsonPhoenix = new Team("Crimson Phoenix");
        crimsonPhoenix.addPlayer(players.get(9));
        crimsonPhoenix.addPlayer(players.get(10));
        crimsonPhoenix.addPlayer(players.get(11));

        List<Team> teams = List.of(
                sunsetTigers, northDragons, ironWolves, crimsonPhoenix
        );

        teams.forEach(teamService::add);

        return teams;
    }

    private static void initDummyTournaments(List<Person> players, List<Team> teams) {

        League<Person> leaguePlayers = new League<>(false, Match::betweenPeople);
        tournaments().add(Tournament.createForPeople(
                "Liga Continental 2025",
                leaguePlayers,
                new ArrayList<>(players.subList(0, 8))
        ));

        Swiss<Person> swissPlayers = new Swiss<>(4, 4, Match::betweenPeople);
        tournaments().add(Tournament.createForPeople(
                "Swiss Cup - Players Edition",
                swissPlayers,
                new ArrayList<>(players)
        ));

        Knockout<Person> koPlayers = new Knockout<>(Match::betweenPeople);
        tournaments().add(Tournament.createForPeople(
                "Player Championship Knockout",
                koPlayers,
                new ArrayList<>(players.subList(0, 8))
        ));

        League<Team> leagueTeams = new League<>(true, Match::betweenTeams);
        tournaments().add(Tournament.createForTeams(
                "Liga Nacional de Clubes",
                leagueTeams,
                new ArrayList<>(teams)
        ));

        Swiss<Team> swissTeams = new Swiss<>(3, 3, Match::betweenTeams);
        tournaments().add(Tournament.createForTeams(
                "Copa Suíça Interclubes",
                swissTeams,
                new ArrayList<>(teams)
        ));

        Knockout<Team> koTeams = new Knockout<>(Match::betweenTeams);
        tournaments().add(Tournament.createForTeams(
                "Supercopa Eliminatória",
                koTeams,
                new ArrayList<>(teams)
        ));
    }
}
