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
    protected abstract void startGame();

    @Override
    public void run() {
        connect();
        if (input != null && output != null && connectionSocket != null) {
            System.out.println("Starting game");
            startGame();
        }
        try {
            if (input != null) {
                System.out.println("Closing input");
                input.close();
            }

            if (output != null) {
                System.out.println("Closing output");
                output.close();
            }

            if (connectionSocket != null) {
                System.out.println("Closing connection socket");
                connectionSocket.close();
            }
        }
        catch (IOException e) {
            System.err.println("Exception closing resources. Message: " + e.getMessage());
            e.printStackTrace();
            System.exit(0);
        }
        System.exit(0);
    }

    protected abstract void updateGameBoard(String move);

    @Override
    public void onQuitButtonPressed() {
        System.out.println("In quit callback");
        stopping = true; // This causes the game loop to end and the resources to be released
        mainRenderFrame.dispose();
        System.out.println("Disposed");
        sendQuit();
    }

    @Override
    public void onValidLocationClicked(String encodedClickMessage) {
        updateGameBoard(encodedClickMessage);
    }

    public String getName() { return name; }

    protected void sendMessage(String message) {
        if (output != null) {
            System.out.println("writeToOpponent: " + message);
            output.println(message);
        }
    }

    protected String readMessage() {
        String message = null;
        try {
            message = input.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("readMessage: " + message);
        return message;
    }

    protected void sendQuit() {
        sendMessage(QUIT_KEYWORD);
    }
}
