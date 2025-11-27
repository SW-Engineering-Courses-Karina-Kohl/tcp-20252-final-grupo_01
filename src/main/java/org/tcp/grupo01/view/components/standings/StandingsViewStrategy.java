package org.tcp.grupo01.view.components.standings;

import javafx.scene.Node;
import org.tcp.grupo01.models.Tournament;

public interface StandingsViewStrategy {
    Node render(Tournament<?> tournament);
}
