import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.util.Objects;
import javax.sound.sampled.FloatControl;


/**
 * This is our handler for the various sound effects used by the game. I had
 * read that enums are better for short sounds that tend to be
 * rapidly-deployed as there is less overhead involved so I decided it would
 * be better architecture to separate the sound effects from the music player
 * that is constantly looping the background music. I located multiple sound
 * clips from online sources (credits listed in full in README.MD file). I
 * had to convert them down to a 16-bit depth using an online converter in
 * order for Java to be able to use them. I did have to research and include
 * a gain adjuster because my explosion sound could not be heard very well
 * over the background music. The rest of the clips were already balanced so
 * I did not have to adjust them, although they do have the FloatControl
 * option implemented in case they need to be adjusted in the future.
 */
public enum SoundFX {
    DIGGING("/audio/digging.wav", +0f),
    FLAG("/audio/flag_toggle.wav", +0f),
    WIN("/audio/win_jingle.wav", +0f),
    CLICK("/audio/menu_click.wav", +0f),
    BOOM("/audio/explosion.wav", +6f);


    private Clip clip;
    private float volume;

    SoundFX(String path, float volume) {
        try {
            var url = Objects.requireNonNull(
                    SoundFX.class.getResource(path),
                    "Sound resource not found: " + path
            );

            AudioInputStream audio = AudioSystem.getAudioInputStream(url);
            clip = AudioSystem.getClip();
            clip.open(audio);

            if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                FloatControl gain = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                gain.setValue(volume);
            }
            System.out.println("Loaded sound: " + path);
        } catch (Exception e) {
            System.err.println("Failed to load sound: " + path);
            e.printStackTrace();
            clip = null;
        }
    }

    /**
     * This is the method used to play each sound effect. It is used by both
     * the JFrame and BoardPanel when the user interacts with the difficulty
     * combo box, new game JButton, and cells. It is also used (by the JFrame
     * class) when the game ends and the JOptionPane is displayed.
     */
    public void play() {
        System.out.println("Playing sound: " + this.name());
        if (clip == null) return;

        if (clip.isRunning()) {
            clip.stop();
        }
        clip.setFramePosition(0);
        clip.start();
    }
}
