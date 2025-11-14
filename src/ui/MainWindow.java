package ui;

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
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(245, 245, 245)); // cinza claro elegante
        panel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        // Título estilizado
        JLabel title = new JLabel("Organizador de Torneios");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // Botões estilizados
        JButton swissButton = createStyledButton("Torneio Suíço");
        JButton knockoutButton = createStyledButton("Mata-Mata");
        JButton roundRobbinButton = createStyledButton("Pontos Corridos");

        //criando botões ao clicar um botão:
        swissButton.addActionListener(e -> new SwissWindow());
        knockoutButton.addActionListener(e -> new KnockoutWindow());
        roundRobbinButton.addActionListener(e -> new RoundRobbinWindow());

        // Adiciona os botões ao painel
        panel.add(title);
        panel.add(swissButton);
        panel.add(Box.createVerticalStrut(15));
        panel.add(knockoutButton);
        panel.add(Box.createVerticalStrut(15));
        panel.add(roundRobbinButton);

        add(panel);
    }

    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);

        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 16));
        btn.setBackground(new Color(66, 135, 245));
        btn.setForeground(Color.WHITE);
        btn.setPreferredSize(new Dimension(200, 45));
        btn.setMaximumSize(new Dimension(200, 45));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Bordas Arredondadas
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Hover
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(56, 120, 230));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(66, 135, 245));
            }
        });

        return btn;
    }
}
