import javax.sound.sampled.*;
import java.io.IOException;
import java.io.InputStream;


/**
 * This class is used only for the looping background music. It uses Java's
 * Sound and InputStream libraries.
 */
public class MusicPlayer {

    private Clip clip;

    /**
     * This method handles the playback loop for our background music. It
     * pulls the audio as a resource via InputStream and then samples and
     * clips the audio in order to run it in a continuous loop.
     * @param resourcePath path to the music file
     */
    public void playLoop(String resourcePath) {
        try {
            if (clip != null && clip.isRunning()) {
                clip.stop();
            }

            InputStream audioSrc = getClass().getResourceAsStream(resourcePath);
            if (audioSrc == null) {
                System.err.println("Audio file not found: " + resourcePath);
                return;
            }

            // Needed because AudioSystem can't read compressed streams directly
            InputStream bufferedIn = new java.io.BufferedInputStream(audioSrc);
            AudioInputStream ais = AudioSystem.getAudioInputStream(bufferedIn);

            clip = AudioSystem.getClip();
            clip.open(ais);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            clip.start();

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }
}
