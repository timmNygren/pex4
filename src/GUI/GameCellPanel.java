package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;

/**
 * Represents one cell on the game grid that can contain an X or an O
 */
public class GameCellPanel extends JPanel {

    /**
     * Scalar for sizing the objects on the grid
     */
    private static final double INSET_SCALAR = 0.4;

    /**
     * An X or an O
     */
    private char marker;

    /**
     * Default constructor
     * Makes a panel with the bare minimum
     */
    public GameCellPanel() {
        this.marker = '.';
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createLineBorder(Color.BLACK, 5));
    }

    /**
     * Paints the component, giving it an X, an O, or nothing as necessary
     * @param g             graphics used to draw
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        int insetX = (int) (getPreferredSize().width * INSET_SCALAR);
        int insetY = (int) (getPreferredSize().height * INSET_SCALAR);
        Graphics2D g2d = (Graphics2D)g;
        g2d.setStroke(new BasicStroke(5.0f));
        switch (marker) {
            case 'X':
                g2d.setColor(Color.RED);
                g2d.drawLine(insetX, insetY, getWidth() - insetX, getHeight() - insetY);
                g2d.drawLine(getWidth() - insetX, insetY, insetX, getHeight() - insetY);
                break;
            case 'O':
                g2d.setColor(Color.BLUE);
                g2d.draw(new Ellipse2D.Double(insetX, insetY, getWidth() - (2 * insetX), getHeight() - (2 * insetY)));
                break;
            default:
                break;
        }
    }

    /**
     * Updates the marker and triggers a repaint
     * @param marker        marker to update to
     */
    public void updateCell(char marker) {
        this.marker = marker;
        invalidate();
        repaint();
    }
}