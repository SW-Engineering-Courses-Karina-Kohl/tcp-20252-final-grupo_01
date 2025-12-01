package org.tcp.grupo01.controller;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.tcp.grupo01.models.Tournament;
import org.tcp.grupo01.models.competitors.Person;
import org.tcp.grupo01.models.competitors.Team;
import org.tcp.grupo01.services.ServiceRegistry;
import org.tcp.grupo01.services.competitors.CompetitorService;
import org.tcp.grupo01.services.pairing.*;
import org.tcp.grupo01.services.tournament.TournamentService;

import java.io.IOException;
import java.util.*;

public class TeamManagerController {

    @FXML private TextField txtTeamName;
    @FXML private TableView<Team> tableTeams;
    @FXML private TableColumn<Team, String> colTeamName;
    @FXML private TableColumn<Team, Number> colPlayersCount;
    @FXML private TableColumn<Team, String> colTeamPlayers;
    @FXML private ComboBox<Team> cbExistingTeams;
    @FXML private Label lblCountTeams;

    private String tournamentName;
    private Pairing<Team> pairingMethod;

    private TournamentService service = ServiceRegistry.tournaments();
    private final CompetitorService<Team> teamService = ServiceRegistry.teams();

    private final ObservableList<Team> teams = FXCollections.observableArrayList();
    private final ObservableList<Team> existingTeams = FXCollections.observableArrayList();

    private Tournament<Team> editingTournament;

    public void setupTournamentData(String name, Pairing<?> pairing) {
        this.tournamentName = name;
        this.pairingMethod = (Pairing<Team>) pairing;

        loadExistingTeams();
        updateCountLabel();
    }

    @FXML
    public void initialize() {
        tableTeams.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        colTeamName.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getName()));

        colPlayersCount.setCellValueFactory(data ->
                new SimpleIntegerProperty(data.getValue().getPlayers().size()));

        colTeamPlayers.setCellValueFactory(data ->
                new SimpleStringProperty(
                        String.join(", ",
                                data.getValue().getPlayers().stream()
                                        .map(Person::getName)
                                        .toList()
                        )
                ));

        tableTeams.setItems(teams);
        cbExistingTeams.setItems(existingTeams);

        updateCountLabel();
    }

    private void loadExistingTeams() {
        existingTeams.clear();
        existingTeams.addAll(teamService.getAll());
    }

    @FXML
    private void handleAddNewTeam() {
        String name = txtTeamName.getText();
        if (name.isBlank()) {
            new Alert(Alert.AlertType.ERROR, "Nome do time é obrigatório.").showAndWait();
            return;
        }

        Team t = new Team(name);
        teamService.add(t);
        teams.add(t);

        txtTeamName.clear();
        updateCountLabel();
    }

    @FXML
    private void handleAddExistingTeam() {
        Team selected = cbExistingTeams.getValue();
        if (selected == null) return;

        if (!teams.contains(selected)) {
            teams.add(selected);
            updateCountLabel();
        }
    }

    @FXML
    private void handleRemoveTeam() {
        Team selected = tableTeams.getSelectionModel().getSelectedItem();
        if (selected != null) {
            teams.remove(selected);
            updateCountLabel();
        }
    }

    @FXML
    private void handleManagePlayers() {
        Team selected = tableTeams.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "Selecione um time para gerenciar jogadores.").showAndWait();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/tcp/grupo01/addPlayersToTeam.fxml")
            );
            Parent root = loader.load();

            AddPlayersToTeamController controller = loader.getController();
            controller.setTeamAndService(selected);

            Stage modal = new Stage();
            modal.initModality(Modality.APPLICATION_MODAL);
            modal.setTitle("Jogadores do time: " + selected.getName());
            modal.setScene(new Scene(root));
            modal.showAndWait();

            tableTeams.refresh();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleFinish() {

        int size = teams.size();

        if (size == 0) {
            new Alert(Alert.AlertType.ERROR, "Adicione pelo menos 1 time.").showAndWait();
            return;
        }

        for (Team t : teams) {
            if (t.getPlayers().isEmpty()) {
                new Alert(Alert.AlertType.ERROR,
                        "O time '" + t.getName() + "' precisa ter pelo menos 1 jogador.")
                        .showAndWait();
                return;
            }
        }

        if (pairingMethod instanceof Swiss) {
            if (size != 16 && size != 32) {
                new Alert(Alert.AlertType.ERROR,
                        "Torneio suíço exige 16 ou 32 times.")
                        .showAndWait();
                return;
            }
        }

        if (pairingMethod instanceof League) {
            if (size < 2) {
                new Alert(Alert.AlertType.ERROR,
                        "Pontos corridos exige pelo menos 2 times.")
                        .showAndWait();
                return;
            }
        }

        if (pairingMethod instanceof Knockout) {
            if (!isPowerOfTwo(size)) {
                new Alert(Alert.AlertType.ERROR,
                        "Mata-mata exige número potência de 2.")
                        .showAndWait();
                return;
            }
        }

        if (editingTournament != null) {
            editingTournament.replaceParticipants(teams);
            Stage stage = (Stage) tableTeams.getScene().getWindow();
            stage.close();
            return;
        }

        Tournament<Team> tournament =
                Tournament.createForTeams(tournamentName, pairingMethod, new ArrayList<>(teams));

        service.add(tournament);

        ((Stage) tableTeams.getScene().getWindow()).close();
    }

    private boolean isPowerOfTwo(int n) {
        return n > 1 && (n & (n - 1)) == 0;
    }

    @FXML
    private void handleCancel() {
        Stage stage = (Stage) tableTeams.getScene().getWindow();
        stage.close();
    }

    public void setupEditMode(Tournament<?> tournament) {
        this.editingTournament = (Tournament<Team>) tournament;
        this.pairingMethod = (Pairing<Team>) tournament.getPairing();
        this.tournamentName = tournament.getName();

        teams.clear();
        for (var tm : tournament.getParticipants()) teams.add((Team) tm);

        updateCountLabel();
    }

    private void updateCountLabel() {
        int count = teams.size();
        lblCountTeams.setText(count == 1 ?
                "1 time adicionado" :
                count + " times adicionados");
    }
}
