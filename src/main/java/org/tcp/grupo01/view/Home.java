package org.tcp.grupo01.view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.tcp.grupo01.services.ServiceRegistry;

import java.io.IOException;
import java.util.Objects;

public class Home extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
            getClass().getResource("/org/tcp/grupo01/home.fxml")
        );
        
        Scene scene = new Scene(fxmlLoader.load(), 1000, 700);

        scene.getStylesheets().add(
             Objects.requireNonNull(getClass().getResource("/org/tcp/grupo01/styles/style.css")).toExternalForm()
        );
        stage.setTitle("MatchUp - Gerenciador de Campeonatos");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}