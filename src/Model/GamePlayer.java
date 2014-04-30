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

public abstract class GamePlayer implements Runnable, GameFrame.GameFrameEventListener {

    protected static final String QUIT_KEYWORD = ":quit:";

    protected String name;
    protected String opponentName;

    protected Socket connectionSocket;
    protected BufferedReader input;
    protected PrintWriter output;

    protected GameFrame mainRenderFrame;

    protected boolean stopping;


    public GamePlayer(String name) {
        this.name = name;
        stopping = false;
        mainRenderFrame = new GameFrame(name);
        mainRenderFrame.setVisible(true);
        mainRenderFrame.setGameFrameEventListener(this);
    }

    protected abstract void connect();
    protected abstract void startGame() throws IOException;

    @Override
    public void run() {
        try {
            connect();
            startGame();
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

    protected abstract void updateGameBoard(String move);

    public void disconnect() {
        stopping = true; // This causes the game loop to end and the resources to be released
    }

    @Override
    public void onQuitButtonPressed() {
        disconnect();
    }

    @Override
    public void onValidLocationClicked(String encodedClickMessage) {
        updateGameBoard(encodedClickMessage);
    }

    public String getName() { return name; }
}
