package org.tcp.grupo01.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.tcp.grupo01.models.competitors.Person;
import org.tcp.grupo01.models.competitors.Team;

public class TeamManagerController {

    @FXML private TextField teamNameField;
    @FXML private TextField playerField;

    @FXML private TableView<Person> playersTable;
    @FXML private TableColumn<Person, String> playerNameColumn;

    private Team currentTeam;
    private final ObservableList<Person> players = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        currentTeam = new Team("");

        playerNameColumn.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getName())
        );

        playersTable.setItems(players);
    }

    @FXML
    public void handleAddPlayer() {
        String name = playerField.getText().trim();

        if (name.isEmpty()) {
            showAlert("Erro", "O nome do jogador não pode estar vazio.");
            return;
        }

        Person p = new Person(name);

        currentTeam.addPlayer(p);
        players.add(p);

        playerField.clear();
    }

    @FXML
    public void handleRemovePlayer() {
        Person selected = playersTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showAlert("Erro", "Selecione um jogador para remover.");
            return;
        }

        currentTeam.removePlayer(selected);
        players.remove(selected);
    }

    @FXML
    public void handleSaveTeam() {
        String teamName = teamNameField.getText().trim();

        if (teamName.isEmpty()) {
            showAlert("Erro", "O nome do time é obrigatório.");
            return;
        }

        currentTeam.setName(teamName);

        showAlert("Sucesso",
                "Time salvo!\n" +
                "Nome: " + currentTeam.getName() + "\n" +
                "Jogadores: " + currentTeam.getPlayers().size()
        );
    }

    private void showAlert(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}
