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

    public GameFrame(String name) {
        gameCellPanels = new ArrayList<GameCellPanel>();

        setTitle("Tic-Tac-Toe: Player " + name);
        setLayout(new BorderLayout());
        setSize(new Dimension(Main.Main.WINDOW_SIZE, Main.Main.WINDOW_SIZE));
        setLocation(50, 50);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                if (gameFrameEventListener != null) {
                    gameFrameEventListener.onQuitButtonPressed();
                }
            }
        });

        add(createGameControlPanel(), BorderLayout.SOUTH);
        add(createGameStatusPanel(), BorderLayout.CENTER);
    }

    public void update() {
        for (GameCellPanel cellPanel : gameCellPanels) {
            cellPanel.update();
        }
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

    private enum GameCellState {
        EMPTY,
        OCCUPIED_X,
        OCCUPIED_Y
    }

    private class GameCellPanel extends JPanel {

        private GameCellState state;
        private int index;

        public GameCellPanel(int index) {
            state = GameCellState.EMPTY;
            this.index = index;
            setBackground(Color.GRAY);
        }

        public void update() {
            switch (state) {
                case EMPTY:
                    setBackground(Color.BLACK);
                    break;
                case OCCUPIED_X:
                    setBackground(Color.RED);
                    break;
                case OCCUPIED_Y:
                    setBackground(Color.BLUE);
                    break;
            }
        }
    }
}
