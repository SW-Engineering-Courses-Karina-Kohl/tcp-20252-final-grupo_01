package org.tcp.grupo01.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import org.tcp.grupo01.models.Match;
import org.tcp.grupo01.models.Tournament;
import org.tcp.grupo01.models.competitors.Competitor;
import org.tcp.grupo01.models.competitors.Person;
import org.tcp.grupo01.services.pairing.League;
import org.tcp.grupo01.services.tournament.TournamentService;
import org.tcp.grupo01.services.tournament.TournamentServiceIM;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class HomeController implements Initializable {

    private final TournamentService service;

    public HomeController() {
        this.service = TournamentServiceIM.getInstance();
    }

    // Injects FXML FlowPane (fx:id="containerCards")
    @FXML
    private FlowPane containerCards;

    // JavaFX calls initialize to open the new window
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        List<Tournament<? extends Competitor>> tournaments = service.getAll();

        if (!tournaments.isEmpty()) {
            this.loadTournamentCards(tournaments);
        } else {
            containerCards.getChildren().add(new Label("Nenhum campeonato encontrado."));
        }
    }

    private void loadTournamentCards(List<Tournament<? extends Competitor>> tournaments) {
        for (Tournament<?> t : tournaments) {
            VBox card = this.createTournamentCard(t);
            containerCards.getChildren().add(card);
        }
    }

    private VBox createTournamentCard(Tournament<?> tournament) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(20));

        card.setMinWidth(280);
        card.setMaxWidth(350);
        card.setPrefWidth(300);

        card.setMinHeight(180);

        card.getStyleClass().add("tournament-card");

        Label nomeLabel = new Label(tournament.getName());
        nomeLabel.getStyleClass().add("card-title");

        Label statusLabel = new Label("Status: " + tournament.getStatus());
        statusLabel.getStyleClass().add("card-info");

        Label participantesLabel = new Label("Participantes: " + tournament.getParticipants().size());
        participantesLabel.getStyleClass().add("card-info");

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        card.getChildren().addAll(nomeLabel, statusLabel, participantesLabel, spacer);

        card.setOnMouseClicked(_ -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/tcp/grupo01/tournamentDetails.fxml"));
                Scene scene = new Scene(loader.load(), 1000, 700);

                TournamentDetailsController controller = loader.getController();
                controller.setTournament(tournament);

                scene.getStylesheets().add(
                        Objects.requireNonNull(getClass().getResource("/org/tcp/grupo01/style.css")).toExternalForm()
                );

                Stage stage = (Stage) containerCards.getScene().getWindow();
                stage.setScene(scene);

            } catch (java.io.IOException e) {
                e.printStackTrace();
            }
        });

        return card;
    }

    @FXML
    public void handleNewTournament() {
        try {FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/tcp/grupo01/new_tournament.fxml"));

            Parent root = loader.load();
            NewTournamentController controller = loader.getController();
            controller.setService(this.service);

            Stage stage = new Stage();
            stage.setTitle("Novo Campeonato");
            Scene scene = new Scene(root);

            scene.getStylesheets().add(
                    Objects.requireNonNull(
                            getClass()
                            .getResource("/org/tcp/grupo01/newTournament.css"))
                            .toExternalForm());

            stage.setScene(scene);
            stage.showAndWait();

            containerCards.getChildren().clear();
            loadTournamentCards(service.getAll());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleManageTeams() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/tcp/grupo01/teamManager.fxml"));

            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Gerenciar Times");

            Scene scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/org/tcp/grupo01/style.css")).toExternalForm());

            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}