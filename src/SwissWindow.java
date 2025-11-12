import javax.swing.*;
import java.awt.*;

public class SwissWindow extends JFrame {
    public SwissWindow() {
        super("Modo Suíço:");
        setSize(400, 300);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // Centraliza na tela

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 1, 0, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        JButton b1 = new JButton("Botão 1");
        JButton b2 = new JButton("Botão 2");
        JButton b3 = new JButton("Voltar");

        b1.addActionListener(e -> JOptionPane.showMessageDialog(this, "Você clicou no botão 1 do modo suíço"));
        b2.addActionListener(e -> JOptionPane.showMessageDialog(this, "Você clicou no botão 2 do modo suíço"));
        b3.addActionListener(e -> dispose());

        panel.add(b1);
        panel.add(b2);
        panel.add(b3);

        add(panel);
        setVisible(true);
    }
}


