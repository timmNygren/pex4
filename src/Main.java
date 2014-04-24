import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Samus on 4/24/14.
 */
public class Main {

    private JFrame mainFrame;
    private JButton serverButton;
    private JButton clientButton;

    public Main() {
        mainFrame = new JFrame("Tic-Tac-Toe");
        mainFrame.setLayout(new BorderLayout());
        mainFrame.setLocation(50,50);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        serverButton = new JButton("Host a game");
        serverButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("CALL ME");
            }
        });
        clientButton = new JButton("Join a game");
        clientButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("CALL ME MAYBE");
            }
        });

        mainFrame.add(serverButton, BorderLayout.WEST);
        mainFrame.add(clientButton, BorderLayout.EAST);

        mainFrame.pack();
        mainFrame.setVisible(true);
    }

    public static void main(String[] args) {
        Main main = new Main();
    }
}
