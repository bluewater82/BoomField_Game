import javax.swing.*;
import java.awt.*;

/**
 * CS251 - Final Project
 * This is the JFrame class and is quite extensive. It provides the main
 * window for the game and ties in the different panels for our user.
 */
public class BoomFieldFrame extends JFrame implements LogicToGUI {

    private final int rows;
    private final int cols;
    private int booms;
    private final InfoPanel infoPanel;
    private final BoardPanel boardPanel;
    private BoomFieldLogic logic;

    /**
     * This is the main constructor for the frame. It initializes all of our
     * panels, logic, artwork, and music.
     * @param rows rows of cells
     * @param cols cols of cells
     * @param booms mines hidden
     */
    public BoomFieldFrame(int rows, int cols, int booms) {
        this.rows = rows;
        this.cols = cols;
        this.booms = booms;

        infoPanel = new InfoPanel();
        boardPanel = new BoardPanel(rows, cols);

        MusicPlayer music = new MusicPlayer();
        music.playLoop("/audio/space-ambient-cinematic-442834.wav");

        String initialDiff =
                (String) infoPanel.getDifficultyBox().getSelectedItem();
        this.booms = computeBoomsForDifficulty(initialDiff);

        infoPanel.setMineCount(this.booms);

        logic = new BoomFieldLogic(rows, cols, this.booms, this);
        boardPanel.setLogic(logic);

        boardPanel.setFirstClickCallback(() -> infoPanel.startTimer());

        infoPanel.getNewGameButton().addActionListener(e -> {
                    SoundFX.CLICK.play();
                    startNewGame();
                });

        infoPanel.getDifficultyBox().addActionListener(e -> {
            String diff = (String) infoPanel.getDifficultyBox().getSelectedItem();
            SoundFX.CLICK.play();
            this.booms = computeBoomsForDifficulty(diff);

        });

        setLayout(new BorderLayout());
        add(infoPanel, BorderLayout.NORTH);
        add(boardPanel, BorderLayout.CENTER);

        BackgroundPanel bg = new BackgroundPanel();
        bg.setLayout(new BorderLayout());
        bg.add(infoPanel, BorderLayout.NORTH);
        bg.add(boardPanel, BorderLayout.CENTER);

        setContentPane(bg);
        setTitle("BoomField - Watch Your Step!");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setResizable(false);
        setSize(960,650);

    }

    private int computeBoomsForDifficulty(String difficulty) {
        int totalCells = rows * cols;

        if ("Easy".equals(difficulty)) {
            return 27;
        } else if ("Hard".equals(difficulty)) {
            return 64;
        } else {
            return 43;
        }
    }


    /**
     * Method used to update the number of flags that have been placed on the
     * game board.
     * @param flags number of flags used
     */
    @Override
    public void updateFlagsUsed(int flags) {
        infoPanel.setFlagLabel(flags);
    }

    /**
     * Method used to update board cells as the game is played
     * @param row row of cell
     * @param col col of cell
     * @param hasBoom mine state
     * @param flagged flagged state
     * @param hidden hidden state
     * @param neighboringBooms neighboring mines
     */
    @Override
    public void updateCell(int row, int col,
                           boolean hasBoom,
                           boolean flagged,
                           boolean hidden,
                           int neighboringBooms) {
        boardPanel.updateCellView(row, col, hasBoom, flagged, hidden,
                neighboringBooms);
    }

    /**
     * This method displays our JOptionPane that comes up when the game is
     * over. It detects if the result was a win or loss and displays the
     * appropriate message along with the time elapsed. For extra flavor I
     * have included some sounds to accompany the messages.
     * @param won if game ended due to successfully marking the field
     */
    @Override
    public void showGameOver(boolean won) {
        infoPanel.stopTimer();

        String timeStr = infoPanel.getFormattedElapsedTime();
        String title = won ? "You Win!" : "Game Over";
        String message = won
                ? "You cleared the field in " + timeStr + "."
                : "Boom! You lasted " + timeStr + ".";

        if (won) {
            SoundFX.WIN.play();
        } else {
            SoundFX.BOOM.play();
        }
        JOptionPane.showMessageDialog(
                this,
                message,
                title,
                won ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE
        );
    }

    /**
     * Overrides the repainting method of the same name located in the
     * BoardPanel class.
     */
    @Override
    public void refreshBoard() {
        boardPanel.refreshBoard();
    }


    /**
     * This is the method that controls subsequent new games. It sets a new
     * mine seed/count based on difficulty selected, updates the displayed
     * mine count, resets the timer, and refreshes the logic and board.
     */
    public void startNewGame() {
        infoPanel.resetTimer();
        infoPanel.setMineCount(this.booms);
        boardPanel.resetInteractionState();

        // Recalculate booms based on current difficulty selection
        String diff = (String) infoPanel.getDifficultyBox().getSelectedItem();
        this.booms = computeBoomsForDifficulty(diff);

        // Rebuild logic and rewire
        logic = new BoomFieldLogic(rows, cols, this.booms, this);
        boardPanel.setLogic(logic);
        boardPanel.refreshBoard();
    }


    /**
     * Getter for the elapsed time
     * @return elapsed time in seconds
     */
    public int getElapsedSeconds() {
        return infoPanel.getElapsedSeconds();
    }


    /**
     * Getter for the elapsed time after mm:ss formatting
     * @return formatted ET
     */
    public String getElapsedTimeString() {
        return infoPanel.getFormattedElapsedTime();
    }
}
