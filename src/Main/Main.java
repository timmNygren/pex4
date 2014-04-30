package Main;

import Model.GameClient;
import Model.GameHost;
import Model.GamePlayer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Startup class for an instance of the game
 */
public class Main {

    /**
     * Window size used for sizing components
     */
    public static final int WINDOW_SIZE = 800;

    /**
     * Communications port
     */
    public static final int PORT = 51042;


    private String name;
    private JFrame mainFrame;
    private GamePlayer currentGame;

    /**
     * Default constructor
     * Creates a new instance of the game
     */
    public Main() {

        mainFrame = new JFrame("Tic-Tac-Toe");
        mainFrame.setLayout(new BorderLayout());
        mainFrame.setLocation(50,50);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JButton serverButton = new JButton("Host a game");
        serverButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentGame = new GameHost(name);
                Thread listener = new Thread(currentGame);
                listener.start();
                mainFrame.setVisible(false);
            }
        });
        JButton clientButton = new JButton("Join a game");
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
            name = "Tic-Tac-Toe Beginner";
        }
        mainFrame.setTitle("Tic-Tac-Toe: Player " + name);
    }

    /**
     * Startup method for the application
     * @param args          command line arguments
     */
    public static void main(String[] args) {
        Main main = new Main();
    }
}
