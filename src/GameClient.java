import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.Buffer;

/**
 * Created by Samus on 4/24/14.
 */
public class GameClient implements Runnable, TurnSender {

    private JFrame mainFrame;
    private JLabel label;
    private JTextField text;
    private String hostName;
    private String clientName;

    private PrintWriter output;

    public GameClient(String name) {
        clientName = name;

        mainFrame = new JFrame("Tic-Tac-Toe: Player " + name);
        mainFrame.setLayout(new BorderLayout());
        mainFrame.setSize(new Dimension(Main.WINDOW_SIZE, Main.WINDOW_SIZE));
        mainFrame.setLocation(50, 50);

        label = new JLabel("Client Text that I want");

        text = new JTextField(20);
        text.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String update = text.getText();
                sendTurn(update);
            }
        });

        mainFrame.add(label, BorderLayout.NORTH);
        mainFrame.add(text, BorderLayout.SOUTH);


        mainFrame.setVisible(true);
    }

    @Override
    public void run() {
        String ip = JOptionPane.showInputDialog(mainFrame, "Enter IP address for server:", "Game Client", JOptionPane.QUESTION_MESSAGE);

        if (ip == null || ip.isEmpty()) {
            try {
                ip = InetAddress.getLocalHost().getHostAddress();
            } catch (UnknownHostException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }

        try {
            Socket clientSocket = new Socket(ip, Main.PORT);
            BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            output = new PrintWriter(clientSocket.getOutputStream(), true);

            // Exchange names for 'waiting for move from ' text
            output.println(clientName);
            hostName = input.readLine();
            label.setText("Connected to " + hostName);
            String move;

            do {
                move = input.readLine();

                label.setText(move);

            } while (!move.equalsIgnoreCase("raggle fraggle"));

            output.println("raggle fraggle");

            clientSocket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendTurn(String move) {
        output.println(move);
    }
}
