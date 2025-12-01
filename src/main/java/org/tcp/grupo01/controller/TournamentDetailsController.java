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

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class TournamentDetailsController {

    @FXML private Label lblTournamentName, lblStatus, lblParticipantsCount, lblSectionTitle;
    @FXML private ScrollPane roundsContainer, standingsScroll;
    @FXML private VBox matchesList, standingsContainer;
    @FXML private HBox paginationControls;
    @FXML private Button btnTabRounds, btnTabStandings, btnPrevRound, btnNextRound;
    @FXML private Button btnGenerateRound, btnShowResults;
    @FXML private Button btnEditParticipants;

    @FXML private VBox modalOverlay;
    @FXML private Label lblModalTeamA, lblModalTeamB, lblModalScoreA, lblModalScoreB, lblModalError;
    @FXML private Button btnStatusConfirm, btnStatusRunning, btnStatusFinished;
    @FXML private HBox scoreBoxA, scoreBoxB;

    private Tournament<?> tournament;
    private int currentRoundIndex = 0;

    private Match<?> currentEditingMatch;
    private int tempScoreA;
    private int tempScoreB;
    private EventStatus tempStatus;

    public void setTournament(Tournament<?> tournament) {
        this.tournament = tournament;
        this.currentRoundIndex = Math.max(0, tournament.getRoundCount() - 1);

        updateSidebar();
        showRounds();
    }

    private void updateSidebar() {
        if (tournament == null) return;
        lblTournamentName.setText(tournament.getName());
        lblStatus.setText("Status: " + tournament.getStatus());
        lblParticipantsCount.setText(tournament.getParticipants().size() + " participantes");
    }

    private boolean hasNextRoundExisting() {
        return currentRoundIndex < tournament.getRoundCount() - 1;
    }

    private boolean isCurrentRoundFinished() {
        List<? extends List<? extends Match<?>>> rounds = tournament.getRounds();
        if (rounds.isEmpty() || currentRoundIndex >= rounds.size()) return false;

        return rounds.get(currentRoundIndex).stream()
                .allMatch(m -> m.getStatus() == EventStatus.FINISHED);
    }

    private void tryAdvanceRound() {
        if (hasNextRoundExisting()) {
            currentRoundIndex++;
            renderCurrentRound();
            return;
        }

        if (!isCurrentRoundFinished()) {
            renderCurrentRound();
            return;
        }

        try {
            int oldRoundCount = tournament.getRoundCount();

            tournament.generateNextMatches();

            int newRoundCount = tournament.getRoundCount();

            if (newRoundCount > oldRoundCount) {
                currentRoundIndex = newRoundCount - 1;
                renderCurrentRound();
            } else {
                finalizeTournament();
                renderCurrentRound();
            }

        } catch (Exception e) {
            lblModalError.setText("Erro fluxo: " + e.getMessage());
        }
    }

    private void finalizeTournament() {
        if (tournament.getStatus() != EventStatus.FINISHED) {
            tournament.setStatus(EventStatus.FINISHED);
            updateSidebar();
        }
    }

    private void updateButtons() {
        boolean hasRounds = !tournament.getRounds().isEmpty();
        boolean isFinished = tournament.getStatus() == EventStatus.FINISHED;
        boolean showingRoundsTab = roundsContainer.isVisible();

        btnEditParticipants.setVisible(!hasRounds);
        btnEditParticipants.setManaged(!hasRounds);

        btnGenerateRound.setVisible(!hasRounds);
        btnGenerateRound.setManaged(!hasRounds);

        btnShowResults.setVisible(isFinished);
        btnShowResults.setManaged(isFinished);

        if (paginationControls != null) {
            boolean showPagination = hasRounds && showingRoundsTab;
            paginationControls.setVisible(showPagination);
            paginationControls.setManaged(showPagination);
        }
    }

    @FXML
    public void showRounds() {
        updateTabStyles(true);
        renderCurrentRound();
    }

    @FXML
    public void showStandings() {
        updateTabStyles(false);
        lblSectionTitle.setText("Classificação / Resultados");
        renderStandings();
    }

    private void updateTabStyles(boolean isRoundsActive) {
        btnTabRounds.getStyleClass().remove("segment-button-active");
        btnTabStandings.getStyleClass().remove("segment-button-active");

        if (isRoundsActive) {
            btnTabRounds.getStyleClass().add("segment-button-active");
            roundsContainer.setVisible(true);
            standingsScroll.setVisible(false);
        } else {
            btnTabStandings.getStyleClass().add("segment-button-active");
            roundsContainer.setVisible(false);
            standingsScroll.setVisible(true);
        }

        updateButtons();
    }

    private void renderCurrentRound() {
        matchesList.getChildren().clear();
        List<? extends List<? extends Match<?>>> rounds = tournament.getRounds();

        if (rounds.isEmpty()) {
            lblSectionTitle.setText("Aguardando Início");
            matchesList.getChildren().add(new Label("Nenhuma rodada gerada."));
            updateButtons();
            return;
        }

        if (currentRoundIndex >= rounds.size()) currentRoundIndex = rounds.size() - 1;

        lblSectionTitle.setText("Rodada " + (currentRoundIndex + 1));

        btnPrevRound.setDisable(currentRoundIndex == 0);
        btnNextRound.setDisable(currentRoundIndex == rounds.size() - 1);

        rounds.get(currentRoundIndex).forEach(m ->
                matchesList.getChildren().add(new MatchCard(m, this::openModal))
        );

        updateButtons();
    }

    private void renderStandings() {
        standingsContainer.getChildren().setAll(
                StandingsViewFactory.createViewFor(tournament)
        );
    }

    @FXML
    public void handleNextRound() {
        if (hasNextRoundExisting()) {
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

    @FXML
    public void handleGenerateRound() {
        try {
            tournament.generateNextMatches();
            currentRoundIndex = 0;
            updateSidebar();
            showRounds();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Erro ao gerar rodada:\n" + e.getMessage()).showAndWait();
        }
    }

    @FXML
    public void handleShowResults() {
        showStandings();
    }

    @FXML
    public void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/tcp/grupo01/home.fxml"));
            Stage stage = (Stage) lblTournamentName.getScene().getWindow();
            Scene scene = new Scene(loader.load(), 1000, 700);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/org/tcp/grupo01/styles/style.css")).toExternalForm());
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleEditParticipants() {
        if (!tournament.getRounds().isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Edição bloqueada após início.").showAndWait();
            return;
        }
        try {
            boolean isTeam =
                    !tournament.getParticipants().isEmpty() &&
                            tournament.getParticipants().get(0) instanceof Team;

            String fxml = isTeam ?
                    "/org/tcp/grupo01/teamManager.fxml" :
                    "/org/tcp/grupo01/playerManager.fxml";

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();

            if (isTeam) {
                TeamManagerController controller = loader.getController();
                controller.setupEditMode(tournament);
            } else {
                PlayerManagerController controller = loader.getController();
                controller.setupEditMode((Tournament<Person>) tournament);
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
            new Alert(Alert.AlertType.ERROR, "Erro ao abrir editor: " + e.getMessage()).showAndWait();
        }
    }

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
            currentEditingMatch = null;

            tryAdvanceRound();

        } catch (Exception e) {
            lblModalError.setText("⚠ " + e.getMessage());
        }
    }

    @FXML
    public void closeModal() {
        modalOverlay.setVisible(false);
        currentEditingMatch = null;
    }

    private void updateModalDisplay() {
        lblModalScoreA.setText(String.valueOf(tempScoreA));
        lblModalScoreB.setText(String.valueOf(tempScoreB));

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
            btn.getStyleClass().add(status == EventStatus.FINISHED ? "status-finalized-selected" : "status-option-selected");
        }
    }

    @FXML private void incrementScoreA() { tempScoreA++; updateModalDisplay(); }
    @FXML private void decrementScoreA() { if (tempScoreA > 0) tempScoreA--; updateModalDisplay(); }
    @FXML private void incrementScoreB() { tempScoreB++; updateModalDisplay(); }
    @FXML private void decrementScoreB() { if (tempScoreB > 0) tempScoreB--; updateModalDisplay(); }

    @FXML private void setStatusPlanning() { tempStatus = EventStatus.PLANNING; updateModalDisplay(); }
    @FXML private void setStatusRunning() { tempStatus = EventStatus.RUNNING; updateModalDisplay(); }
    @FXML private void setStatusFinished() { tempStatus = EventStatus.FINISHED; updateModalDisplay(); }
}
