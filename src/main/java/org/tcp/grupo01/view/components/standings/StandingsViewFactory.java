package org.tcp.grupo01.view.components.standings;

import javafx.scene.Node;
import javafx.scene.control.Label;
import org.tcp.grupo01.models.Tournament;
import org.tcp.grupo01.services.pairing.League;

public class StandingsViewFactory {
    public static Node createViewFor(Tournament<?> tournament) {
        StandingsViewStrategy strategy;

        if (tournament.getPairing() instanceof League) {
            strategy = new LeagueStandingsView();
        }
        // else if (tournament.getPairing() instanceof Swiss) {
        //     strategy = new SwissStandingsView();
        // }
        /* future:
        else if (tournament.getPairing() instanceof Elimination) {
            strategy = new BracketView();
        }
        */
        else {
            return new Label("Visualização de classificação não implementada para este formato.");
        }

        return strategy.render(tournament);
    }
}