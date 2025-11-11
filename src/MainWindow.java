import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainWindow extends JFrame {

    public MainWindow() {
        setTitle("Organizador de Torneios");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centraliza na tela

        // Painel principal
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 1, 10, 10));

        JButton btnSuico = new JButton("Torneio Suíço");
        JButton btnMataMata = new JButton("Mata-Mata");
        JButton btnPontosCorridos = new JButton("Pontos Corridos");

        btnSuico.addActionListener(e -> JOptionPane.showMessageDialog(this, "Modo Suíço selecionado!"));
        btnMataMata.addActionListener(e -> JOptionPane.showMessageDialog(this, "Modo Mata-Mata selecionado!"));
        btnPontosCorridos.addActionListener(e -> JOptionPane.showMessageDialog(this, "Modo Pontos Corridos selecionado!"));

        // Adiciona os botões ao painel
        panel.add(btnSuico);
        panel.add(btnMataMata);
        panel.add(btnPontosCorridos);

        add(panel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainWindow().setVisible(true);
        });
    }
}
