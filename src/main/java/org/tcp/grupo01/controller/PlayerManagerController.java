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
import org.tcp.grupo01.services.pairing.*;
import org.tcp.grupo01.services.tournament.TournamentService;

import java.time.LocalDate;
import java.util.*;

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
    @FXML private Label lblCountPlayers;

    private String tournamentName;
    private Pairing<Person> pairingMethod;
    private TournamentService service;

    private final ObservableList<Person> players = FXCollections.observableArrayList();
    private final ObservableList<Person> existingPlayers = FXCollections.observableArrayList();

    // ----- MODO DE EDIÇÃO -----
    private boolean editMode = false;
    private Tournament<Person> editingTournament;

    // ==========================================================
    // SETUP
    // ==========================================================

    public void setupTournamentData(String name, Pairing<?> pairing, TournamentService service) {
        this.tournamentName = name;
        this.pairingMethod = (Pairing<Person>) pairing;
        this.service = service;

        loadExistingPlayers();
        updateCountLabel();
    }

    public void setupEditMode(Tournament<Person> t, TournamentService service) {
        this.editingTournament = t;
        this.editMode = true;
        this.service = service;

        this.tournamentName = t.getName();
        this.pairingMethod = (Pairing<Person>) t.getPairing();

        players.clear();
        for (var p : t.getParticipants()) players.add((Person) p);

        loadExistingPlayers();
        updateCountLabel();
    }

    @FXML
    public void initialize() {

        tablePlayers.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        colName.setStyle("-fx-alignment: CENTER;");
        colCpf.setStyle("-fx-alignment: CENTER;");
        colPhone.setStyle("-fx-alignment: CENTER;");
        colBirth.setStyle("-fx-alignment: CENTER;");

        colName.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        colCpf.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getCpf() != null ? data.getValue().getCpf() : ""));

        colPhone.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getPhone() != null ? data.getValue().getPhone() : ""));

        colBirth.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getBirthDate()));

        tablePlayers.setItems(players);
        cbExistingPlayers.setItems(existingPlayers);
        updateCountLabel();
    }

    private void loadExistingPlayers() {
        if (service == null) return;

        existingPlayers.clear();
        Set<Integer> seen = new HashSet<>();

        for (Tournament<?> t : service.getAll()) {

            if (t.getParticipants().isEmpty()) continue;

            if (t.getParticipants().get(0) instanceof Person) {
                for (Object obj : t.getParticipants()) {
                    Person p = (Person) obj;

                    if (seen.add(p.getId()))
                        existingPlayers.add(p);
                }
            }
        }
    }

    // ==========================================================
    // AÇÕES
    // ==========================================================

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
        updateCountLabel();

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
            updateCountLabel();
        }
    }

    @FXML
    private void handleRemovePlayer() {
        Person selected = tablePlayers.getSelectionModel().getSelectedItem();
        if (selected != null) {
            players.remove(selected);
            updateCountLabel();
        }
    }

    // ==========================================================
    // FINALIZAR (CRIA OU EDITA)
    // ==========================================================

    @FXML
    private void handleFinish() {

        int size = players.size();

        if (size == 0) {
            new Alert(Alert.AlertType.ERROR, "Adicione pelo menos 1 jogador.").showAndWait();
            return;
        }

        if (pairingMethod instanceof Swiss) {
            if (size != 16 && size != 32) {
                new Alert(Alert.AlertType.ERROR, "Torneio suíço exige 16 ou 32 jogadores.").showAndWait();
                return;
            }
        }

        if (pairingMethod instanceof League) {
            if (size < 2) {
                new Alert(Alert.AlertType.ERROR, "Pontos corridos exige pelo menos 2 jogadores.").showAndWait();
                return;
            }
        }

        if (pairingMethod instanceof Knockout) {
            if (!isPowerOfTwo(size)) {
                new Alert(Alert.AlertType.ERROR, "Mata-mata exige número potência de 2.").showAndWait();
                return;
            }
        }

        // ======== EDIT MODE ========
        if (editMode) {
            editingTournament.replaceParticipants(new ArrayList<>(players));
            ((Stage) tablePlayers.getScene().getWindow()).close();
            return;
        }

        // ======== CREATE MODE ========
        Tournament<Person> tournament =
                Tournament.createForPeople(tournamentName, pairingMethod, new ArrayList<>(players));

        service.add(tournament);
        ((Stage) tablePlayers.getScene().getWindow()).close();
    }

    @FXML
    private void handleCancel() {
        ((Stage) tablePlayers.getScene().getWindow()).close();
    }

    private boolean isPowerOfTwo(int n) {
        return n > 1 && (n & (n - 1)) == 0;
    }

    private void updateCountLabel() {
        int count = players.size();
        lblCountPlayers.setText(
                count == 1 ? "1 jogador adicionado" : count + " jogadores adicionados"
        );
    }

}
