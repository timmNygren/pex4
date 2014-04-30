package Model;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class GameClient extends GamePlayer {

    private JDialog connectingDialog;
    private boolean waitingForResponse;

    private String lastMove;

    public GameClient(String name) {
        super(name);
        mainRenderFrame.setMarker('O');
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
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }

    protected void startGame() {

        String move;

        do {
            move = readMessage();
            if (move == null) {
                System.err.println("Message failed to read");
                return;
            }

            String[] splitResponse = move.split("->");
            if (splitResponse.length != 2) {
                System.err.println("Poorly formed communications packet");
                continue;
            }

            if (splitResponse[0].equals("update")) {
                if (splitResponse[1].equals("BAD")) {
                    updateGameBoard(lastMove);
                }
                else {
                    mainRenderFrame.hideWaitDialog();
                    mainRenderFrame.updateBoardDisplay(splitResponse[1]);
                }
            }
            else {
                System.err.println("Bad communications verb");
            }
        } while (!move.equalsIgnoreCase(QUIT_KEYWORD) && !stopping);
    }

    @Override
    protected void updateGameBoard(String move) {
        lastMove = move;
        sendMessage("request->" + move);
        mainRenderFrame.showWaitDialog();
    }

}
