package Model;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.renderable.RenderableImageProducer;
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
        mainRenderFrame.setMarker('X');
    }

    // On a new thread so that swing does not block it
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
                    ServerSocket serverSocket = new ServerSocket(Main.Main.PORT);
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

    // On a new thread so that swing does not block it
    @Override
    protected void startGame() {
        Thread readThread = new Thread(new Runnable() {
            @Override
            public void run() {
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

                    if (splitResponse[0].equals("request")) {
                        if (game.makeMove(splitResponse[1])) {
                            // Move apply success
                            mainRenderFrame.hideWaitDialog();
                            updateGameBoard(game.getGameString());
                            if (game.checkForWin()) {
                                sendMessage("win->" + game.getGameString());
                                mainRenderFrame.showLoseDialog();
                                // Stop the read loop here since the game is over
                                break;
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
                } while (!move.equalsIgnoreCase(QUIT_KEYWORD) && !stopping);
            }
        });
        readThread.start();
        try {
            readThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

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
}
