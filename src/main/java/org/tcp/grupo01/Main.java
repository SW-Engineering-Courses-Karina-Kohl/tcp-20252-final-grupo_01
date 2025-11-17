package org.tcp.grupo01;

import org.tcp.grupo01.models.Match;
import org.tcp.grupo01.models.competitors.Person;
import org.tcp.grupo01.services.pairings.League;
import org.tcp.grupo01.ui.MainWindow;

import javax.swing.*;
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
    }
}

