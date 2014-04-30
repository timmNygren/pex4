package Model;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class GameHost extends GamePlayer {

    private Game game;
    private JDialog connectingDialog;

    public GameHost(String name) {
        super(name);
        game = new Game();
    }

    @Override
    protected void connect() {
        try {
            label.setText(("Connect to " + InetAddress.getLocalHost().getHostAddress() + "."));
        } catch(UnknownHostException e) {
            e.printStackTrace();
            System.exit(1);
        }
        // Create the content pane for the connecting message
        JOptionPane optionPane = new JOptionPane("Awaiting for opponent to connect. Please wait.",
                JOptionPane.INFORMATION_MESSAGE,
                JOptionPane.DEFAULT_OPTION,
                null,
                new Object[]{},
                null);
        connectingDialog = new JDialog();
        connectingDialog.setTitle("Message");
        connectingDialog.setModal(true);
        connectingDialog.setContentPane(optionPane);
        connectingDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        connectingDialog.pack();
        System.out.println("Creating connection thread");
        Thread connectionThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("Staring host connection");
                    System.out.println("Creating server socket");
                    ServerSocket serverSocket = new ServerSocket(Main.Main.PORT);
                    System.out.println("Creating client socket, waiting for connection...");
                    Socket gameClientSocket = serverSocket.accept();
                    System.out.println("Connection established, getting input and output");
                    input = new BufferedReader(new InputStreamReader(gameClientSocket.getInputStream()));
                    System.out.println("Input created");
                    output = new PrintWriter(gameClientSocket.getOutputStream(), true);
                    System.out.println("Output created");
                    System.out.println("Exchanging opponent names");
                    // Exchange names for 'waiting for move from ' text
                    opponentName = input.readLine();
                    System.out.println("Got opponent name: " + opponentName);
                    System.out.println("Sending host name");
                    output.println(name);
                    System.out.println("Saving connection socket");
                    connectionSocket = gameClientSocket;
                    removeConnectingDialog();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        connectionThread.start();
        connectingDialog.setVisible(true);
    }

    private void removeConnectingDialog() {
        System.out.println("Removing the dialog");
        // Remove the connecting dialogue
        connectingDialog.dispose();
        System.out.println("Dialog removed");
    }

    @Override
    protected void startGame() throws IOException {
        String move;

        do {
            move = input.readLine();

            System.out.println(move);

        } while (!move.equalsIgnoreCase(QUIT_KEYWORD));
    }
}
