package Model;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class GameClient extends GamePlayer {

    private JDialog connectingDialog;

    public GameClient(String name) {
        super(name);
    }

    protected void connect() {

        String ip = JOptionPane.showInputDialog(mainRenderFrame, "Enter IP address for server:", "Model.GameClient", JOptionPane.QUESTION_MESSAGE);

            if (ip == null || ip.isEmpty()) {
            try {
                ip = InetAddress.getLocalHost().getHostAddress();
            } catch (UnknownHostException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }

        final String immutableIP = ip;

        // Create the content pane for the connecting message
        JOptionPane optionPane = new JOptionPane("Connecting to " + ip + ". Please wait.",
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
        Thread connectionThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("Start client connect");
                    System.out.println("Creating client socket");
                    Socket clientSocket = new Socket(immutableIP, Main.Main.PORT);
                    System.out.println("Created client socket, getting input and output");
                    input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    System.out.println("Input created");
                    output = new PrintWriter(clientSocket.getOutputStream(), true);
                    System.out.println("Output created");
                    System.out.println("Exchanging names");
                    // Exchange names for 'waiting for move from ' text
                    output.println(name);
                    System.out.println("Name exchanged");
                    opponentName = input.readLine();
                    System.out.println("Got an opponent name: " + opponentName);
                    connectionSocket = clientSocket; // Save off the completed connection socket
                    System.out.println("Connection complete");
                    removeConnectingDialog();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        connectionThread.start();
        connectingDialog.setVisible(true);
    }

    private void removeConnectingDialog() {
        // Remove the connection popup
        System.out.println("Removing the dialog");
        connectingDialog.dispose();
        System.out.println("Dialog disposed");
    }

    protected void startGame() throws IOException {
        String move;

        do {
            move = input.readLine();
            System.out.println(move);
        } while (!move.equalsIgnoreCase(QUIT_KEYWORD));

    }
}
