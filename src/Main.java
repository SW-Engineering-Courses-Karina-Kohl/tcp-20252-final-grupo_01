import models.Match;
import models.competitors.Person;
import models.competitors.Team;
import services.pairings.League;
import ui.MainWindow;

import javax.swing.SwingUtilities;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainWindow window = new MainWindow();
            window.setVisible(true);
        });

        League<Person> league = new League<>(
                true,
                Match::betweenPeople
        );

        ArrayList<Person> players = new ArrayList<>();
        players.add(new Person("Alice"));
        players.add(new Person("Bob"));
        players.add(new Person("Carol"));

        var matches = league.generateNextRound(players, new ArrayList<>());

        System.out.println("Geradas " + matches.size() + " partidas:");
        for (var match : matches) {
            System.out.println(match.getCompetitorA().getName() + " vs " + match.getCompetitorB().getName());
        }

    }
}
