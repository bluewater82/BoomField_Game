import javax.swing.*;


/**
 * This class is strictly the main. It is where the JAR looks to execute the
 * game and triggers the JFrame (which triggers the rest of the gui and logic
 * layers).
 */
public class MainLauncher {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            BoomFieldFrame frame = new BoomFieldFrame(15,15,1);
            frame.setVisible(true);
        });
    }
}
