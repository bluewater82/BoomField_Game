import javax.swing.*;
import java.awt.*;

/**
 * This class is the JPanel that provides information (mine count, elapsed
 * time, and flags used) and user controls (difficulty selection and starting
 * new game) to the player by use of a JComboBox, JButton, and multiple
 * JLabels. It also handles the timer.
 */
public class InfoPanel extends JPanel {

    private final JLabel timeLabel;
    private final JLabel boomLabel;
    private final JComboBox<String> difficultyBox;
    private final JButton newGameButton;
    private final JLabel flagLabel;

    private final Timer timer;
    private int elapsedSeconds = 0;
    private boolean running = false;


    InfoPanel() {
        setLayout(new GridLayout(1, 4));


        difficultyBox = new JComboBox<>(new String[] { "Easy", "Medium", "Hard" });
        difficultyBox.setSelectedItem("Easy");


        timeLabel = new JLabel("Time: 00:00", SwingConstants.CENTER);
        timeLabel.setFont(timeLabel.getFont().deriveFont(Font.BOLD, 16f));

        boomLabel = new JLabel("Buried Booms: 27",
                SwingConstants.LEADING);
        boomLabel.setFont(timeLabel.getFont().deriveFont(Font.BOLD, 16f));

        flagLabel = new JLabel("Flags Used: 0");
        flagLabel.setFont(timeLabel.getFont().deriveFont(Font.BOLD, 16f));

        newGameButton = new JButton("Start New Game");

        add(difficultyBox);
        add(timeLabel);
        add(boomLabel);
        add(flagLabel);
        add(newGameButton);

        timer = new Timer(1000, e -> {
            elapsedSeconds++;
            updateTimeLabel();
        });
    }

    private void updateTimeLabel() {
        timeLabel.setText("Time: " + formatElapsed(elapsedSeconds));
    }

    /**
     * Updates the JLabel that displays the current number of flags that have
     * been placed on the BoomField.
     * @param usedFlags num of flags used
     */
    public void setFlagLabel(int usedFlags) {
        flagLabel.setText("Flags Used: " + usedFlags);
    }

    private String formatElapsed(int totalSeconds) {
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }


    /**
     * Trigger method for the timer to start
     */
    public void startTimer() {
        if (running) return;
        running = true;
        timer.start();
    }

    /**
     * Trigger method for the timer to stop
     */
    public void stopTimer() {
        if (!running) return;
        running = false;
        timer.stop();
    }

    /**
     * Trigger method to stop and reset the elapsed time to zero
     */
    public void resetTimer() {
        timer.stop();
        running = false;
        elapsedSeconds = 0;
        updateTimeLabel();
    }

    /**
     * Getter for total seconds elapsed at time of event
     * @return ET
     */
    public int getElapsedSeconds() {
        return elapsedSeconds;
    }

    /**
     * Getter for the ET after it has been formatted into mm:ss
     * @return formatted ET
     */
    public String getFormattedElapsedTime() {
        return formatElapsed(elapsedSeconds);
    }


    /**
     * Method used to update the mine count JLabel when a new game has been
     * started.
     * @param count num of mines
     */
    public void setMineCount(int count) {
        boomLabel.setText("Buried Booms: " + count);
    }


    /**
     * Exposes the difficulty combo box to the JFrame
     * @return combo box
     */
    public JComboBox<String> getDifficultyBox() {
        return difficultyBox;
    }

    /**
     * Exposes the JButton for starting a new game to the JFrame
     * @return new game button
     */
    public JButton getNewGameButton() {
        return newGameButton;
    }
}
