package Model;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
        mainRenderFrame.setMarker('X');
    }

    @Override
    protected void connect() {
        String thisIP = "BAD IP";
        try {
            thisIP = InetAddress.getLocalHost().getHostAddress();
        } catch(UnknownHostException e) {
            e.printStackTrace();
            System.exit(1);
        }
        Thread connectionThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (stopping) {return;}
                    ServerSocket serverSocket = new ServerSocket(Main.Main.PORT);
                    if (stopping) {return;}
                    Socket gameClientSocket = serverSocket.accept();
                    if (stopping) {return;}
                    input = new BufferedReader(new InputStreamReader(gameClientSocket.getInputStream()));
                    output = new PrintWriter(gameClientSocket.getOutputStream(), true);
                    if (stopping) {return;}
                    opponentName = input.readLine();
                    output.println(name);
                    connectionSocket = gameClientSocket;
                    removeConnectingDialog();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        // Create the content pane for the connecting message
        JOptionPane optionPane = new JOptionPane("Awaiting for opponent to connect to " + thisIP + ". Please wait.",
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
        connectingDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                stopping = true;
                removeConnectingDialog();
            }
        });
        connectingDialog.pack();
        System.out.println("Creating connection thread");
        stopping = false;
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

        } while (!move.equalsIgnoreCase(QUIT_KEYWORD) && !stopping);
    }
}
