package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class GameFrame extends JFrame {

    private static final int NUM_ROWS = 3;
    private static final int NUM_COLS = 3;

    private GameFrameEventListener gameFrameEventListener;

    private char marker;

    private ArrayList<GameCellPanel> gameCellPanels;

    private JDialog connectingDialog;

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

    public void showWaitDialog() {
        System.out.println("wait:show");
    }

    public void hideWaitDialog() {
        System.out.println("wait:hide");
    }

    public void showWinDialog() {
        System.out.println("win:show");
    }

    public void hideWinDialog() {
        System.out.println("win:hide");
    }

    public void showLoseDialog() {
        System.out.println("lose:show");
    }

    public void hideLoseDialog() {
        System.out.println("lose:hide");
    }

    public void showConnectionDialog(String message) {
        // Create the content pane for the connecting message
        JOptionPane optionPane = new JOptionPane(message,
                JOptionPane.INFORMATION_MESSAGE,
                JOptionPane.DEFAULT_OPTION,
                null,
                new Object[]{},
                null);
        connectingDialog = new JDialog();
        connectingDialog.setTitle("Message");
        connectingDialog.setModal(true);
        connectingDialog.setContentPane(optionPane);
        connectingDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

        connectingDialog.pack();
        connectingDialog.setVisible(true);
//        connectingDialog.addWindowListener(new WindowAdapter() {
//            @Override
//            public void windowClosing(WindowEvent e) {
//                stopping = true;
//                removeConnectingDialog();
//            }
//        });
    }

    public void hideConnectionDialog() {
        System.out.println("Removing the dialog");
        // Remove the connecting dialogue
        connectingDialog.dispose();
        System.out.println("Dialog removed");
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

        public GameCellPanel(int index) {
            this.index = index;
            setBackground(Color.GRAY);
        }

        public void updateCell(char marker) {
            switch (marker) {
                case 'X':
                    setBackground(Color.RED);
                    break;
                case 'O':
                    setBackground(Color.BLUE);
                    break;
                default:
                    setBackground(Color.GRAY);
                    break;
            }
            invalidate();
            repaint();
        }
    }
}
