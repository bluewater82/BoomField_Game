import java.util.Random;


/**
 * CS251 - Final Project: Hex Grid Minesweeper
 * <p>Author: Andre DeHerrera</p>
 * <p>This class composes our logic layer. It reuses most of the logic from
 * our previous assignment with additional methods that handle the flows
 * between the logic and gui layers. I have several sanity checks dropped
 * throughout my logic layer that print things like the updated board
 * representation and number of flags used to help me verify that what is
 * being displayed on the running gui match the internal logic.</p>
 */
public class BoomFieldLogic implements GUIToLogic {

    private final int ROWS;
    private final int COLS;
    private final Cell[][] BOARD;
    private final int numberOfBooms;
    private int flagsUsed = 0;
    private final Random boomRandomizer;
    private boolean gameOver = false;

    private final LogicToGUI infoForGUI;   // callback into GUI

    private static class Cell {
        boolean hasBoom;
        boolean flagged;
        boolean hidden = true;
        int neighboringBooms = 0;
    }

    /**
     * This is our main logic constructor. It builds the board with a 2D
     * array of Cells and initializes the board state by planting the
     * specified number of "booms" using random seeds and provides the info
     * for the gui.
     * @param rows number of horizontal cells
     * @param cols number of vertical cells
     * @param booms number of mines to be planted
     * @param infoForGUI information for the gui
     */
    public BoomFieldLogic(int rows, int cols, int booms, LogicToGUI infoForGUI){
        this.ROWS = rows;
        this.COLS = cols;
        this.BOARD = new Cell[rows][cols];
        this.numberOfBooms = booms;
        this.boomRandomizer = new Random();
        this.infoForGUI = infoForGUI;

        initializeBoard();
        plantBooms();
        countNearbyBooms();
        pushFullBoardState();
    }

    /**
     * This method handles the toggling of markers on the board's cells. It
     * first checks to make sure the game isn't over and that the cell being
     * interacted with is valid (!hidden). After passing these checks the
     * method will toggle using a basic state=!state function and updates our
     * number of flags used (with associated sanity check). It then passes
     * the information back to the gui and ultimately checks for the win
     * condition.
     * @param row row of cell selected
     * @param col col of cell selected
     */
    @Override
    public void toggleFlag(int row, int col) {
        if (gameOver) {
            return;
        }
        Cell cell = BOARD[row][col];

        if (!cell.hidden) {
            return;
        }

        cell.flagged = !cell.flagged;
        if (cell.flagged) {
            flagsUsed++;
        }
        if (!cell.flagged) {
            flagsUsed--;
        }
        System.out.println("Flags used: " + flagsUsed); // sanity check

        pushCellState(row, col);
        infoForGUI.updateFlagsUsed(flagsUsed);

        checkWinCondition();
    }


    /**
     * This method handles the logic of uncovering a cell. It first checks to
     * make sure the game isn't over and that a cell is a valid candidate for
     * being uncovered (if it is already uncovered or if it has been flagged
     * it is not eligible for uncovering). If the cell has a mine it triggers
     * the logic to uncover all mines, end the game, and send the appropriate
     * info to the gui. If there is no mine hidden in the cell it will then
     * trigger the floodFillUncover and checkWinCondition methods.
     * @param row row of selected cell
     * @param col col of selected cell
     */
    @Override
    public void uncoverSelectedCell(int row, int col) {
        if (gameOver) {
            return;
        }

        Cell cell = BOARD[row][col];

        if (!cell.hidden || cell.flagged) {
            return;
        }

        if (cell.hasBoom) {
            revealBoomsUponBoom();
            gameOver = true;
            infoForGUI.showGameOver(false);
            infoForGUI.refreshBoard();
        } else {
            floodFillUncover(row, col);
            checkWinCondition();
        }

        System.out.println(this);
    }



    private void initializeBoard() {
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                BOARD[r][c] = new Cell();
            }
        }
    }

    private void plantBooms() {
        int plantedBooms = 0;
        while (plantedBooms < numberOfBooms) {
            int row = boomRandomizer.nextInt(ROWS);
            int col = boomRandomizer.nextInt(COLS);

            if (!BOARD[row][col].hasBoom) {
                BOARD[row][col].hasBoom = true;
                plantedBooms++;
            }
        }
    }

    private boolean inBounds(int row, int col) {
        return row >= 0 && row < ROWS && col >= 0 && col < COLS;
    }



    private void checkWinCondition() {
        if (gameOver) {
            return;
        }

        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                Cell checkedCell = BOARD[r][c];

                if (checkedCell.hasBoom) {
                    // All booms must be flagged
                    if (!checkedCell.flagged) {
                        return;
                    }
                } else {
                    // All non-booms must be uncovered
                    if (checkedCell.hidden) {
                        return;
                    }
                }
            }
        }
        gameOver = true;
        infoForGUI.showGameOver(true);
        infoForGUI.refreshBoard();
    }

    private int[][] getNeighbors(int row, int col) {

        final int[][] FOR_EVENS = {
                {-1, 0}, {-1, -1},
                { 0,-1}, { 0, 1},
                { 1, 0}, { 1,-1}
        };
        final int[][] FOR_ODDS = {
                {-1, 0}, {-1, 1},
                { 0,-1}, { 0, 1},
                { 1, 0}, { 1, 1}
        };

        int[][] deltas = (row % 2 == 0) ? FOR_EVENS : FOR_ODDS;

        int[][] tmp = new int[6][2];
        int count = 0;
        for (int[] d : deltas) {
            int rowNeighbor = row + d[0];
            int colNeighbor = col + d[1];
            if (inBounds(rowNeighbor, colNeighbor)) {
                tmp[count][0] = rowNeighbor;
                tmp[count][1] = colNeighbor;
                count++;
            }
        }

        int[][] result = new int[count][2];
        System.arraycopy(tmp, 0, result, 0, count);
        return result;
    }

    private void revealBoomsUponBoom() {
        gameOver = true;

        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                Cell cell = BOARD[r][c];
                if (cell.hasBoom) {
                    cell.hidden = false;
                    pushCellState(r, c);
                }
            }
        }
    }

    private void floodFillUncover(int row, int col) {
        Cell cell = BOARD[row][col];

        if (cell.hasBoom || !cell.hidden || cell.flagged) {
            return;
        }

        cell.hidden = false;
        pushCellState(row, col);

        if (cell.neighboringBooms != 0) {
            return;
        }

        for (int[] nbr : getNeighbors(row, col)) {
            int nr = nbr[0], nc = nbr[1];
            floodFillUncover(nr, nc);
        }
    }

    private void countNearbyBooms() {
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                if (BOARD[r][c].hasBoom) {
                    BOARD[r][c].neighboringBooms = -1;
                } else {
                    int count = 0;
                    for (int[] nbr : getNeighbors(r, c)) {
                        int nr = nbr[0], nc = nbr[1];
                        if (BOARD[nr][nc].hasBoom) {
                            count++;
                        }
                    }
                    BOARD[r][c].neighboringBooms = count;
                }
            }
        }
    }



    private void pushCellState(int row, int col) {
        Cell cell = BOARD[row][col];
        infoForGUI.updateCell(row, col,
                cell.hasBoom,
                cell.flagged,
                cell.hidden,
                cell.neighboringBooms);
    }

    private void pushFullBoardState() {
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                pushCellState(r, c);
            }
        }
        infoForGUI.refreshBoard();
    }


    /**
     * Method to construct our string representation of the current game
     * state. This is a relic from the previous assignment and is kept as
     * another sanity check while running the program to make sure the gui is
     * behaving. It is purely for console display; the game will run fine
     * without it.
     * @return string sanity check
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (int r = 0; r < ROWS; r++) {
            if (r % 2 == 1) {
                sb.append(" ");
            }

            for (int c = 0; c < COLS; c++) {
                Cell cell = BOARD[r][c];
                char letter;

                if (cell.hidden) {
                    if (cell.flagged) {
                        letter = 'F';
                    } else {
                        letter = 'H';
                    }
                } else {
                    if (cell.hasBoom) {
                        letter = '*';
                    } else if (cell.neighboringBooms == 0) {
                        letter = 'U';
                    } else {
                        letter = (char) ('0' + cell.neighboringBooms);
                    }
                }

                sb.append(letter).append(' ');
            }
            sb.append('\n');
        }
        return sb.toString();
    }

}
