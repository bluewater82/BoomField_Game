/**
 * This is the interface used by the JFrame to retrieve information from the
 * logic manager (BoomFieldLogic)
 */
public interface LogicToGUI {

    void updateCell(int row, int col,
                    boolean hasBoom,
                    boolean flagged,
                    boolean hidden,
                    int neighboringBooms);

    void showGameOver(boolean won);

    void refreshBoard();

    void updateFlagsUsed(int flagsUsed);
}
