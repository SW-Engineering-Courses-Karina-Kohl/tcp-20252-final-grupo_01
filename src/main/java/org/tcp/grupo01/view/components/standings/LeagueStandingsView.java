package org.tcp.grupo01.view.components.standings;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.tcp.grupo01.models.Tournament;
import org.tcp.grupo01.models.stats.CompetitorStats;
import org.tcp.grupo01.services.stats.LeagueStandingsCalculator;

import java.util.List;

public class LeagueStandingsView implements StandingsViewStrategy {

    @Override
    public Node render(Tournament<?> tournament) {
        LeagueStandingsCalculator calculator = new LeagueStandingsCalculator();
        List<CompetitorStats> stats = calculator.calculate(tournament);

        if (stats.isEmpty()) {
            return new Label("Ainda não há dados suficientes para classificação.");
        }

        VBox tableContainer = new VBox();
        tableContainer.getStyleClass().add("standings-table");

        // Adiciona o Header
        tableContainer.getChildren().add(createHeader());

        // Adiciona as Linhas
        int rank = 1;
        for (CompetitorStats stat : stats) {
            tableContainer.getChildren().add(new StandingRow(rank++, stat));
        }

        return tableContainer;
    }

    private HBox createHeader() {
        HBox header = new HBox();
        header.getStyleClass().add("standings-header");
        header.setAlignment(Pos.CENTER_LEFT);

        // 1. RANKING (#)
        // Usamos StackPane para garantir a mesma largura e comportamento da linha (StandingRow)
        Label lblHash = createHeaderLabel("#", 0);
        StackPane rankContainer = new StackPane(lblHash);
        rankContainer.setPrefWidth(40); // Largura fixa igual ao StandingRow
        rankContainer.setAlignment(Pos.CENTER);

        // 2. TIME
        // Usamos HBox Wrapper para garantir que o Priority.ALWAYS funcione igual à linha
        Label lblTime = createHeaderLabel("Time", 0);
        lblTime.setPadding(new javafx.geometry.Insets(0, 0, 0, 10)); // Padding igual à linha

        HBox nameContainer = new HBox(lblTime);
        nameContainer.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(nameContainer, Priority.ALWAYS); // Ocupa o espaço restante

        // 3. COLUNAS DE DADOS
        Label lblPts = createHeaderLabel("PTS", 50);
        Label lblJ = createHeaderLabel("J", 40);
        Label lblV = createHeaderLabel("V", 40);
        Label lblD = createHeaderLabel("D", 40);
        Label lblGP = createHeaderLabel("GP", 40);
        Label lblGC = createHeaderLabel("GC", 40);
        Label lblSG = createHeaderLabel("SG", 40);

        header.getChildren().addAll(
                rankContainer,
                nameContainer,
                lblPts, lblJ, lblV, lblD, lblGP, lblGC, lblSG
        );

        return header;
    }

    private Label createHeaderLabel(String text, double width) {
        Label l = new Label(text);
        l.getStyleClass().add("header-label");
        if (width > 0) {
            l.setPrefWidth(width);
            l.setAlignment(Pos.CENTER); // Garante que o texto fique centralizado na largura fixa
        }
        return l;
    }
}