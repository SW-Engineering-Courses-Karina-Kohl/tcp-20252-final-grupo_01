package org.tcp.grupo01.view.components.standings;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.tcp.grupo01.models.Match;
import org.tcp.grupo01.models.Tournament;
import org.tcp.grupo01.models.competitors.Competitor;

import java.util.*;
import java.util.stream.Collectors;

public class SwissBucketsView implements StandingsViewStrategy {

    @Override
    public Node render(Tournament<?> tournament) {

        VBox root = new VBox(25);
        root.setPadding(new Insets(15));
        root.setAlignment(Pos.TOP_CENTER);

        root.getStylesheets().add(
                getClass().getResource("/org/tcp/grupo01/styles/swiss.css").toExternalForm()
        );

        List<List<Match<?>>> rounds = (List) tournament.getRounds();

        List<Match<?>> allMatches = rounds.stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());

        List<Competitor> competitors = (List) tournament.getParticipants();

        Map<Competitor, int[]> recordMap = new HashMap<>();
        for (Competitor c : competitors)
            recordMap.put(c, new int[]{0, 0});

        for (Match<?> m : allMatches) {
            if (m.getWinner() == null) continue;
            Competitor a = m.getCompetitorA();
            Competitor b = m.getCompetitorB();

            if (m.getWinner().equals(a)) {
                recordMap.get(a)[0]++;
                recordMap.get(b)[1]++;
            } else {
                recordMap.get(b)[0]++;
                recordMap.get(a)[1]++;
            }
        }

        Map<String, List<Competitor>> buckets = new TreeMap<>(
                (r1, r2) -> {
                    int[] a = Arrays.stream(r1.split("-")).mapToInt(Integer::parseInt).toArray();
                    int[] b = Arrays.stream(r2.split("-")).mapToInt(Integer::parseInt).toArray();
                    if (a[0] != b[0]) return Integer.compare(b[0], a[0]);
                    return Integer.compare(a[1], b[1]);
                }
        );

        for (Competitor c : competitors) {
            int[] rec = recordMap.get(c);
            String key = rec[0] + "-" + rec[1];
            buckets.computeIfAbsent(key, k -> new ArrayList<>()).add(c);
        }

        for (Map.Entry<String, List<Competitor>> entry : buckets.entrySet()) {

            String bucketKey = entry.getKey().replace("-", " – ");
            List<Competitor> players = entry.getValue();

            VBox bucketBox = new VBox(0);
            bucketBox.getStyleClass().add("bucket-box");

            StackPane headerPane = new StackPane();
            headerPane.getStyleClass().add("bucket-header");

            Label headerLabel = new Label(bucketKey);
            headerLabel.getStyleClass().add("bucket-header-text");
            headerPane.getChildren().add(headerLabel);

            VBox playersBox = new VBox(6);
            playersBox.setAlignment(Pos.CENTER);
            playersBox.getStyleClass().add("bucket-players");

            for (Competitor p : players) {
                Label nameLabel = new Label(p.getName());
                nameLabel.getStyleClass().add("bucket-player-name");
                playersBox.getChildren().add(nameLabel);
            }

            bucketBox.getChildren().addAll(headerPane, playersBox);
            root.getChildren().addAll(bucketBox, new Separator());
        }

        return root;
    }
}
