package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;

/**
 * GUI representing the main game execution
 */
public class GameFrame extends JFrame {

    private static final int NUM_ROWS = 3;
    private static final int NUM_COLS = 3;

    private GameFrameEventListener gameFrameEventListener;

    private char marker;

    private ArrayList<GameCellPanel> gameCellPanels;

    private JDialog waitDialog;
    private JDialog winDialog;
    private JDialog loseDialog;
    private JDialog connectingDialog;
    private JDialog tieDialog;

    /**
     * Parametrized constructor
     * Creates a new game frame given a name
     * @param name          name of the player this panel manages
     */
    public GameFrame(String name) {
        gameCellPanels = new ArrayList<GameCellPanel>();

        setTitle("Tic-Tac-Toe: Player " + name);
        setLayout(new BorderLayout());
        setSize(new Dimension(Main.Main.WINDOW_SIZE, Main.Main.WINDOW_SIZE));
        setLocation(50, 50);

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (gameFrameEventListener != null) {
                    gameFrameEventListener.onQuitButtonPressed();
                }
                System.out.println("Game window closing");
                super.windowClosing(e);
            }
        });

        add(createGameControlPanel(), BorderLayout.SOUTH);
        add(createGameStatusPanel(), BorderLayout.CENTER);
    }

    /**
     * Updates the display of the board by updating all the cells
     * @param boardDescriptor           board string used to update the board
     */
    public void updateBoardDisplay(String boardDescriptor) {
        for (int i = 0; i < boardDescriptor.length(); ++i) {
            gameCellPanels.get(i).updateCell(boardDescriptor.charAt(i));
        }
    }

    /**
     * Shows the dialog prompting the user to wait for their opponent
     * @param opponentName              the opponent's name
     */
    public void showWaitDialog(String opponentName) {
        waitDialog = createButtonlessDialogWithMessage("Waiting for input from " + opponentName + "...");
        waitDialog.setVisible(true);
        setEnabled(false);
    }

    /**
     * Shows a dialog indicating that the user has won
     */
    public void showWinDialog() {
        winDialog = createButtonlessDialogWithMessage("You win!");
        winDialog.setVisible(true);
        setEnabled(false);
    }

    /**
     * Shows a dialog indicating that the user has lost
     */
    public void showLoseDialog() {
        loseDialog = createButtonlessDialogWithMessage("You lose...");
        loseDialog.setVisible(true);
        setEnabled(false);
    }

    /**
     * Shows a dialog indicating that the game is a tie
     */
    public void showTieDialog(){
        tieDialog = createButtonlessDialogWithMessage("It's a tie.");
        tieDialog.setVisible(true);
        setEnabled(false);
    }

    /**
     * Shows a please wait dialog for the connection to complete
     * @param message               specific message for the connection completion
     */
    public void showConnectionDialog(String message) {
        connectingDialog = createButtonlessDialogWithMessage(message);
        connectingDialog.setVisible(true);
        setEnabled(false);
    }

    /**
     * Hides the wait dialog
     */
    public void hideWaitDialog() {
        if (waitDialog == null) {
            return;
        }
       waitDialog.dispose();
        setEnabled(true);
    }

    /**
     * Hides the win dialog
     */
    public void hideWinDialog() {
        if (winDialog == null) {
            return;
        }
        winDialog.dispose();
        setEnabled(true);

    }

    /**
     * Hides the lose dialog
     */
    public void hideLoseDialog() {
        if (loseDialog == null) {
            return;
        }
        loseDialog.dispose();
        setEnabled(true);

    }

    /**
     * Hides the tie dialog
     */
    public void hideTieDialog() {
        if (tieDialog == null) {
            return;
        }
        tieDialog.dispose();
        setEnabled(true);

    }

    /**
     * Hides the connection waiting to complete dialog
     */
    public void hideConnectionDialog() {
        if (connectingDialog == null) {
            return;
        }
        connectingDialog.dispose();
        setEnabled(true);

    }

    private JDialog createButtonlessDialogWithMessage(String message) {
        // Create the content pane for the connecting message
        JOptionPane optionPane = new JOptionPane(message,
                JOptionPane.INFORMATION_MESSAGE,
                JOptionPane.DEFAULT_OPTION,
                null,
                new Object[]{},
                null);
        JDialog dialog = new JDialog();
        dialog.setTitle("Message");
        if (marker == 'X') {
            dialog.setLocation((Main.Main.WINDOW_SIZE - 75) / 2, (Main.Main.WINDOW_SIZE - 50) / 2);
        }
        else {
            dialog.setLocation((Main.Main.WINDOW_SIZE + 25) * 3 / 2, (Main.Main.WINDOW_SIZE + 25) / 2);
        }
        //dialog.setModal(true);
        dialog.setContentPane(optionPane);
        dialog.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.out.println("Dialog close detected");
                if (gameFrameEventListener != null) {
                    System.out.println("Notifying listener");
                    gameFrameEventListener.onQuitButtonPressed();
                }
                super.windowClosing(e);
            }
        });
        dialog.pack();
        return dialog;
    }

    /**
     * Sets the listener for game events
     * @param gameFrameEventListener            the listener to set
     */
    public void setGameFrameEventListener(GameFrameEventListener gameFrameEventListener) {
        this.gameFrameEventListener = gameFrameEventListener;
    }

    /**
     * Sets the marker for the player
     * @param marker        marker to set
     */
    public void setMarker(char marker) {
        if (marker != 'X' && marker != 'O') {
            return;
        }
        this.marker = marker;
    }


    private JPanel createGameControlPanel() {
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BorderLayout());
        JButton quitButton = new JButton("Quit Game");
        quitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (gameFrameEventListener != null) {
                    gameFrameEventListener.onQuitButtonPressed();
                }
            }
        });
        controlPanel.add(quitButton);

        return controlPanel;
    }

    private JPanel createGameStatusPanel() {
        JPanel statusPanel = new JPanel();
        statusPanel.setLayout(new GridLayout(NUM_ROWS, NUM_COLS));
        GameCellPanel tempPanel;
        for (int i = 0; i < NUM_COLS * NUM_ROWS; ++i) {
            tempPanel = new GameCellPanel();
            final int finalIndex = i;
            tempPanel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    super.mouseClicked(e);
                    System.out.println("Got click -> " + encode(finalIndex, marker));
                    if (gameFrameEventListener != null) {
                        gameFrameEventListener.onValidLocationClicked(encode(finalIndex, marker));
                    }
                }
            });
            statusPanel.add(tempPanel);
            gameCellPanels.add(tempPanel);
        }

        return statusPanel;
    }

    private String encode(int index, char marker) {
        return String.format("%d:%c", index, marker);
    }

    /**
     * Used to update another class of events pertaining to
     * certain UI events
     */
    public interface GameFrameEventListener {
        /**
         * Notifies that the quit button has been pressed
         * or that the window has been closed
         */
        public void onQuitButtonPressed();

        /**
         * Notifies that a location has been clicked for the next move
         * @param encodedClickMessage           move message generated from the click
         */
        public void onValidLocationClicked(String encodedClickMessage);
    }
}
