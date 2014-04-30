package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;

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

    public GameFrame(String name) {
        gameCellPanels = new ArrayList<GameCellPanel>();

        setTitle("Tic-Tac-Toe: Player " + name);
        setLayout(new BorderLayout());
        setSize(new Dimension(Main.Main.WINDOW_SIZE, Main.Main.WINDOW_SIZE));
        setLocation(50, 50);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
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

    public void updateBoardDisplay(String boardDescriptor) {
        for (int i = 0; i < boardDescriptor.length(); ++i) {
            gameCellPanels.get(i).updateCell(boardDescriptor.charAt(i));
        }
    }

    public void showWaitDialog(String opponentName) {
        waitDialog = createButtonlessDialogWithMessage("Waiting for input from " + opponentName + "...");
        waitDialog.setVisible(true);
        setEnabled(false);
    }

    public void showWinDialog() {
        winDialog = createButtonlessDialogWithMessage("You win!");
        winDialog.setVisible(true);
        setEnabled(false);
    }

    public void showLoseDialog() {
        loseDialog = createButtonlessDialogWithMessage("You lose...");
        loseDialog.setVisible(true);
        setEnabled(false);
    }

    public void showTieDialog(){
        tieDialog = createButtonlessDialogWithMessage("It's a tie.");
        tieDialog.setVisible(true);
        setEnabled(false);
    }

    public void showConnectionDialog(String message) {
        connectingDialog = createButtonlessDialogWithMessage(message);
        connectingDialog.setVisible(true);
        setEnabled(false);
    }

    public void hideWaitDialog() {
        if (waitDialog == null) {
            return;
        }
       waitDialog.dispose();
        setEnabled(true);
    }

    public void hideWinDialog() {
        if (winDialog == null) {
            return;
        }
        winDialog.dispose();
        setEnabled(true);

    }

    public void hideLoseDialog() {
        if (loseDialog == null) {
            return;
        }
        loseDialog.dispose();
        setEnabled(true);

    }

    public void hideTieDialog() {
        if (tieDialog == null) {
            return;
        }
        tieDialog.dispose();
        setEnabled(true);

    }

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
                if (gameFrameEventListener != null) {
                    gameFrameEventListener.onQuitButtonPressed();
                }
            }
        });
        dialog.pack();
        return dialog;
    }

    public void setGameFrameEventListener(GameFrameEventListener gameFrameEventListener) {
        this.gameFrameEventListener = gameFrameEventListener;
    }

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
            tempPanel = new GameCellPanel(i);
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

    public interface GameFrameEventListener {
        public void onQuitButtonPressed();
        public void onValidLocationClicked(String encodedClickMessage);
    }

    private class GameCellPanel extends JPanel {

        private int index;
        private char marker;
        private int inset;


        public GameCellPanel(int index) {
            this.index = index;
            this.marker = '.';
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createLineBorder(Color.BLACK, 5));
            inset = (int)(getWidth() * .05);
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D)g;
            g2d.setStroke(new BasicStroke(5.0f));
            switch (marker) {
                case 'X':
                    g2d.setColor(Color.RED);
                    g2d.drawLine(inset, inset, getWidth() - inset, getWidth() - inset);
                    g2d.drawLine(getWidth() - inset, inset, inset, getWidth() - inset);
                    break;
                case 'Y':
                    g2d.setColor(Color.BLUE);
                    g2d.draw(new Ellipse2D.Double(getWidth() / 2, getHeight() / 2, getWidth() - 2 * inset, getHeight() - 2 * inset));
                    break;
                default:
                    break;
            }
        }

        public void updateCell(char marker) {
            this.marker = marker;
            invalidate();
            repaint();
        }
    }
}
