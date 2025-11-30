package org.tcp.grupo01.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import org.tcp.grupo01.models.Match;
import org.tcp.grupo01.models.competitors.Person;
import org.tcp.grupo01.models.competitors.Team;
import org.tcp.grupo01.services.pairing.Knockout;
import org.tcp.grupo01.services.pairing.League;
import org.tcp.grupo01.services.pairing.Pairing;
import org.tcp.grupo01.services.pairing.Swiss;
import org.tcp.grupo01.services.tournament.TournamentService;

import java.io.IOException;

public class NewTournamentController {

    @FXML private TextField txtName;
    @FXML private ComboBox<String> cbCompetitionType;
    @FXML private ComboBox<String> cbCompetitorType;
    @FXML private TextField txtLocation;
    @FXML private DatePicker dateStart;
    @FXML private ComboBox<String> cbFormat;

    private TournamentService service;

    public void setService(TournamentService service) {
        this.service = service;
    }

    @FXML
    public void initialize() {
        cbCompetitionType.getItems().addAll("Pontos Corridos", "Mata-Mata", "Suíço");
        cbCompetitorType.getItems().addAll("Jogadores", "Times");

        cbFormat.setDisable(true);

        cbCompetitionType.valueProperty().addListener((obs, oldVal, newVal) -> {
            updateFormatOptions();
            cbFormat.setDisable(false);
        });
    }

    @FXML
    private void handleContinue() {

        if (txtName.getText().isBlank() ||
            cbCompetitionType.getValue() == null ||
            cbCompetitorType.getValue() == null ||
            cbFormat.getValue() == null) {

            new Alert(Alert.AlertType.ERROR, "Preencha todos os campos antes de continuar.").showAndWait();
            return;
        }

        try {
            FXMLLoader loader;

            boolean isTeam = cbCompetitorType.getValue().equals("Times");

            if (isTeam) {
                loader = new FXMLLoader(getClass().getResource("/org/tcp/grupo01/teamManager.fxml"));
            } else {
                loader = new FXMLLoader(getClass().getResource("/org/tcp/grupo01/playerManager.fxml"));
            }

            Parent root = loader.load();

            Pairing<?> pairing = buildPairing(cbCompetitionType.getValue(), cbCompetitorType.getValue());

            if (isTeam) {
                TeamManagerController controller = loader.getController();
                controller.setupTournamentData(txtName.getText(), pairing, service);
            } else {
                PlayerManagerController controller = loader.getController();
                controller.setupTournamentData(txtName.getText(), pairing, service);
            }

            Stage modal = new Stage();
            modal.setTitle("Gerenciar Participantes");
            modal.setScene(new Scene(root));
            modal.initModality(Modality.WINDOW_MODAL);
            modal.initOwner(txtName.getScene().getWindow());

            modal.showAndWait();

            Stage stage = (Stage) txtName.getScene().getWindow();
            stage.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Pairing<?> buildPairing(String competition, String competitorType) {

        return switch (competition) {
            case "Pontos Corridos" -> {
                String format = cbFormat.getValue();
                boolean doubleRound = ("Turno".equals(format) ? false : true);

                if (competitorType.equals("Jogadores")) {
                    yield new League<Person>(doubleRound, Match::betweenPeople);
                } else {
                    yield new League<Team>(doubleRound, Match::betweenTeams);
                }
            }

            case "Suíço" -> {
                String format = cbFormat.getValue();
                int max = ("16 Times".equals(format) ? 3 : 4);

                if (competitorType.equals("Jogadores")) {
                    yield new Swiss<Person>(max, max, Match::betweenPeople);
                } else {
                    yield new Swiss<Team>(max, max, Match::betweenTeams);
                }
            }

            case "Mata-Mata" ->
                competitorType.equals("Jogadores")
                        ? new Knockout<Person>(Match::betweenPeople)
                        : new Knockout<Team>(Match::betweenTeams);

                default -> throw new IllegalArgumentException("Tipo de competição inválido");
            };
    }

    private void updateFormatOptions() {
        cbFormat.getItems().clear();
        cbFormat.setValue(null);

        switch (cbCompetitionType.getValue()) {
            case "Pontos Corridos" ->
                    cbFormat.getItems().addAll("Turno", "Turno e Returno");

            case "Suíço" ->
                    cbFormat.getItems().addAll("16 Times", "32 Times");

            case "Mata-Mata" ->
                    cbFormat.getItems().add("Eliminação Simples");
        }
    }

    @FXML
    public void handleClose() {
        ((Stage) txtName.getScene().getWindow()).close();
    }

}
