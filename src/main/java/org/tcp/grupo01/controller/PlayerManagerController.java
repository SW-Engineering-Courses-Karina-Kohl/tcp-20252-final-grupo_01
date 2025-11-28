package org.tcp.grupo01.controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.tcp.grupo01.models.Tournament;
import org.tcp.grupo01.models.competitors.Person;
import org.tcp.grupo01.services.pairing.Pairing;
import org.tcp.grupo01.services.tournament.TournamentService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PlayerManagerController {

    @FXML private TextField txtPlayerName;
    @FXML private TextField txtCpf;
    @FXML private TextField txtPhone;
    @FXML private DatePicker dateBirth;

    @FXML private TableView<Person> tablePlayers;
    @FXML private TableColumn<Person, String> colName;
    @FXML private TableColumn<Person, String> colCpf;
    @FXML private TableColumn<Person, String> colPhone;
    @FXML private TableColumn<Person, LocalDate> colBirth;

    @FXML private ComboBox<Person> cbExistingPlayers;

    private String tournamentName;
    private Pairing<Person> pairingMethod;
    private TournamentService service;

    private final ObservableList<Person> players = FXCollections.observableArrayList();
    private final ObservableList<Person> existingPlayers = FXCollections.observableArrayList();

    public void setupTournamentData(String name, Pairing<?> pairing, TournamentService service) {
        this.tournamentName = name;
        this.pairingMethod = (Pairing<Person>) pairing;
        this.service = service;

        loadExistingPlayers();
    }

    @FXML
    public void initialize() {
        tablePlayers.setColumnResizePolicy(TableView. CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        colName.setStyle("-fx-alignment: CENTER;");
        colCpf.setStyle("-fx-alignment: CENTER;");
        colPhone.setStyle("-fx-alignment: CENTER;");
        colBirth.setStyle("-fx-alignment: CENTER;");

        colName.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getName()));
        colCpf.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getCpf() != null ? data.getValue().getCpf() : ""));
        colPhone.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getPhone() != null ? data.getValue().getPhone() : ""));
        colBirth.setCellValueFactory(data ->
                new SimpleObjectProperty<>(data.getValue().getBirthDate()));

        tablePlayers.setItems(players);
        cbExistingPlayers.setItems(existingPlayers);
    }

    private void loadExistingPlayers() {
        if (service == null) return;

        Set<Integer> seenIds = new HashSet<>();
        List<Tournament<?>> tournaments = service.getAll();

        for (Tournament<?> t : tournaments) {
            if (t.getParticipants().isEmpty()) continue;

            if (t.getParticipants().get(0) instanceof Person) {
                for (Object obj : t.getParticipants()) {
                    Person p = (Person) obj;
                    // evita duplicados pelo id
                    if (seenIds.add(p.getId())) {
                        existingPlayers.add(p);
                    }
                }
            }
        }
    }

    @FXML
    private void handleAddNewPlayer() {
        if (txtPlayerName.getText().isBlank()) {
            new Alert(Alert.AlertType.ERROR, "Nome é obrigatório.").showAndWait();
            return;
        }

        Person p = new Person(txtPlayerName.getText());
        p.setCpf(txtCpf.getText());
        p.setPhone(txtPhone.getText());
        p.setBirthDate(dateBirth.getValue());

        players.add(p);

        txtPlayerName.clear();
        txtCpf.clear();
        txtPhone.clear();
        dateBirth.setValue(null);
    }

    @FXML
    private void handleAddExistingPlayer() {
        Person selected = cbExistingPlayers.getValue();
        if (selected == null) return;

        if (!players.contains(selected)) {
            players.add(selected);
        }
    }

    @FXML
    private void handleRemovePlayer() {
        Person selected = tablePlayers.getSelectionModel().getSelectedItem();
        if (selected != null) {
            players.remove(selected);
        }
    }

    @FXML
    private void handleFinish() {
        if (players.isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Adicione pelo menos um jogador.").showAndWait();
            return;
        }

        Tournament<Person> tournament = Tournament.createForPeople(tournamentName, pairingMethod, new ArrayList<>(players));

        service.add(tournament);

        Stage stage = (Stage) tablePlayers.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleCancel() {
        Stage stage = (Stage) tablePlayers.getScene().getWindow();
        stage.close();
    }

}
