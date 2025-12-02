import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;


/**
 * This class is a JPanel class that I am using to load up my custom
 * background image. It uses a BufferedImage that reads the image from the
 * resource directory and then paints the panel with the image.
 */
public class BackgroundPanel extends JPanel {

    private BufferedImage backgroundImage;

    /**
     * This constructs the background panel as makes it opaque.
     */
    public BackgroundPanel() {
        try {
            backgroundImage = ImageIO.read(
                    getClass().getResourceAsStream("/BOOMFIELD.png")
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
        setOpaque(true);
    }

    /**
     * This method paints the buffered image onto the panel. It takes the
     * standard/legacy Graphics and recasts it as a Graphics2D for more
     * flexibility.
     * @param g the object being recast and painted
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();
        int w = getWidth();
        int h = getHeight();

        if (backgroundImage != null) {
            g2.drawImage(backgroundImage, 0, 0, w, h, this);
        }
        g2.dispose();
    }
}
