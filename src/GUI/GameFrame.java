package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameFrame extends JFrame {

    private String name;
    protected JLabel label;
    protected JTextField text;

    public GameFrame(String name) {
        this.name = name;
        setTitle("Tic-Tac-Toe: Player " + this.name);
        setLayout(new BorderLayout());
        setSize(new Dimension(Main.Main.WINDOW_SIZE, Main.Main.WINDOW_SIZE));
        setLocation(50, 50);

        label = new JLabel("Something");

        text = new JTextField(20);
        text.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Enter pressed");
            }
        });

        add(label, BorderLayout.NORTH);
        add(text, BorderLayout.SOUTH);
    }
}
