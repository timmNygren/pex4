package GUI;

import Model.GamePlayer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameRenderer {
    private GamePlayer gamePlayer;

    private JFrame mainRenderFrame;
    private JLabel label;
    private JTextField text;

    public GameRenderer(GamePlayer player) {
        gamePlayer = player;

        mainRenderFrame = new JFrame("Tic-Tac-Toe: Player " + player.getClientName());
        mainRenderFrame.setLayout(new BorderLayout());
        mainRenderFrame.setSize(new Dimension(Main.Main.WINDOW_SIZE, Main.Main.WINDOW_SIZE));
        mainRenderFrame.setLocation(50, 50);

        label = new JLabel("Client Text that I want");

        text = new JTextField(20);
        text.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String update = text.getText();
                gamePlayer.sendTurn(update);
            }
        });

        mainRenderFrame.add(label, BorderLayout.NORTH);
        mainRenderFrame.add(text, BorderLayout.SOUTH);


        mainRenderFrame.setVisible(true);

        String ip = JOptionPane.showInputDialog(mainRenderFrame, gamePlayer.getConnectionMessage(), gamePlayer.getClientName(), JOptionPane.QUESTION_MESSAGE);
        // gamePlayer.connect();
        // Run on handler
    }
}
