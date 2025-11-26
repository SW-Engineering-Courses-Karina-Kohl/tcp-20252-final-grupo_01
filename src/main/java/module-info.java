module org.tcp.grupo01 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;

    opens org.tcp.grupo01 to javafx.fxml;
    opens org.tcp.grupo01.view to javafx.fxml, javafx.graphics;
    opens org.tcp.grupo01.controller to javafx.fxml;  // Adicione esta linha

    exports org.tcp.grupo01;
    exports org.tcp.grupo01.view;
    opens org.tcp.grupo01.view.components to javafx.fxml;
}