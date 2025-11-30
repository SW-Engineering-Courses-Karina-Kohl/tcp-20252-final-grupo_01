package org.tcp.grupo01.view.components.standings;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.tcp.grupo01.models.Match;
import org.tcp.grupo01.models.Tournament;
import org.tcp.grupo01.models.competitors.Competitor;

import java.util.List;
import java.util.Objects;

public class KnockoutBracketView implements StandingsViewStrategy {

    @Override
    public Node render(Tournament<?> tournament) {
        HBox bracketContent = new HBox(40);
        bracketContent.setPadding(new Insets(20));

        bracketContent.setAlignment(Pos.TOP_LEFT);
        bracketContent.setFillHeight(true);

        bracketContent.getStylesheets().add(
                Objects.requireNonNull(getClass()
                                .getResource("/org/tcp/grupo01/styles/knockout.css"))
                        .toExternalForm()
        );

        List<List<Match<?>>> rounds = (List<List<Match<?>>>) (List<?>) tournament.getRounds();
        int totalRounds = rounds.size();

        if (totalRounds == 0) {
            return new Label("Nenhuma rodada gerada ainda.");
        }

        for (int roundIndex = 0; roundIndex < totalRounds; roundIndex++) {
            List<Match<?>> round = rounds.get(roundIndex);

            // Coluna: Título no topo, conteúdo flexível
            VBox column = new VBox(20);
            column.getStyleClass().add("bracket-column");
            column.setAlignment(Pos.TOP_CENTER);

            Label header = new Label(getRoundName(round.size(), roundIndex));
            header.getStyleClass().add("bracket-round-title");
            column.getChildren().add(header);

            VBox matchesBox = new VBox(20);
            matchesBox.setAlignment(Pos.CENTER);

            VBox.setVgrow(matchesBox, Priority.ALWAYS);

            for (Match<?> match : round) {
                matchesBox.getChildren().add(createMatchCard(match));
            }

            column.getChildren().add(matchesBox);
            bracketContent.getChildren().add(column);
        }

        ScrollPane scrollPane = new ScrollPane(bracketContent);

        scrollPane.setFitToHeight(false);
        scrollPane.setFitToWidth(false);

        scrollPane.setPannable(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        Platform.runLater(() -> scrollPane.setHvalue(1.0));

        return scrollPane;
    }

    private Node createMatchCard(Match<?> m) {
        VBox box = new VBox(5);
        box.setAlignment(Pos.CENTER_LEFT);
        box.getStyleClass().add("bracket-match");
        box.setMinWidth(160);
        box.setMaxWidth(160);

        Competitor a = m.getCompetitorA();
        Competitor b = m.getCompetitorB();

        String nameA = a != null ? a.getName() : "A definir";
        String nameB = b != null ? b.getName() : "A definir";

        Label lblA = new Label(nameA);
        Label lblB = new Label(nameB);
        lblA.getStyleClass().add("bracket-player");
        lblB.getStyleClass().add("bracket-player");

        if (m.getWinner() != null) {
            if (m.getWinner().equals(a)) {
                lblA.getStyleClass().add("winner");
                lblB.getStyleClass().add("loser");
            } else if (m.getWinner().equals(b)) {
                lblB.getStyleClass().add("winner");
                lblA.getStyleClass().add("loser");
            }
        }

        box.getChildren().addAll(lblA, lblB);
        return box;
    }

    private String getRoundName(int matchCount, int roundIndex) {
        return switch (matchCount) {
            case 1 -> "Grande Final";
            case 2 -> "Semifinais";
            case 4 -> "Quartas de Final";
            case 8 -> "Oitavas de Final";
            default -> "Rodada " + (roundIndex + 1);
        };
    }
}