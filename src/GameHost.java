import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class GameHost implements Runnable {

    private Game game;
    private JFrame mainFrame;
    private JLabel label;
    private JTextField text;

    private PrintWriter output;
    private String hostName;
    private String clientName;

    public GameHost(String name) {
        game = new Game();
        hostName = name;

        mainFrame = new JFrame("Tic-Tac-Toe: Player " + name);
        mainFrame.setLayout(new BorderLayout());
        mainFrame.setSize(new Dimension(Main.WINDOW_SIZE, Main.WINDOW_SIZE));
        mainFrame.setLocation(50,50);

        label = new JLabel("Text that I want");

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
        try {
            label.setText("Connect to " + InetAddress.getLocalHost().getHostAddress() + ".");
        } catch(UnknownHostException e) {
            e.printStackTrace();
            System.exit(1);
        }

        try {
            ServerSocket serverSocket = new ServerSocket(Main.PORT);
            Socket gameClientSocket = serverSocket.accept();
            BufferedReader input = new BufferedReader(new InputStreamReader(gameClientSocket.getInputStream()));
            output = new PrintWriter(gameClientSocket.getOutputStream(), true);

            // Exchange names for 'waiting for move from ' text
            clientName = input.readLine();
            output.println(hostName);
            label.setText("Connected to " + clientName);
            String move;

            do {
                move = input.readLine();

                label.setText(move);

            } while (!move.equalsIgnoreCase("raggle fraggle"));

            output.println("raggle fraggle");

            gameClientSocket.close();
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendTurn(String move) {
        output.println(move);
    }
}
