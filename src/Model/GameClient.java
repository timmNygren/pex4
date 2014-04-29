package Model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class GameClient extends GamePlayer {

    public GameClient(String name) {
        super(name);
        connectionMessage = "Enter IP address for server:";
        gamePlayerIdentifier = "Model.Game Client";
    }

    protected void connect(String ip) {


        if (ip == null || ip.isEmpty()) {
            try {
                ip = InetAddress.getLocalHost().getHostAddress();
            } catch (UnknownHostException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }

        try {
            Socket clientSocket = new Socket(ip, Main.Main.PORT);
            input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            output = new PrintWriter(clientSocket.getOutputStream(), true);

            // Exchange names for 'waiting for move from ' text
            output.println(name);
            opponentName = input.readLine();
            // label.setText("Connected to " + hostName);
            // TODO: Make connected popup?

            connectionSocket = clientSocket; // Save off the completed connection socket
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void startGame() throws IOException {
        String move;

        do {
            move = input.readLine();
            System.out.println(move);
        } while (!move.equalsIgnoreCase(QUIT_KEYWORD));

    }
}
