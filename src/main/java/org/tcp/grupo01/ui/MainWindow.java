package org.tcp.grupo01.ui;

import javax.swing.*;
import java.awt.*;

public class MainWindow extends JFrame {

    public MainWindow() {
        setTitle("Organizador de Torneios");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centraliza na tela

        // Painel principal
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 1, 10, 10));
        
        JButton swissButton = new JButton("Torneio Suíço");
        JButton knockoutButton = new JButton("Mata-Mata");
        JButton roundRobbinButton = new JButton("Pontos Corridos");

        //criando botões ao clicar um botão:
        swissButton.addActionListener(e -> new SwissWindow());
        knockoutButton.addActionListener(e -> new KnockoutWindow());
        roundRobbinButton.addActionListener(e -> new RoundRobbinWindow());

        // Adiciona os botões ao painel
        panel.add(swissButton);
        panel.add(knockoutButton);
        panel.add(roundRobbinButton);

        add(panel);
    }
}
