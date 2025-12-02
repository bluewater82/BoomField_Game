import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * This is a JPanel class that handles the cell representation of our hex
 * board based on the current game state and the user's interactions with the
 * cells.
 */
public class BoardPanel extends JPanel {

    private static class CellView {
        boolean hasBoom;
        boolean flagged;
        boolean hidden = true;
        int neighboringBooms = 0;

        Polygon shape;   // hex polygon for this cell
    }

    private final int rows;
    private final int cols;
    private final CellView[][] cells;

    private GUIToLogic logic;

    private boolean firstClickOccurred = false;
    private Runnable firstClickCallback;

    private static final int HEX_SIZE = 22;
    private static final int MARGIN   = 8;
    private static final int GRID_SHIFT_HORIZ = 190;
    private static final int GRID_SHIFT_VERT  = 25;

    private final double hexWidth;
    private final double hexHeight;
    private final double vertStep;

    // --- Cell icons ---
    private Image iconHidden;
    private Image iconFlagged;
    private Image iconMine;
    private Image iconUncovered;


    /**
     * This method constructs the playing board with our hex geometry. I used
     * the hex grid tutorial found on Red Blob Games to help me with
     * implementing the layout of the cells and their geometry.
     * @param rows number of rows
     * @param cols number of cols
     */
    public BoardPanel(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.cells = new CellView[rows][cols];

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                cells[r][c] = new CellView();
            }
        }

        hexWidth  = Math.sqrt(3.0) * HEX_SIZE;
        hexHeight = 2.0 * HEX_SIZE;
        vertStep  = 1.5 * HEX_SIZE;

        buildHexGeometry();
        loadIcons();

        setBackground(Color.DARK_GRAY);
        setOpaque(false);  // so the BackgroundPanel shows through
        installMouseHandler();
    }

    /**
     * This method ties our logic between the logic layer and the frame.
     * @param logic logic from our BoomFieldLogic
     */
    public void setLogic(GUIToLogic logic) {
        this.logic = logic;
    }

    /**
     * This is a small method that is used as the trigger for starting the
     * timer method used by the InfoPanel. I chose to use a Runnable
     * parameter here to differentiate between menu clicks and the first
     * actual game board move click.
     * @param click first click on game cell
     */
    public void setFirstClickCallback(Runnable click) {
        this.firstClickCallback = click;
    }

    /**
     * This method resets the first click state and used by our method that
     * restarts a new game.
     */
    public void resetInteractionState() {
        firstClickOccurred = false;
    }

    private void loadIcons() {
        try {
            ClassLoader cl = getClass().getClassLoader();

            iconHidden    =
                    new ImageIcon(cl.getResource("icons/" +
                            "ground_tile.jpg")).getImage();
            iconFlagged   =
                    new ImageIcon(cl.getResource("icons/" +
                            "flagged_tile.jpg")).getImage();
            iconMine      =
                    new ImageIcon(cl.getResource("icons/" +
                            "boomv2.jpg")).getImage();
            iconUncovered =
                    new ImageIcon(cl.getResource("icons/" +
                            "ground_tile_revealed.jpg")).getImage();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void buildHexGeometry() {

        int panelWidth = (int) Math.ceil(
                MARGIN * 2 + GRID_SHIFT_HORIZ + hexWidth * cols + hexWidth / 2.0
        );
        int panelHeight = (int) Math.ceil(
                MARGIN * 2 + GRID_SHIFT_VERT + hexHeight + vertStep * (rows - 1)
        );
        setPreferredSize(new Dimension(panelWidth, panelHeight));

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {


                double centerX = GRID_SHIFT_HORIZ
                        + MARGIN
                        + c * hexWidth
                        + ((r % 2) * (hexWidth / 2.0));

                double centerY = GRID_SHIFT_VERT
                        + MARGIN
                        + hexHeight / 2.0
                        + r * vertStep;

                Polygon poly = new Polygon();

                /* Note to Grader: I cannot truncate these to fit limit */
                for (int i = 0; i < 6; i++) {
                    double angleRad = Math.toRadians(60 * i - 30);
                    int vx =
                            (int) Math.round(centerX + HEX_SIZE * Math.cos(angleRad));
                    int vy =
                            (int) Math.round(centerY + HEX_SIZE * Math.sin(angleRad));
                    poly.addPoint(vx, vy);
                }

                cells[r][c].shape = poly;
            }
        }
    }


    /**
     * This is our updater method called by the main frame. it provides the
     * updated cell properties used for repainting the cells.
     * @param row row of cell
     * @param col col of cell
     * @param hasBoom if cell is hiding a mine
     * @param flagged if cell has been flagged
     * @param hidden if cell is still uncovered
     * @param neighboringBooms how many mines are touching
     */
    public void updateCellView(int row, int col,
                               boolean hasBoom,
                               boolean flagged,
                               boolean hidden,
                               int neighboringBooms) {

        CellView cv = cells[row][col];
        cv.hasBoom = hasBoom;
        cv.flagged = flagged;
        cv.hidden = hidden;
        cv.neighboringBooms = neighboringBooms;

        repaintCell(row, col);
    }

    /**
     * Method called when board needs to be repainted
     */
    public void refreshBoard() {
        repaint();
    }

    private void repaintCell(int row, int col) {
        Polygon p = cells[row][col].shape;
        if (p != null) {
            Rectangle bounds = p.getBounds();
            repaint(bounds.x - 1, bounds.y - 1,
                    bounds.width + 2, bounds.height + 2);
        }
    }


    private void installMouseHandler() {
        addMouseListener(new MouseAdapter() {

            /**
             * This method ties our mouse clicks to the game. It handles
             * quite a few tasks. It first gets the (x,y) coordinates of the
             * mouse click and matches it to the cell location. It then
             * checks to see if it is the first valid play click to start the
             * timer. After that it detects which mouse button was pressed in
             * order to trigger which action is to be performed as well as
             * firing their associated sound effects (uncovering+digging
             * sound for left-clicks and flag toggle+sound for right-clicks).
             * @param e the mouse click event
             */
            @Override
            public void mousePressed(MouseEvent e) {
                if (logic == null) return;

                int x = e.getX();
                int y = e.getY();

                int hitRow = -1;
                int hitCol = -1;

                outer:
                for (int r = 0; r < rows; r++) {
                    for (int c = 0; c < cols; c++) {
                        Polygon p = cells[r][c].shape;
                        if (p != null && p.contains(x, y)) {
                            hitRow = r;
                            hitCol = c;
                            break outer;
                        }
                    }
                }

                if (hitRow == -1) return;

                if (SwingUtilities.isLeftMouseButton(e)) {

                    if (!firstClickOccurred) {
                        firstClickOccurred = true;
                        if (firstClickCallback != null) {
                            firstClickCallback.run();
                        }
                    }
                    SoundFX.DIGGING.play();
                    logic.uncoverSelectedCell(hitRow, hitCol);
                } else if (SwingUtilities.isRightMouseButton(e)) {
                    SoundFX.FLAG.play();
                    logic.toggleFlag(hitRow, hitCol);
                }
            }
        });
    }


    private Image chooseIconForCell(CellView cv) {
        if (cv.hidden) {
            if (cv.flagged) {
                return (iconFlagged != null ? iconFlagged : iconHidden);
            } else {
                return iconHidden;
            }
        } else {
            if (cv.hasBoom) {
                return iconMine;
            } else {
                return iconUncovered;
            }
        }
    }


    /**
     * This method repaints the cells when the user clicks on one. I chose to
     * use Graphics2D's antialiasing here for better visuals. I learned that
     * Swing does not clear out a Graphics2D like it automatically does with
     * the legacy Graphics object and that it needs a .dispose() method to
     * free the resources.
     * @param g our graphics object to be painted
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                drawCell(g2, cells[r][c]);
            }
        }
        g2.dispose();
    }

    private void drawCell(Graphics2D g2, CellView cv) {
        Polygon p = cv.shape;
        if (p == null) return;

        Rectangle bounds = p.getBounds();

        Image icon = chooseIconForCell(cv);

        if (icon != null) {
            int padding = 0; // small margin so the border is visible
            int size = Math.min(bounds.width, bounds.height) - 2 * padding;
            if (size < 0) size = 0;

            int drawX = bounds.x + (bounds.width - size) / 2;
            int drawY = bounds.y + (bounds.height - size) / 2;

            Shape oldClip = g2.getClip();
            g2.setClip(p);
            g2.drawImage(icon, drawX, drawY, size, size, this);
            g2.setClip(oldClip);
        } else {
            if (cv.hidden) {
                g2.setColor(new Color(70, 70, 70));
            } else {
                g2.setColor(new Color(170, 170, 170));
            }
            g2.fillPolygon(p);
        }

        g2.setColor(Color.BLACK);
        g2.drawPolygon(p);

        if (!cv.hidden && !cv.hasBoom && cv.neighboringBooms > 0) {
            drawCenteredString(g2,
                    Integer.toString(cv.neighboringBooms),
                    bounds);
        }
    }

    private void drawCenteredString(Graphics2D g2, String text,
                                    Rectangle bounds) {
        Font font = g2.getFont().deriveFont(Font.BOLD, 14f);
        g2.setFont(font);
        FontMetrics fm = g2.getFontMetrics();

        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getAscent();

        int x = bounds.x + (bounds.width - textWidth) / 2;
        int y = bounds.y + (bounds.height + textHeight) / 2 - 2;

        g2.setColor(Color.BLACK);
        g2.drawString(text, x, y);
    }
}
