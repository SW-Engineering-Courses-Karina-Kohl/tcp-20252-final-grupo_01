package org.tcp.grupo01.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.tcp.grupo01.models.EventStatus;
import org.tcp.grupo01.models.Match;
import org.tcp.grupo01.models.Tournament;
import org.tcp.grupo01.view.components.standings.StandingsViewFactory;
import org.tcp.grupo01.view.components.MatchCard;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class TournamentDetailsController {

    // --- ELEMENTOS PRINCIPAIS ---
    @FXML private Label lblTournamentName, lblStatus, lblParticipantsCount, lblSectionTitle;
    @FXML private ScrollPane roundsContainer;
    @FXML private VBox matchesList, standingsContainer;
    @FXML private HBox paginationControls;
    @FXML private Button btnTabRounds, btnTabStandings, btnPrevRound, btnNextRound;
    @FXML private ScrollPane standingsScroll; // Adicione isso

    // --- ELEMENTOS DO MODAL ---
    @FXML private VBox modalOverlay;
    @FXML private Label lblModalTeamA, lblModalTeamB, lblModalScoreA, lblModalScoreB, lblModalError;
    @FXML private Button btnGenerateRound, btnStatusConfirm, btnStatusRunning, btnStatusFinished;
    @FXML private HBox scoreBoxA, scoreBoxB;

    // --- ESTADO ---
    private Tournament<?> tournament;
    private int currentRoundIndex = 0;

    private Match<?> currentEditingMatch;
    private int tempScoreA;
    private int tempScoreB;
    private EventStatus tempStatus;

    // ================== INICIALIZAÇÃO ==================

    public void setTournament(Tournament<?> tournament) {
        this.tournament = tournament;
        updateSidebarInfo();
        this.currentRoundIndex = 0;
        showRounds(); // Default view
    }

    private void updateSidebarInfo() {
        if (tournament == null) return;
        lblTournamentName.setText(tournament.getName());
        lblStatus.setText("Status: " + tournament.getStatus());
        lblParticipantsCount.setText(tournament.getParticipants().size() + " participantes");
        
    }

    // ================== NAVEGAÇÃO (ABAS) ==================

    @FXML public void showRounds() {
        setTabActive(btnTabRounds, btnTabStandings);

        roundsContainer.setVisible(true);
        standingsScroll.setVisible(false);

        paginationControls.setVisible(true);
        renderCurrentRound();    }

    @FXML public void showStandings() {
        setTabActive(btnTabStandings, btnTabRounds);

        roundsContainer.setVisible(false);
        standingsScroll.setVisible(true);

        paginationControls.setVisible(false);
        lblSectionTitle.setText("Classificação");
        renderStandings();    }

    private void setTabActive(Button active, Button inactive) {
        active.getStyleClass().add("segment-button-active");
        inactive.getStyleClass().remove("segment-button-active");
    }

    // ================== RENDERIZAÇÃO DAS RODADAS ==================

    private void renderCurrentRound() {
        matchesList.getChildren().clear();
        List<? extends List<? extends Match<?>>> allRounds = tournament.getRounds();

        if (allRounds.isEmpty()) {
            lblSectionTitle.setText("Rodadas");
            matchesList.getChildren().add(new Label("Nenhuma rodada gerada."));
            paginationControls.setVisible(false);
            return;
        }

        lblSectionTitle.setText("Rodada " + (currentRoundIndex + 1));
        btnPrevRound.setDisable(currentRoundIndex == 0);
        btnNextRound.setDisable(currentRoundIndex == allRounds.size() - 1);

        List<? extends Match<?>> currentMatches = allRounds.get(currentRoundIndex);
        for (Match<?> match : currentMatches) {
            matchesList.getChildren().add(new MatchCard(match, this::openModal));
        }
    }

    // ================== RENDERIZAÇÃO DA CLASSIFICAÇÃO [NOVO] ==================

    private void renderStandings() {
        standingsContainer.getChildren().clear();

        // Usa a Factory para decidir qual visualização mostrar (League, Bracket, etc.)
        // Isso mantém o princípio Open/Closed
        javafx.scene.Node view = StandingsViewFactory.createViewFor(tournament);

        standingsContainer.getChildren().add(view);
    }

    // ================== LÓGICA DO MODAL ==================

    private void openModal(Match<?> match) {
        // Se já estiver finalizada, proíbe a edição
        if (match.getStatus() == EventStatus.FINISHED) {
            return;
        }

        this.currentEditingMatch = match;

        this.tempScoreA = match.getScoreA();
        this.tempScoreB = match.getScoreB();
        this.tempStatus = match.getStatus();

        lblModalTeamA.setText(match.getCompetitorA().getName());
        lblModalTeamB.setText(match.getCompetitorB().getName());
        lblModalError.setText("");

        updateModalDisplay();
        modalOverlay.setVisible(true);
    }

    @FXML
    public void closeModal() {
        modalOverlay.setVisible(false);
        currentEditingMatch = null;
    }

    private void updateModalDisplay() {
        lblModalScoreA.setText(String.valueOf(tempScoreA));
        lblModalScoreB.setText(String.valueOf(tempScoreB));

        boolean isLocked = (tempStatus == EventStatus.PLANNING);
        scoreBoxA.setDisable(isLocked);
        scoreBoxB.setDisable(isLocked);

        updateStatusButtonStyle(btnStatusConfirm, EventStatus.PLANNING);
        updateStatusButtonStyle(btnStatusRunning, EventStatus.RUNNING);
        updateStatusButtonStyle(btnStatusFinished, EventStatus.FINISHED);
    }

    private void updateStatusButtonStyle(Button btn, EventStatus targetStatus) {
        btn.getStyleClass().removeAll("status-option-selected", "status-finalized-selected");
        if (this.tempStatus == targetStatus) {
            if (targetStatus == EventStatus.FINISHED) {
                btn.getStyleClass().add("status-finalized-selected");
            } else {
                btn.getStyleClass().add("status-option-selected");
            }
        }
    }

    // --- CONTROLES MODAL ---
    @FXML public void incrementScoreA() { tempScoreA++; updateModalDisplay(); }
    @FXML public void decrementScoreA() { if(tempScoreA > 0) tempScoreA--; updateModalDisplay(); }
    @FXML public void incrementScoreB() { tempScoreB++; updateModalDisplay(); }
    @FXML public void decrementScoreB() { if(tempScoreB > 0) tempScoreB--; updateModalDisplay(); }

    @FXML public void setStatusPlanning() { tempStatus = EventStatus.PLANNING; updateModalDisplay(); }
    @FXML public void setStatusRunning() { tempStatus = EventStatus.RUNNING; updateModalDisplay(); }
    @FXML public void setStatusFinished() { tempStatus = EventStatus.FINISHED; updateModalDisplay(); }

    @FXML
    public void saveMatch() {
        if (currentEditingMatch == null) return;

        // Só proíbe empate se tentar FINALIZAR a partida
        if (tempStatus == EventStatus.FINISHED && tempScoreA == tempScoreB) {
            lblModalError.setText("⚠️ Para finalizar, é necessário haver um vencedor!");
            return;
        }

        // Tenta aplicar as mudanças no modelo
        try {
            currentEditingMatch.updateResult(tempScoreA, tempScoreB, tempStatus);

            closeModal();
            renderCurrentRound();

        } catch (Exception e) {
            lblModalError.setText("⚠️ " + e.getMessage());
        }
    }

    // ================== OUTROS ==================
    @FXML public void handleNextRound() { if (currentRoundIndex < tournament.getRounds().size() - 1) { currentRoundIndex++; renderCurrentRound(); } }
    @FXML public void handlePrevRound() { if (currentRoundIndex > 0) { currentRoundIndex--; renderCurrentRound(); } }

    @FXML
    public void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/tcp/grupo01/home.fxml"));
            Stage stage = (Stage) lblTournamentName.getScene().getWindow();
            Scene scene = new Scene(loader.load(), 1000, 700);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/org/tcp/grupo01/style.css")).toExternalForm());
            stage.setScene(scene);
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML
    public void handleGenerateRound() {
        try {
            tournament.generateNextMatches();
            renderCurrentRound();

        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, 
                "Erro ao gerar rodada: " + e.getMessage()
            ).showAndWait();
        }
    }

}