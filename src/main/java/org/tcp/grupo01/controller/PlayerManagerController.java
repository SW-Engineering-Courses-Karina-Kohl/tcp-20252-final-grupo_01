package org.tcp.grupo01.controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import org.tcp.grupo01.models.Tournament;
import org.tcp.grupo01.models.competitors.Person;
import org.tcp.grupo01.services.ServiceRegistry;
import org.tcp.grupo01.services.competitors.CompetitorService;
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

    private final CompetitorService<Person> competitorService = ServiceRegistry.persons();
    private final TournamentService tournamentService = ServiceRegistry.tournaments();

    private final ObservableList<Person> players = FXCollections.observableArrayList();
    private final ObservableList<Person> existingPlayers = FXCollections.observableArrayList();

    private boolean editMode = false;
    private Tournament<Person> editingTournament;

    // Ajustado: agora só recebe name + pairing (sem service)
    public void setupTournamentData(String name, Pairing<?> pairing) {
        this.tournamentName = name;
        this.pairingMethod = (Pairing<Person>) pairing;

        loadExistingPlayers();
        updateCountLabel();
    }

    public void setupEditMode(Tournament<Person> t) {
        this.editingTournament = t;
        this.editMode = true;

        this.tournamentName = t.getName();
        this.pairingMethod = t.getPairing();

        players.clear();
        players.addAll(t.getParticipants());

        loadExistingPlayers();
        updateCountLabel();
    }

    @FXML
    public void initialize() {
        tablePlayers.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        configureTextField(txtCpf);
        configureTextField(txtPhone);
        configureFields();

        tablePlayers.setItems(players);
        cbExistingPlayers.setItems(existingPlayers);
        updateCountLabel();
    }

    private void configureFields() {
        colName.setStyle("-fx-alignment: CENTER;");
        colCpf.setStyle("-fx-alignment: CENTER;");
        colPhone.setStyle("-fx-alignment: CENTER;");
        colBirth.setStyle("-fx-alignment: CENTER;");
        dateBirth.getEditor().setDisable(true);
        dateBirth.getEditor().setOpacity(1);

        colName.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getName()));
        colCpf.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getCpf() != null ? data.getValue().getCpf() : ""));
        colPhone.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getPhone() != null ? data.getValue().getPhone() : ""));
        colBirth.setCellValueFactory(data ->
                new SimpleObjectProperty<>(data.getValue().getBirthDate()));
    }

    private void configureTextField(TextField textField) {
        textField.setTextFormatter(new TextFormatter<>(change -> {
            if (change.getControlNewText().matches("[0-9]*"))
                return change;
            return null;
        }));

        textField.addEventFilter(KeyEvent.KEY_TYPED,
                event -> validateInput(event, textField.getId()));
    }

    private void validateInput(KeyEvent event, String field) {
        String input = event.getCharacter();
        if (!input.matches("[0-9]")) {
            event.consume();
            showInvalidInputAlert();
        }
    }

    private void showInvalidInputAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Entrada Inválida");
        alert.setHeaderText("Apenas números são permitidos.");
        alert.setContentText("Por favor, insira apenas números.");
        alert.showAndWait();
    }

    private void loadExistingPlayers() {
        existingPlayers.clear();
        existingPlayers.addAll(competitorService.getAll());
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

        competitorService.add(p);

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

        if (editMode) {
            editingTournament.replaceParticipants(new ArrayList<>(players));
            ((Stage) tablePlayers.getScene().getWindow()).close();
            return;
        }

        Tournament<Person> tournament =
                Tournament.createForPeople(tournamentName, pairingMethod, new ArrayList<>(players));

        tournamentService.add(tournament);

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
