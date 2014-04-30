package Communication;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Represents a client in the game
 * Controls the connection and read write logic
 * for talking with the host
 */
public class GameClient extends GamePlayer {

    /**
     * Parametrized constructor
     * Creates a new instance of the client
     * @param name      display name of the client
     */
    public GameClient(String name) {
        super(name);
        mainRenderFrame.setMarker('O');
        mainRenderFrame.setLocation(Main.Main.WINDOW_SIZE + 100, 50);
    }

    /**
     * Displays a panel to get the host to connect to, then attempts the connection
     * On a new thread so that swing does not block it
     */
    @Override
    protected void connect() {
        Thread connectThread = new Thread(new Runnable() {
            @Override
            public void run() {
                String ip = JOptionPane.showInputDialog(mainRenderFrame, "Enter IP address for server:", "Communication.GameClient", JOptionPane.QUESTION_MESSAGE);

                if (ip == null || ip.isEmpty()) {
                    try {
                        ip = InetAddress.getLocalHost().getHostAddress();
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                        System.exit(1);
                    }
                }

                final String immutableIP = ip;
                try {
                    if (stopping) {return;}
                    Socket clientSocket = new Socket(immutableIP, Main.Main.PORT);
                    input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    output = new PrintWriter(clientSocket.getOutputStream(), true);
                    if (stopping) {return;}
                    // Exchange names for 'waiting for move from ' text
                    output.println(name);
                    opponentName = input.readLine();
                    connectionSocket = clientSocket; // Save off the completed connection socket
                    System.out.println("Connected to host: " + opponentName);
                    mainRenderFrame.hideConnectionDialog();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        connectThread.start();
        mainRenderFrame.showConnectionDialog("Connecting to host...");
        try {
            connectThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles the execution of the game by starting the read loop
     * and managing the cleanup after
     * On a new thread so that swing does not block it
     */
    @Override
    protected void startGame() {
        Thread readThread = new Thread(new Runnable() {
            @Override
            public void run() {
                String move;

                while (true) {
                    System.out.println("Posting read");
                    move = readMessage();
                    System.out.println("Read: " + move);

                    if (move == null) {
                        System.err.println("Message failed to read");
                        return;
                    }

                    if (move.equals(QUIT_KEYWORD)) {
                        return;
                    }

                    String[] splitResponse = move.split("->");
                    if (splitResponse.length != 2) {
                        System.err.println("Poorly formed communications packet");
                        continue;
                    }

                    if (splitResponse[0].equals("update")) {
                        if (splitResponse[1].equals("BAD")) {
                            System.out.println("Bad move");
                        }
                        else {
                            mainRenderFrame.hideWaitDialog();
                            mainRenderFrame.updateBoardDisplay(splitResponse[1]);
                        }
                    }
                    else if (splitResponse[0].equals("win")) {
                        mainRenderFrame.updateBoardDisplay(splitResponse[1]);
                        mainRenderFrame.showWinDialog();
                    }
                    else if (splitResponse[0].equals("lose")) {
                        mainRenderFrame.updateBoardDisplay(splitResponse[1]);
                        mainRenderFrame.showLoseDialog();
                    }
                    else if (splitResponse[0].equals("tie")) {
                        mainRenderFrame.updateBoardDisplay(splitResponse[1]);
                        mainRenderFrame.showTieDialog();
                    }
                    else {
                        System.err.println("Bad communications verb");
                    }
                }
            }
        });
        readThread.start();
        try {
            readThread.join();
            sendQuit();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Attempts to update the game board by sending a request to the host
     * @param move          encoded move to attempt to make
     */
    @Override
    protected void updateGameBoard(String move) {
        sendMessage("request->" + move);
        mainRenderFrame.showWaitDialog(opponentName);
    }

}
