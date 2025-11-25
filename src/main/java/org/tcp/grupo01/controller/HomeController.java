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
import java.util.ResourceBundle;

public class HomeController implements Initializable {

    private final TournamentService service;

    public HomeController() {
        this.service = new TournamentServiceIM();

        ArrayList<Person> players = new ArrayList<>();
        players.add(new Person("Alice"));
        players.add(new Person("Bob"));
        players.add(new Person("Carol"));
        players.add(new Person("David"));

        League<Person> league = new League<>(true, Match::betweenPeople);
        service.add(Tournament.createForPeople("Torneio 1", league, players));
        service.add(Tournament.createForPeople("Torneio 2", league, players));
        service.add(Tournament.createForPeople("Torneio 3", league, players));
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

        card.setOnMouseClicked(event -> {
            System.out.println("==== TORNEIO ====");
            System.out.println("Nome: " + tournament.getName());
            System.out.println("Pareamento: " + tournament.getPairing().getClass().getSimpleName());
            System.out.println("Participantes:");

            tournament.getParticipants().forEach(p ->
                System.out.println(" - " + p.getName())
            );

            System.out.println("=================");
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

            scene.getStylesheets().add(getClass().getResource("/org/tcp/grupo01/newTournament.css").toExternalForm());

            stage.setScene(scene);
            stage.showAndWait();

            containerCards.getChildren().clear();
            loadTournamentCards(service.getAll());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}