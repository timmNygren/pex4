package Main;

import Model.GameClient;
import Model.GameHost;
import Model.GamePlayer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main {

    public static final int WINDOW_SIZE = 800;
    public static final int PORT = 51042;
    private final String DEFAULT_NAME = "Tic-Tac-Toe Beginner";

    private String name;

    private JFrame mainFrame;
    private JButton serverButton;
    private JButton clientButton;

    private GamePlayer currentGame;

    public Main() {

        mainFrame = new JFrame("Tic-Tac-Toe");
        mainFrame.setLayout(new BorderLayout());
        mainFrame.setLocation(50,50);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        serverButton = new JButton("Host a game");
        serverButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentGame = new GameHost(name);
                Thread listener = new Thread(currentGame);
                listener.start();
                mainFrame.setVisible(false);
            }
        });
        clientButton = new JButton("Join a game");
        clientButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentGame = new GameClient(name);
                Thread listener = new Thread(currentGame);
                listener.start();
                mainFrame.setVisible(false);
            }
        });

        mainFrame.add(serverButton, BorderLayout.WEST);
        mainFrame.add(clientButton, BorderLayout.EAST);

        mainFrame.pack();
        mainFrame.setVisible(true);

        name = JOptionPane.showInputDialog( mainFrame, "Enter a name:", "Tic-Tac-Toe", JOptionPane.QUESTION_MESSAGE );
        if (name == null || name.isEmpty()) {
            name = DEFAULT_NAME;
        }
        mainFrame.setTitle("Tic-Tac-Toe: Player " + name);
    }

    public static void main(String[] args) {
        Main main = new Main();
    }
}
