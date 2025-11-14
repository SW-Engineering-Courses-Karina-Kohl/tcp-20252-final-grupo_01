import models.Match;
import models.competitors.Person;
import services.pairings.League;
import services.pairings.Swiss;
import ui.MainWindow;

import javax.swing.SwingUtilities;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainWindow window = new MainWindow();
            window.setVisible(true);
        });

        List<Person> players = new ArrayList<>();
        players.add(new Person("Alice"));
        players.add(new Person("Bob"));
        players.add(new Person("Carol"));
        players.add(new Person("David"));

        League<Person> league = new League<>(
                true,
                Match::betweenPeople
        );

        System.out.println("Gerando tabela para " + players.size() + " participantes...\n");
        List<List<Match<Person>>> allRounds = league.generateRounds(players, new ArrayList<>());

        int roundNumber = 1;
        for (List<Match<Person>> round : allRounds) {
            System.out.println("--- RODADA " + roundNumber + " ---");
            for (Match<Person> match : round) {
                System.out.println(
                        match.getCompetitorA().getName() + " vs " + match.getCompetitorB().getName()
                );
            }
            System.out.println();
            roundNumber++;
        }
        System.out.println("Total de rodadas geradas: " + allRounds.size());


        Swiss<Person> swiss = new Swiss<>(
                2,
                2,
                Match::betweenPeople
        );

        List<List<Match<Person>>> rounds = new ArrayList<>();
        System.out.println("\n\nGerando rodadas do sistema suíço...\n");
        rounds = swiss.generateRounds(players, rounds);
        rounds = swiss.generateRounds(players, rounds);
        rounds = swiss.generateRounds(players, rounds);

        roundNumber = 1;
        for (List<Match<Person>> round : rounds) {
            System.out.println("--- RODADA " + roundNumber + " ---");
            for (Match<Person> match : round) {
                System.out.println(match.getCompetitorA().getName() + " vs " + match.getCompetitorB().getName());
            }
            System.out.println();
            roundNumber++;
        }
        System.out.println("Total de rodadas geradas: " + rounds.size());
    }
}
