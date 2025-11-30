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
import org.tcp.grupo01.models.competitors.Team;
import org.tcp.grupo01.services.tournament.TournamentService;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AddPlayersToTeamController {

    @FXML private Label lblTeamName;

    @FXML private TextField txtPlayerName;
    @FXML private TextField txtCpf;
    @FXML private TextField txtPhone;
    @FXML private DatePicker dateBirth;

    @FXML private TableView<Person> tableTeamPlayers;
    @FXML private TableColumn<Person, String> colName;
    @FXML private TableColumn<Person, String> colCpf;
    @FXML private TableColumn<Person, String> colPhone;
    @FXML private TableColumn<Person, LocalDate> colBirth;

    @FXML private ComboBox<Person> cbExistingPlayers;

    private Team team;
    private TournamentService service;

    private final ObservableList<Person> players = FXCollections.observableArrayList();
    private final ObservableList<Person> existingPlayers = FXCollections.observableArrayList();

    public void setTeamAndService(Team team, TournamentService service) {
        this.team = team;
        this.service = service;

        lblTeamName.setText(team.getName());
        players.setAll(team.getPlayers());

        loadExistingPlayers();
    }

    @FXML
    public void initialize() {
        tableTeamPlayers.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        configureTextField(txtCpf);
        configureTextField(txtPhone);
        configureFields();

        tableTeamPlayers.setItems(players);
        cbExistingPlayers.setItems(existingPlayers);
    }

    private void configureFields() {
        colName.setStyle("-fx-alignment: CENTER;");
        colCpf.setStyle("-fx-alignment: CENTER;");
        colPhone.setStyle("-fx-alignment: CENTER;");
        colBirth.setStyle("-fx-alignment: CENTER;");
        dateBirth.getEditor().setDisable(true);
        dateBirth.getEditor().setOpacity(1);

        colName.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        colCpf.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCpf() != null ? data.getValue().getCpf() : ""));
        colPhone.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPhone() != null ? data.getValue().getPhone() : ""));
        colBirth.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getBirthDate()));
    }

    private void configureTextField(TextField textField) {
        textField.setTextFormatter(new TextFormatter<>(change -> {
            if (change.getControlNewText().matches("[0-9]*")) {
                return change;
            }
            return null;
        }));

        textField.addEventFilter(KeyEvent.KEY_TYPED, event -> validateInput(event, textField.getId()));
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
        alert.setContentText("Por favor, insira apenas números no campo .");
        alert.showAndWait();
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
        team.addPlayer(p);

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
            team.addPlayer(selected);
        }
    }

    @FXML
    private void handleRemovePlayer() {
        Person selected = tableTeamPlayers.getSelectionModel().getSelectedItem();
        if (selected != null) {
            players.remove(selected);
            team.removePlayer(selected);
        }
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) tableTeamPlayers.getScene().getWindow();
        stage.close();
    }
}
