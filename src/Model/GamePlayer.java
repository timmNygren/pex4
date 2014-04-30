package Model;

import GUI.GameFrame;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Base class for a player of the game, encapsulates the interfaces necessary
 * and provides passthroughs and variables to the hosts and clients
 */
public abstract class GamePlayer implements Runnable, GameFrame.GameFrameEventListener {

    /**
     * Keyword to stop the game with, if encountered
     */
    protected static final String QUIT_KEYWORD = ":quit:";

    /**
     * Name of this player
     */
    protected String name;

    /**
     * Name of the opposing player
     */
    protected String opponentName;

    /**
     * Socket used for communication
     */
    protected Socket connectionSocket;

    /**
     * Used for reading information from the opponent
     */
    protected BufferedReader input;

    /**
     * Used for writing information to the opponent
     */
    protected PrintWriter output;

    /**
     * Frame that is used to render the game state
     */
    protected GameFrame mainRenderFrame;

    /**
     * Used to stop the threads when the game is closing
     */
    protected boolean stopping;

    /**
     * Parametrized constructor
     * Creates the basic functionality of the base class
     * @param name          name of the player
     */
    public GamePlayer(String name) {
        this.name = name;
        stopping = false;
        mainRenderFrame = new GameFrame(name);
        mainRenderFrame.setVisible(true);
        mainRenderFrame.setGameFrameEventListener(this);
    }

    /**
     * Connects to another player
     */
    protected abstract void connect();

    /**
     * Starts the game logic for a player
     */
    protected abstract void startGame();

    /**
     * Attempts to make a change to the board
     * @param move          encoded move to attempt
     */
    protected abstract void updateGameBoard(String move);

    /**
     * Encompasses a players entire execution from start to cleanup
     */
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


    /**
     * Callback used to cleanup the threads and game when quit is pressed
     * or if a window is closed
     */
    @Override
    public void onQuitButtonPressed() {
        System.out.println("In quit callback");
        stopping = true; // This causes the game loop to end and the resources to be released
        mainRenderFrame.dispose();
        System.out.println("Disposed");
        sendQuit();
    }

    /**
     * Callback for when a location is clicked on the board when it is
     * this player's turn to move
     * @param encodedClickMessage       encoded move from the click
     */
    @Override
    public void onValidLocationClicked(String encodedClickMessage) {
        updateGameBoard(encodedClickMessage);
    }

    /**
     * Sends a message to the opponent through the output object
     * @param message                   message to send
     */
    protected void sendMessage(String message) {
        if (output != null) {
            System.out.println("writeToOpponent: " + message);
            output.println(message);
        }
    }

    /**
     * Reads the next message off of the input stream
     * @return                          the next message on the stream
     */
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

    /**
     * Convienence method for sending a quit message over the output pipe
     */
    protected void sendQuit() {
        sendMessage(QUIT_KEYWORD);
    }
}
