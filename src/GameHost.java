import javax.swing.*;
import java.awt.*;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by Samus on 4/24/14.
 */
public class GameHost implements Runnable, TurnSender {

    private Game game;
    private JFrame mainFrame;
    private JLabel label;

    public GameHost() {
        game = new Game();
        mainFrame = new JFrame();
        label = new JLabel("Text that I want");
        mainFrame.add(label);
        mainFrame.setSize(new Dimension(100,100));
        mainFrame.setLocation(50,50);
        mainFrame.setVisible(true);
    }

    @Override
    public void run() {
        try {
            label.setText("Connect to " + InetAddress.getLocalHost().getHostAddress() + ".");
        } catch(UnknownHostException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    @Override
    public void makeTurn(String move) {

    }
}
