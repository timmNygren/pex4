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

        try {
            ServerSocket serverSocket = new ServerSocket(Main.Main.PORT);
            Socket gameClientSocket = serverSocket.accept();
            input = new BufferedReader(new InputStreamReader(gameClientSocket.getInputStream()));
            output = new PrintWriter(gameClientSocket.getOutputStream(), true);

            // Exchange names for 'waiting for move from ' text
            opponentName = input.readLine();
            output.println(name);
            // label.setText("Connected to " + clientName);
            // TODO: Make connected popup?

            connectionSocket = gameClientSocket;
        }
        catch (IOException e) {
            e.printStackTrace();
        }
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
