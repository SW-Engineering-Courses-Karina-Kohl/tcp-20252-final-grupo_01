package org.tcp.grupo01.view.components;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import org.tcp.grupo01.models.EventStatus;
import org.tcp.grupo01.models.Match;

import java.util.function.Consumer;

public class MatchCard extends HBox {

    public MatchCard(Match<?> match, Consumer<Match<?>> onClickAction) {
        this.getStyleClass().add("match-card");
        this.setAlignment(Pos.CENTER_LEFT);
        this.setSpacing(20);

        // --- GRID DE PLACAR ---
        GridPane scoreBoard = new GridPane();
        scoreBoard.setVgap(10);
        scoreBoard.setHgap(20);

        ColumnConstraints colTeam = new ColumnConstraints();
        colTeam.setHgrow(Priority.ALWAYS);
        colTeam.setMinWidth(200);

        ColumnConstraints colScore = new ColumnConstraints();
        colScore.setMinWidth(30);
        colScore.setHalignment(javafx.geometry.HPos.RIGHT);

        scoreBoard.getColumnConstraints().addAll(colTeam, colScore);

        // Extração de dados limpa
        String teamA = match.getCompetitorA().getName();
        String teamB = match.getCompetitorB().getName();

        // Se estiver em planejamento, mostra traço. Senão, mostra placar.
        boolean isPlanning = match.getStatus() == EventStatus.PLANNING;
        String scoreAText = isPlanning ? "-" : String.valueOf(match.getScoreA());
        String scoreBText = isPlanning ? "-" : String.valueOf(match.getScoreB());

        scoreBoard.add(createTeamLabel(teamA), 0, 0);
        scoreBoard.add(createScoreLabel(scoreAText), 1, 0);
        scoreBoard.add(createTeamLabel(teamB), 0, 1);
        scoreBoard.add(createScoreLabel(scoreBText), 1, 1);

        // --- ESPAÇADOR ---
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // --- BADGE DE STATUS ---
        // Refatorado: Usa o texto do Enum, aplica CSS condicional apenas para cor
        Label statusBadge = new Label(match.getStatus().toString());
        statusBadge.getStyleClass().add("status-badge");

        if (match.getStatus() == EventStatus.RUNNING) {
            statusBadge.getStyleClass().add("status-badge-running");
        }

        this.getChildren().addAll(scoreBoard, spacer, statusBadge);

        // --- INTERAÇÃO ---
        this.setCursor(javafx.scene.Cursor.HAND);
        this.setOnMouseClicked(e -> onClickAction.accept(match));
    }

    // Helpers privados para manter o construtor limpo
    private Label createTeamLabel(String text) {
        Label l = new Label(text);
        l.getStyleClass().add("match-team-name");
        return l;
    }

    private Label createScoreLabel(String text) {
        Label l = new Label(text);
        l.getStyleClass().add("match-score");
        return l;
    }
}