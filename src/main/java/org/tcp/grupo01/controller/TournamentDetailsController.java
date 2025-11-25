package org.tcp.grupo01.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.tcp.grupo01.models.Match;
import org.tcp.grupo01.models.Tournament;

import java.io.IOException;
import java.util.List;

public class TournamentDetailsController {

    @FXML private Label lblTournamentName, lblStatus, lblParticipantsCount, lblSectionTitle;
    @FXML private ScrollPane roundsContainer;
    @FXML private VBox matchesList, standingsContainer;

    // Controles de Navegação
    @FXML private HBox paginationControls;
    @FXML private Button btnTabRounds, btnTabStandings;
    @FXML private Button btnPrevRound, btnNextRound;

    private Tournament<?> tournament;
    private int currentRoundIndex = 0; // Estado da paginação

    public void setTournament(Tournament<?> tournament) {
        this.tournament = tournament;
        updateSidebarInfo();

        this.currentRoundIndex = 0;
        showRounds();
    }

    private void updateSidebarInfo() {
        if (tournament == null) return;
        lblTournamentName.setText(tournament.getName());
        lblStatus.setText("Status: " + tournament.getStatus());
        lblParticipantsCount.setText(tournament.getParticipants().size() + " participantes");
    }

    @FXML
    public void showRounds() {
        setTabActive(btnTabRounds, btnTabStandings);

        roundsContainer.setVisible(true);
        standingsContainer.setVisible(false);
        paginationControls.setVisible(true);

        renderCurrentRound();
    }

    @FXML
    public void showStandings() {
        setTabActive(btnTabStandings, btnTabRounds);

        roundsContainer.setVisible(false);
        standingsContainer.setVisible(true);
        paginationControls.setVisible(false);

        lblSectionTitle.setText("Classificação");
    }

    private void setTabActive(Button active, Button inactive) {
        active.getStyleClass().add("segment-button-active");
        inactive.getStyleClass().remove("segment-button-active");
    }

    // --- LÓGICA DE PAGINAÇÃO ---

    @FXML
    public void handleNextRound() {
        if (tournament != null && currentRoundIndex < tournament.getRounds().size() - 1) {
            currentRoundIndex++;
            renderCurrentRound();
        }
    }

    @FXML
    public void handlePrevRound() {
        if (currentRoundIndex > 0) {
            currentRoundIndex--;
            renderCurrentRound();
        }
    }

    private void renderCurrentRound() {
        matchesList.getChildren().clear();
        List<? extends List<? extends Match<?>>> allRounds = tournament.getRounds();

        if (allRounds.isEmpty()) {
            lblSectionTitle.setText("Rodadas");
            matchesList.getChildren().add(new Label("Nenhuma rodada gerada."));
            paginationControls.setVisible(false);
            return;
        }

        // Título Dinâmico
        lblSectionTitle.setText("Rodada " + (currentRoundIndex + 1));

        // Controle dos botões (Desabilitar se for inicio ou fim)
        btnPrevRound.setDisable(currentRoundIndex == 0);
        btnNextRound.setDisable(currentRoundIndex == allRounds.size() - 1);

        // Pega partidas apenas da rodada atual
        List<? extends Match<?>> currentMatches = allRounds.get(currentRoundIndex);

        for (Match<?> match : currentMatches) {
            matchesList.getChildren().add(createMatchCard(match));
        }
    }

    // --- CRIAÇÃO DOS CARDS (Igual ao anterior, apenas com Layout ajustado) ---
    private HBox createMatchCard(Match<?> match) {
        HBox card = new HBox();
        card.getStyleClass().add("match-card");
        card.setAlignment(Pos.CENTER_LEFT);
        card.setSpacing(20);

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

        String teamA = match.getCompetitorA().getName();
        String teamB = match.getCompetitorB().getName();

        scoreBoard.add(createTeamLabel(teamA), 0, 0);
        scoreBoard.add(createScoreLabel("-"), 1, 0); // Placeholder
        scoreBoard.add(createTeamLabel(teamB), 0, 1);
        scoreBoard.add(createScoreLabel("-"), 1, 1); // Placeholder

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label statusBadge = new Label(match.getStatus().toString());
        statusBadge.getStyleClass().add("status-badge");

        card.getChildren().addAll(scoreBoard, spacer, statusBadge);
        return card;
    }

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

    @FXML
    public void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/tcp/grupo01/home.fxml"));
            Stage stage = (Stage) lblTournamentName.getScene().getWindow();
            Scene scene = new Scene(loader.load(), 1000, 700);
            scene.getStylesheets().add(getClass().getResource("/org/tcp/grupo01/style.css").toExternalForm());
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}