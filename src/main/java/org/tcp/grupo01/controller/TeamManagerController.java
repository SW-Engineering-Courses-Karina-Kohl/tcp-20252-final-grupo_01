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
import org.tcp.grupo01.services.pairing.Pairing;
import org.tcp.grupo01.services.tournament.TournamentService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TeamManagerController {

    @FXML private TextField txtTeamName;
    @FXML private TableView<Team> tableTeams;
    @FXML private TableColumn<Team, String> colTeamName;
    @FXML private TableColumn<Team, Number> colPlayersCount;
    @FXML private TableColumn<Team, String> colTeamPlayers;
    @FXML private ComboBox<Team> cbExistingTeams;

    private String tournamentName;
    private Pairing<Team> pairingMethod;
    private TournamentService service;

    private final ObservableList<Team> teams = FXCollections.observableArrayList();
    private final ObservableList<Team> existingTeams = FXCollections.observableArrayList();

    public void setupTournamentData(String name, Pairing<?> pairing, TournamentService service) {
        this.tournamentName = name;
        this.pairingMethod = (Pairing<Team>) pairing;
        this.service = service;

        loadExistingTeams();
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
                data.getValue()
                    .getPlayers()
                    .stream()
                    .map(Person::getName)
                    .reduce("", (a, b) -> a.isEmpty() ? b : a + ", " + b)
        ));

        tableTeams.setItems(teams);
        cbExistingTeams.setItems(existingTeams);
    }

    private void loadExistingTeams() {
        if (service == null) return;

        Set<Integer> seenIds = new HashSet<>();
        List<Tournament<?>> tournaments = service.getAll();

        for (Tournament<?> t : tournaments) {
            if (t.getParticipants().isEmpty()) continue;

            if (t.getParticipants().get(0) instanceof Team) {
                for (Object obj : t.getParticipants()) {
                    Team tm = (Team) obj;
                    if (seenIds.add(tm.getId())) {
                        existingTeams.add(tm);
                    }
                }
            }
        }
    }

    @FXML
    private void handleAddNewTeam() {
        String name = txtTeamName.getText();
        if (name.isBlank()) {
            new Alert(Alert.AlertType.ERROR, "Nome do time é obrigatório.").showAndWait();
            return;
        }

        Team t = new Team(name);
        teams.add(t);
        txtTeamName.clear();
    }

    @FXML
    private void handleAddExistingTeam() {
        Team selected = cbExistingTeams.getValue();
        if (selected == null) return;

        if (!teams.contains(selected)) {
            teams.add(selected);
        }
    }

    @FXML
    private void handleRemoveTeam() {
        Team selected = tableTeams.getSelectionModel().getSelectedItem();
        if (selected != null) {
            teams.remove(selected);
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
            controller.setTeamAndService(selected, service);

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
        if (teams.isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Adicione pelo menos um time.").showAndWait();
            return;
        }

        for (Team t : teams) {
            if (t.getPlayers().isEmpty()) {
                new Alert(Alert.AlertType.ERROR,
                        "O time '" + t.getName() + "' precisa ter pelo menos um jogador.")
                        .showAndWait();
                return;
            }
        }

        Tournament<Team> tournament =
                Tournament.createForTeams(tournamentName, pairingMethod, new ArrayList<>(teams));

        service.add(tournament);

        Stage stage = (Stage) tableTeams.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleCancel() {
        Stage stage = (Stage) tableTeams.getScene().getWindow();
        stage.close();
    }
}
