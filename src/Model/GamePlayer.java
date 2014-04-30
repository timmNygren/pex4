package Model;

import GUI.GameFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public abstract class GamePlayer implements Runnable {

    protected static final String QUIT_KEYWORD = ":quit:";

    protected String name;
    protected String opponentName;

    protected Socket connectionSocket;
    protected BufferedReader input;
    protected PrintWriter output;

    protected GameFrame mainRenderFrame;


    public GamePlayer(String name) {
        this.name = name;
        mainRenderFrame = new GameFrame(name);
        mainRenderFrame.setVisible(true);
    }

    protected abstract void connect();
    protected abstract void startGame() throws IOException;

    @Override
    public void run() {
        try {
            System.out.println("Running thread");
            System.out.println("Connecting");
            connect();
            System.out.println("Connected, starting game");
            startGame();
            System.out.println("Game ended");
        }
        catch (IOException e) {
            System.err.println("Exception in game execution, socket could not be read or written");
            System.err.println("Exception message: " + e.getMessage());
            e.printStackTrace();
        }

        try {
            if (input != null) {
                input.close();
            }

            if (output != null) {
                output.close();
            }

            if (connectionSocket != null) {
                connectionSocket.close();
            }
        }
        catch (IOException e) {
            System.err.println("Exception closing resources. Message: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public void sendTurn(String move) {
        output.println(move);
    }

    public void onDisconnect() {

    }

    public String getName() { return name; }
}
