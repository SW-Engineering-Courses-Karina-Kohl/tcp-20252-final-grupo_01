package org.tcp.grupo01.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.FlowPane;
import java.net.URL;
import java.util.ResourceBundle;

// O Controller deve implementar Initializable para carregar dados
public class HomeController implements Initializable {

    // 1. Injeta o FlowPane do FXML (fx:id="containerCards")
    @FXML
    private FlowPane containerCards;

    // 2. Método chamado automaticamente após o FXML ser carregado
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Futuramente: Aqui você chamará o Service para buscar os campeonatos
        // Exemplo: List<Campeonato> campeonatos = service.buscarTodos();
        System.out.println("Controller da tela de campeonatos inicializado!");

        // **Demonstração:** Você pode adicionar um elemento de teste aqui:
        // containerCards.getChildren().add(new javafx.scene.control.Label("Card Carregado!"));
    }

    // 3. Método vinculado ao botão "+ Novo Campeonato" (onAction="#handleNovoCampeonato")
    @FXML
    public void handleNovoCampeonato() {
        System.out.println("Abrindo modal para novo campeonato...");
        // Futuramente: Lógica para abrir a nova tela/modal
    }
}