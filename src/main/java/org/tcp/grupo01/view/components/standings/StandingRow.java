package org.tcp.grupo01.view.components.standings;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import org.tcp.grupo01.models.stats.CompetitorStats;

public class StandingRow extends HBox {

    public StandingRow(int rank, CompetitorStats stats) {
        this.getStyleClass().add("standing-row");
        this.setAlignment(Pos.CENTER_LEFT);

        // --- 1. RANKING (#) ---
        Label lblRank = new Label(String.valueOf(rank));
        lblRank.getStyleClass().add("standing-rank");

        // Estilização específica para Top 3 (bolinhas coloridas)
        if (rank == 1) lblRank.getStyleClass().add("rank-1");
        else if (rank == 2) lblRank.getStyleClass().add("rank-2");
        else if (rank == 3) lblRank.getStyleClass().add("rank-3");

        StackPane rankContainer = new StackPane(lblRank);
        rankContainer.setPrefWidth(40);
        rankContainer.setAlignment(Pos.CENTER);

        // --- 2. TIME ---
        Label lblName = new Label(stats.getCompetitor().getName());
        lblName.getStyleClass().add("standing-team-name");
        HBox nameContainer = new HBox(lblName);
        nameContainer.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(nameContainer, Priority.ALWAYS); // O nome ocupa o espaço sobrando

        // --- 3. DADOS (Colunas Fixas) ---
        // PTS, J, V, D, GP, GC, SG
        Label lblPts = createCell(String.valueOf(stats.getPoints()), "standing-pts", 50);
        Label lblJ = createCell(String.valueOf(stats.getMatchesPlayed()), "standing-cell", 40);
        Label lblV = createCell(String.valueOf(stats.getWins()), "standing-cell", 40);
        Label lblD = createCell(String.valueOf(stats.getLosses()), "standing-cell", 40);
        Label lblGP = createCell(String.valueOf(stats.getPointsFor()), "standing-cell", 40);
        Label lblGC = createCell(String.valueOf(stats.getPointsAgainst()), "standing-cell", 40);

        // SG com sinal positivo
        int sg = stats.getGoalDifference();
        String sgText = (sg > 0 ? "+" : "") + sg;
        Label lblSG = createCell(sgText, "standing-sg", 40);

        this.getChildren().addAll(
                rankContainer,
                nameContainer,
                lblPts, lblJ, lblV, lblD, lblGP, lblGC, lblSG
        );
    }

    private Label createCell(String text, String styleClass, double width) {
        Label lbl = new Label(text);
        lbl.getStyleClass().add(styleClass);
        lbl.setPrefWidth(width);
        lbl.setAlignment(Pos.CENTER);
        return lbl;
    }
}