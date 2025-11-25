package org.tcp.grupo01.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.ArrayList;

import org.tcp.grupo01.models.Match;
import org.tcp.grupo01.models.Tournament;
import org.tcp.grupo01.models.competitors.Person;
import org.tcp.grupo01.models.competitors.Team;
import org.tcp.grupo01.services.pairing.League;
import org.tcp.grupo01.services.pairing.Swiss;

public class NewTournamentController {

    @FXML private TextField txtName;
    @FXML private ComboBox<String> cbCompetitionType;
    @FXML private ComboBox<String> cbCompetitorType;
    @FXML private TextField txtLocation;
    @FXML private DatePicker dateStart;
    @FXML private ComboBox<String> cbFormat;

    private org.tcp.grupo01.services.tournament.TournamentService service;

    public void setService(org.tcp.grupo01.services.tournament.TournamentService service) {
        this.service = service;
    }

    private void updateFieldLock() {
        boolean enabled = cbCompetitionType.getValue() != null;
        cbFormat.setDisable(!enabled);
    }

    @FXML
    public void initialize() {
        cbCompetitionType.getItems().addAll("Pontos Corridos", "Mata-Mata", "Suíço");
        cbCompetitorType.getItems().addAll("Jogadores", "Times");
        cbFormat.setDisable(true);

        updateFormatOptions();

        cbCompetitionType.valueProperty().addListener((obs, o, n) -> {
            updateFieldLock();
            updateFormatOptions();
        });
    }

    @FXML
    public void handleClose() {
        ((Stage) txtName.getScene().getWindow()).close();
    }

    private boolean validateRequired(Control c, boolean valid) {
        if (!valid) {
            c.setStyle("-fx-border-color: #ff4444;");
            return false;
        }
        c.setStyle("");
        return true;
    }

    private boolean validateTextField(TextInputControl t) {
        return validateRequired(t, t.getText() != null && !t.getText().isBlank());
    }

    @FXML
    public void handleCreate() {
        boolean ok = true;

        ok &= validateTextField(txtName);
        ok &= validateRequired(cbCompetitionType, cbCompetitionType.getValue() != null);
        ok &= validateRequired(cbFormat, cbFormat.getValue() != null);
        ok &= validateRequired(cbCompetitorType, cbCompetitorType.getValue() != null);
        ok &= validateTextField(txtLocation);
        ok &= validateRequired(dateStart, dateStart.getValue() != null);

        if (!ok) return;

        Tournament<?> t = buildTournament(txtName.getText(), cbCompetitionType.getValue(), cbCompetitorType.getValue(), cbFormat.getValue());
        service.add(t);
        handleClose();
    }

    private Object buildPairing(String competition, String competitorType) {
        return switch (competition) {

            case "Pontos Corridos" -> 
                    competitorType.equals("Jogadores")
                        ? new League<Person>(false, Match::betweenPeople)
                        : new League<Team>(false, Match::betweenTeams);

            case "Suíço" -> 
                    competitorType.equals("Jogadores")
                        ? new Swiss<Person>(3, 3, Match::betweenPeople)
                        : new Swiss<Team>(3, 3, Match::betweenTeams);

            default -> throw new IllegalArgumentException("Tipo desconhecido: " + competition);
        };
    }

    public Tournament<?> buildTournament(String name, String competition, String competitorType, String format) {
        ArrayList<?> competitors = loadCompetitors(competitorType);

        Object pairing = buildPairing(competition, competitorType);

        if (competitorType.equals("Jogadores")) {
            ArrayList<Person> players = (ArrayList<Person>) competitors;

            if (pairing instanceof League<?> league)
                return Tournament.createForPeople(name, (League<Person>) league, players);

            return Tournament.createForPeople(name, (Swiss<Person>) pairing, players);
        }

        ArrayList<Team> teams = (ArrayList<Team>) competitors;

        if (pairing instanceof League<?> league)
            return Tournament.createForTeams(name, (League<Team>) league, teams);

        return Tournament.createForTeams(name, (Swiss<Team>) pairing, teams);
    }

    private ArrayList<?> loadCompetitors(String type) {
        if ("Jogadores".equals(type)) {
            ArrayList<Person> list = new ArrayList<>();
            list.add(new Person("Alice"));
            list.add(new Person("Bob"));
            list.add(new Person("Carol"));
            list.add(new Person("David"));
            return list;
        }

        if ("Times".equals(type)) {
            ArrayList<Team> list = new ArrayList<>();
            Team t1 = new Team("Team A");
            t1.addPlayer(new Person("Player 1"));
            list.add(t1);

            Team t2 = new Team("Team B");
            t2.addPlayer(new Person("Player 2"));
            list.add(t2);

            return list;
        }

        throw new IllegalArgumentException("Tipo de competidor inválido: " + type);
    }

    private void updateFormatOptions() {
        cbFormat.getItems().clear();
        cbFormat.setValue(null);

        String type = cbCompetitionType.getValue();
        if (type == null) return;

        switch (type) {
            case "Pontos Corridos" ->
                    cbFormat.getItems().addAll("Turno", "Turno e Returno");
            case "Suíço" ->
                    cbFormat.getItems().addAll("Confronto Direto", "Mata-Mata");
            case "Mata-Mata" ->
                    cbFormat.getItems().addAll("Eliminação Simples");
        }
    }
}
