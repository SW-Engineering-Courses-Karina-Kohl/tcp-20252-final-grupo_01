package org.tcp.grupo01.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import org.tcp.grupo01.models.EventStatus;
import org.tcp.grupo01.models.Match;
import org.tcp.grupo01.models.Tournament;
import org.tcp.grupo01.models.competitors.Person;
import org.tcp.grupo01.models.competitors.Team;
import org.tcp.grupo01.view.components.MatchCard;
import org.tcp.grupo01.view.components.standings.StandingsViewFactory;
import org.tcp.grupo01.services.tournament.TournamentService;
import org.tcp.grupo01.services.pairing.Knockout;
import org.tcp.grupo01.services.pairing.Swiss;
import org.tcp.grupo01.services.pairing.League;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class TournamentDetailsController {

    // UI
    @FXML private Label lblTournamentName, lblStatus, lblParticipantsCount, lblSectionTitle;
    @FXML private ScrollPane roundsContainer, standingsScroll;
    @FXML private VBox matchesList, standingsContainer;
    @FXML private HBox paginationControls;
    @FXML private Button btnTabRounds, btnTabStandings, btnPrevRound, btnNextRound;
    @FXML private Button btnGenerateRound, btnShowResults;
    @FXML private Button btnEditParticipants;   // <<<<<< ADICIONADO

    // Modal
    @FXML private VBox modalOverlay;
    @FXML private Label lblModalTeamA, lblModalTeamB, lblModalScoreA, lblModalScoreB, lblModalError;
    @FXML private Button btnStatusConfirm, btnStatusRunning, btnStatusFinished;
    @FXML private HBox scoreBoxA, scoreBoxB;

    // State
    private Tournament<?> tournament;
    private TournamentService service;
    private int currentRoundIndex = 0;

    private Match<?> currentEditingMatch;
    private int tempScoreA;
    private int tempScoreB;
    private EventStatus tempStatus;

    // ============================================================
    // SETUP
    // ============================================================

    public void setService(TournamentService service) {
        this.service = service;
    }

    public void setTournament(Tournament<?> tournament) {
        this.tournament = tournament;
        this.currentRoundIndex = 0;

        updateSidebar();
        showRounds();
        updateButtons();
    }

    private void updateSidebar() {
        lblTournamentName.setText(tournament.getName());
        lblStatus.setText("Status: " + tournament.getStatus());
        lblParticipantsCount.setText(tournament.getParticipants().size() + " participantes");
    }

    // ============================================================
    // IDENTIFICAÇÃO
    // ============================================================

    private boolean isLeague() {
        return tournament.getPairing() instanceof League;
    }

    private boolean isSwiss() {
        return tournament.getPairing() instanceof Swiss;
    }

    private boolean isKnockout() {
        return tournament.getPairing() instanceof Knockout;
    }

    private int expectedSwissRounds() {
        int n = tournament.getParticipants().size();
        return (n == 16) ? 5 : (n == 32) ? 6 : 0;
    }

    private boolean allMatchesFinished() {
        return !tournament.getRounds().isEmpty() &&
                tournament.getRounds().stream()
                        .flatMap(List::stream)
                        .allMatch(m -> m.getStatus() == EventStatus.FINISHED);
    }

    // ============================================================
    // BOTÕES DO SIDEBAR
    // ============================================================

    private void updateButtons() {

        boolean hasRounds = !tournament.getRounds().isEmpty();

        // ----- DESABILITA EDIÇÃO DE PARTICIPANTES DEPOIS DA 1ª RODADA -----
        btnEditParticipants.setVisible(!hasRounds);
        btnEditParticipants.setManaged(!hasRounds);

        // ----- LIGA -----
        if (isLeague()) {
            boolean finished = allMatchesFinished();

            btnGenerateRound.setVisible(!hasRounds);
            btnGenerateRound.setManaged(!hasRounds);

            btnShowResults.setVisible(finished);
            btnShowResults.setManaged(finished);

            if (finished) finalizeTournament();
            return;
        }

        // ----- SUÍÇO -----
        if (isSwiss()) {
            int expected = expectedSwissRounds();
            int generated = tournament.getRounds().size();

            boolean allGenerated = generated == expected;
            boolean finished = allGenerated && allMatchesFinished();

            btnGenerateRound.setVisible(!allGenerated);
            btnGenerateRound.setManaged(!allGenerated);

            btnShowResults.setVisible(finished);
            btnShowResults.setManaged(finished);

            if (finished) finalizeTournament();
            return;
        }

        if (isKnockout())
        {
            boolean finished = allMatchesFinished() && tournament.getRounds().getLast().size() == 1;
    
            btnGenerateRound.setVisible(!finished);
            btnGenerateRound.setManaged(!finished);
    
            btnShowResults.setVisible(finished);
            btnShowResults.setManaged(finished);
    
            if (finished) finalizeTournament();
        }
    }

    private void finalizeTournament() {
        tournament.setStatus(EventStatus.FINISHED);
        lblStatus.setText("Status: FINISHED");
    }

    // ============================================================
    // ABAS
    // ============================================================

    @FXML
    public void showRounds() {
        activate(btnTabRounds, btnTabStandings);

        roundsContainer.setVisible(true);
        standingsScroll.setVisible(false);

        paginationControls.setVisible(true);
        paginationControls.setManaged(true);

        renderCurrentRound();
        updateButtons();
    }

    @FXML
    public void showStandings() {
        activate(btnTabStandings, btnTabRounds);

        roundsContainer.setVisible(false);
        standingsScroll.setVisible(true);

        paginationControls.setVisible(false);
        paginationControls.setManaged(false);

        lblSectionTitle.setText("Classificação");
        renderStandings();
        updateButtons();
    }

    private void activate(Button active, Button inactive) {
        active.getStyleClass().add("segment-button-active");
        inactive.getStyleClass().remove("segment-button-active");
    }

    // ============================================================
    // RODADAS
    // ============================================================

    private void renderCurrentRound() {

        matchesList.getChildren().clear();

        List<? extends List<? extends Match<?>>> rounds = tournament.getRounds();

        if (rounds.isEmpty()) {
            lblSectionTitle.setText("Rodadas");
            matchesList.getChildren().add(new Label("Nenhuma rodada gerada."));
            paginationControls.setVisible(false);
            return;
        }

        lblSectionTitle.setText("Rodada " + (currentRoundIndex + 1));

        btnPrevRound.setDisable(currentRoundIndex == 0);
        btnNextRound.setDisable(currentRoundIndex == rounds.size() - 1);

        rounds.get(currentRoundIndex).forEach(
                m -> matchesList.getChildren().add(new MatchCard(m, this::openModal))
        );

        updateButtons();
    }

    @FXML
    public void handleNextRound() {
        if (currentRoundIndex < tournament.getRounds().size() - 1) {
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

    // ============================================================
    // CLASSIFICAÇÃO
    // ============================================================

    private void renderStandings() {
        standingsContainer.getChildren().setAll(
                StandingsViewFactory.createViewFor(tournament)
        );
    }

    // ============================================================
    // MODAL
    // ============================================================

    private void openModal(Match<?> match) {
        if (match.getStatus() == EventStatus.FINISHED) return;

        currentEditingMatch = match;
        tempScoreA = match.getScoreA();
        tempScoreB = match.getScoreB();
        tempStatus = match.getStatus();

        lblModalTeamA.setText(match.getCompetitorA().getName());
        lblModalTeamB.setText(match.getCompetitorB().getName());
        lblModalError.setText("");

        updateModalDisplay();
        modalOverlay.setVisible(true);
    }

    private void updateModalDisplay() {
        lblModalScoreA.setText("" + tempScoreA);
        lblModalScoreB.setText("" + tempScoreB);

        boolean locked = tempStatus == EventStatus.PLANNING;
        scoreBoxA.setDisable(locked);
        scoreBoxB.setDisable(locked);

        updateStatusStyle(btnStatusConfirm, EventStatus.PLANNING);
        updateStatusStyle(btnStatusRunning, EventStatus.RUNNING);
        updateStatusStyle(btnStatusFinished, EventStatus.FINISHED);
    }

    private void updateStatusStyle(Button btn, EventStatus status) {
        btn.getStyleClass().removeAll("status-option-selected", "status-finalized-selected");

        if (tempStatus == status) {
            btn.getStyleClass().add(
                    status == EventStatus.FINISHED ?
                            "status-finalized-selected" :
                            "status-option-selected"
            );
        }
    }

    @FXML private void incrementScoreA() { tempScoreA++; updateModalDisplay(); }
    @FXML private void decrementScoreA() { if (tempScoreA > 0) tempScoreA--; updateModalDisplay(); }
    @FXML private void incrementScoreB() { tempScoreB++; updateModalDisplay(); }
    @FXML private void decrementScoreB() { if (tempScoreB > 0) tempScoreB--; updateModalDisplay(); }

    @FXML private void setStatusPlanning() { tempStatus = EventStatus.PLANNING; updateModalDisplay(); }
    @FXML private void setStatusRunning() { tempStatus = EventStatus.RUNNING; updateModalDisplay(); }
    @FXML private void setStatusFinished() { tempStatus = EventStatus.FINISHED; updateModalDisplay(); }

    @FXML
    public void saveMatch() {
        if (currentEditingMatch == null) return;

        if (tempStatus == EventStatus.FINISHED && tempScoreA == tempScoreB) {
            lblModalError.setText("⚠ Necessário ter um vencedor!");
            return;
        }

        try {
            currentEditingMatch.updateResult(tempScoreA, tempScoreB, tempStatus);

            modalOverlay.setVisible(false);
            renderCurrentRound();
            updateButtons();

        } catch (Exception e) {
            lblModalError.setText("⚠ " + e.getMessage());
        }
    }

    @FXML
    public void closeModal() {
        modalOverlay.setVisible(false);
        currentEditingMatch = null;
    }

    // ============================================================
    // OUTROS BOTÕES
    // ============================================================

    @FXML
    public void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/tcp/grupo01/home.fxml"));
            Stage stage = (Stage) lblTournamentName.getScene().getWindow();

            Scene scene = new Scene(loader.load(), 1000, 700);
            scene.getStylesheets().add(
                    Objects.requireNonNull(getClass().getResource("/org/tcp/grupo01/styles/style.css")).toExternalForm()
            );

            stage.setScene(scene);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleGenerateRound() {
        try {
            tournament.generateNextMatches();
            renderCurrentRound();
            updateButtons();

            paginationControls.setVisible(true);
            paginationControls.setManaged(true);

        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR,
                    "Erro ao gerar rodada:\n" + e.getMessage()
            ).showAndWait();
        }
    }

    @FXML
    public void handleShowResults() {
        showStandings();
        lblSectionTitle.setText("Resultados Finais");
    }

    // ============================================================
    // EDITAR PARTICIPANTES (COM BLOQUEIO)
    // ============================================================

    @FXML
    public void handleEditParticipants() {

        // -------- BLOQUEIO REAL --------
        if (!tournament.getRounds().isEmpty()) {
            new Alert(Alert.AlertType.WARNING,
                    "Você não pode mais editar participantes após gerar a primeira rodada.")
                    .showAndWait();
            return;
        }

        try {
            boolean isTeam = tournament.getParticipants().get(0) instanceof Team;

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(isTeam ?
                            "/org/tcp/grupo01/teamManager.fxml" :
                            "/org/tcp/grupo01/playerManager.fxml")
            );

            Parent root = loader.load();

            if (isTeam) {
                loader.<TeamManagerController>getController()
                        .setupEditMode((Tournament<Team>) tournament, service);
            } else {
                loader.<PlayerManagerController>getController()
                        .setupEditMode((Tournament<Person>) tournament, service);
            }

            Stage modal = new Stage();
            modal.setTitle("Editar Participantes");
            modal.setScene(new Scene(root));
            modal.initOwner(lblTournamentName.getScene().getWindow());
            modal.initModality(Modality.WINDOW_MODAL);
            modal.showAndWait();

            updateSidebar();

        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR,
                    "Erro ao abrir editor:\n" + e.getMessage()
            ).showAndWait();
        }
    }
}
