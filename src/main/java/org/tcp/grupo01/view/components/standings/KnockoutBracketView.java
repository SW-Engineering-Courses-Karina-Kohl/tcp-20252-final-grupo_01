package org.tcp.grupo01.view.components.standings;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.tcp.grupo01.models.Match;
import org.tcp.grupo01.models.Tournament;
import org.tcp.grupo01.models.competitors.Competitor;

import java.util.List;

public class KnockoutBracketView implements StandingsViewStrategy {

    @Override
    public Node render(Tournament<?> tournament) {
        HBox root = new HBox(30);
        root.setPadding(new Insets(15));
        root.setAlignment(Pos.TOP_CENTER);

        root.getStylesheets().add(
                getClass()
                        .getResource("/org/tcp/grupo01/styles/knockout.css")
                        .toExternalForm()
        );

        @SuppressWarnings("unchecked")
        List<List<Match<?>>> rounds = (List<List<Match<?>>>) (List<?>) tournament.getRounds();

        int totalRounds = rounds.size();
        if (totalRounds == 0) {
            return new Label("Nenhuma rodada gerada ainda.");
        }

        for (int roundIndex = 0; roundIndex < totalRounds; roundIndex++) {
            List<Match<?>> round = rounds.get(roundIndex);

            VBox column = new VBox(15);
            column.setAlignment(Pos.TOP_CENTER);
            column.getStyleClass().add("bracket-column");

            Label header = new Label(getRoundName(round.size(), roundIndex));
            header.getStyleClass().add("bracket-round-title");
            column.getChildren().add(header);

            for (Match<?> match : round) {
                column.getChildren().add(createMatchCard(match));
            }

            root.getChildren().add(column);

            if (roundIndex < totalRounds - 1) {
                root.getChildren().add(new Separator());
            }
        }

        return root;
    }

    private Node createMatchCard(Match<?> m) {
        VBox box = new VBox(4);
        box.setAlignment(Pos.CENTER_LEFT);
        box.getStyleClass().add("bracket-match");

        Competitor a = m.getCompetitorA();
        Competitor b = m.getCompetitorB();

        String nameA = a != null ? a.getName() : "A definir";
        String nameB = b != null ? b.getName() : "A definir";

        Label lblA = new Label(nameA);
        Label lblB = new Label(nameB);
        lblA.getStyleClass().add("bracket-player");
        lblB.getStyleClass().add("bracket-player");

        if (m.getWinner() != null) {
            if (a != null && m.getWinner().equals(a)) {
                lblA.getStyleClass().add("winner");
            } else if (b != null && m.getWinner().equals(b)) {
                lblB.getStyleClass().add("winner");
            }
        }

        box.getChildren().addAll(lblA, lblB);
        return box;
    }

    private String getRoundName(int matchCount, int roundIndex) {
        return switch (matchCount) {
            case 1 -> "Final";
            case 2 -> "Semifinal";
            case 4 -> "Quartas de final";
            case 8 -> "Oitavas de final";
            default -> "Rodada " + (roundIndex + 1);
        };
    }
}
