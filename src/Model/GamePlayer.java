package Model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public abstract class GamePlayer implements Runnable {

    protected static final String QUIT_KEYWORD = ":quit:";

    protected String connectionMessage;
    protected String gamePlayerIdentifier;

    protected String hostName;
    protected String clientName;

    protected Socket connectionSocket;
    protected BufferedReader input;
    protected PrintWriter output;

    public GamePlayer(String name) {
        clientName = name;
    }

    protected abstract void connect(String ip);
    protected abstract void startGame() throws IOException;

    @Override
    public void run() {
        try {
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


    public void sendTurn(String move) {
        output.println(move);
    }

    public void onDisconnect() {

    }

    public String getHostName() {
        return hostName;
    }

    public String getClientName() {
        return clientName;
    }

    public String getConnectionMessage() {
        return connectionMessage;
    }

    public String getGamePlayerIdentifier() {
        return gamePlayerIdentifier;
    }
}
