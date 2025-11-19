package org.tcp.grupo01.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.FlowPane;
import org.tcp.grupo01.models.Tournament;
import org.tcp.grupo01.services.tournament.TournamentService;
import org.tcp.grupo01.services.tournament.TournamentServiceIM; // Importe a implementação

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class HomeController implements Initializable {

    private final TournamentService service;

    public HomeController() {
        this.service = new TournamentServiceIM();
    }


    // Injeta o FlowPane do FXML (fx:id="containerCards")
    @FXML
    private FlowPane containerCards;

    // Método chamado automaticamente após o FXML ser carregado
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // Chama o Service para buscar os dados
        List<Tournament<?>> torneios = service.getAll();

        if (torneios.isEmpty()) {
            System.out.println("Nenhum torneio encontrado.");
        } else {
            System.out.println("Carregando " + torneios.size() + " torneios.");

            // Futuramente: Itere sobre 'torneios' para criar e adicionar os Cards.
            for (Tournament<?> t : torneios) {
                // Aqui você criará o componente Card
                // Exemplo: containerCards.getChildren().add(new TournamentCard(t));
                containerCards.getChildren().add(new javafx.scene.control.Label(t.getName() + " - " + t.getStatus()));
            }
        }
    }

    @FXML
    public void handleNovoCampeonato() {
        // ... (pode usar o 'service' aqui também)
        System.out.println("Abrindo modal para novo campeonato...");
    }
}