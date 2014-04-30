package Model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Represents a client in the game
 * Controls the connection and read write logic
 * for talking with the client
 */
public class GameHost extends GamePlayer {

    private Game game;
    private ServerSocket serverSocket;

    /**
     * Parametrized constructor
     * Creates a new instance of the host
     * @param name      display name of the host
     */
    public GameHost(String name) {
        super(name);
        game = new Game();
        mainRenderFrame.setMarker('X');
        mainRenderFrame.setLocation(50, 50);
    }

    /**
     * Displays a panel with the ip for connecting and awaits a connection
     * On a new thread so that swing does not block it
     */
    @Override
    protected void connect() {
        String connectingIP = "BAD IP";
        try {
            connectingIP = InetAddress.getLocalHost().getHostAddress();
        } catch(UnknownHostException e) {
            e.printStackTrace();
            System.exit(1);
        }
        Thread connectThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                    serverSocket = new ServerSocket(Main.Main.PORT);
                    Socket gameClientSocket = serverSocket.accept();
                    input = new BufferedReader(new InputStreamReader(gameClientSocket.getInputStream()));
                    output = new PrintWriter(gameClientSocket.getOutputStream(), true);
                    opponentName = input.readLine();
                    output.println(name);
                    connectionSocket = gameClientSocket;
                    System.out.println("Client connected: " + opponentName);
                    mainRenderFrame.hideConnectionDialog();
                }
                catch (IOException e) {
                    e.printStackTrace();

                }
            }
        });
        connectThread.start();
        mainRenderFrame.showConnectionDialog("Tell your opponent to connect to " + connectingIP);
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
        // Client never goes first
        mainRenderFrame.showWaitDialog(opponentName);
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

                    if (splitResponse[0].equals("request")) {
                        if (game.makeMove(splitResponse[1])) {
                            // Move apply success
                            mainRenderFrame.hideWaitDialog();
                            mainRenderFrame.updateBoardDisplay(game.getGameString());
                            if (game.checkForWin()) {
                                sendMessage("win->" + game.getGameString());
                                mainRenderFrame.showLoseDialog();
                                mainRenderFrame.updateBoardDisplay(game.getGameString());
                                // Stop the read loop here since the game is over
                                break;
                            }
                            else if (game.checkForTie()) {
                                sendMessage("tie->" + game.getGameString());
                                mainRenderFrame.showTieDialog();
                                mainRenderFrame.updateBoardDisplay(game.getGameString());
                            }
                            else {
                                sendMessage("update->" + game.getGameString());
                            }
                        }
                        else { // Bad move received
                            sendMessage("update->BAD");
                        }
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
     * Attempts to update the game board by directly invoking the methods on the
     * game object. Also checks win and tie conditions here
     * @param move          encoded move to attempt to make
     */
    @Override
    protected void updateGameBoard(String move) {
        if (game.makeMove(move)) {
            if (game.checkForWin()) {
                sendMessage("lose->" + game.getGameString());
                mainRenderFrame.showWinDialog();
            }
            else {
                sendMessage("update->" + game.getGameString());
                mainRenderFrame.showWaitDialog(opponentName);
                mainRenderFrame.updateBoardDisplay(game.getGameString());
            }
        }
    }

    /**
     * Attempts to close the server socket on close,
     * in case a connection has not yet been made.
     */
    @Override
    public void onQuitButtonPressed() {
        super.onQuitButtonPressed();
        try {
            System.out.println("Closing the server socket");
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
