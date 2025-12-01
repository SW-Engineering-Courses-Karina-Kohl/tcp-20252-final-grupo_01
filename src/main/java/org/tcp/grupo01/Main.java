package org.tcp.grupo01;

import org.tcp.grupo01.services.ServiceRegistry;
import org.tcp.grupo01.view.Home;

public class Main {
    public static void main(String[] args) {
        ServiceRegistry.initializeDummyData();
        Home.main(args);
    }
}
