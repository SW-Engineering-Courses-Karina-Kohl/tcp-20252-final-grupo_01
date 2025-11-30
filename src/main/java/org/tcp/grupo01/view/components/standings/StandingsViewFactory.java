package org.tcp.grupo01.view.components.standings;

import javafx.scene.Node;
import javafx.scene.control.Label;
import org.tcp.grupo01.models.Tournament;
import org.tcp.grupo01.services.pairing.Knockout;
import org.tcp.grupo01.services.pairing.League;
import org.tcp.grupo01.services.pairing.Swiss;

public class StandingsViewFactory {
    public static Node createViewFor(Tournament<?> tournament) {
        StandingsViewStrategy strategy;

        switch (tournament.getPairing()) {
            case League league -> strategy = new LeagueStandingsView();
            case Swiss swiss -> strategy = new SwissBucketsView();
            case Knockout knockout -> strategy = new KnockoutBracketView();
            case null, default -> {
                return new Label("Visualização de classificação não implementada para este formato.");
            }
        }

        return strategy.render(tournament);
    }
}