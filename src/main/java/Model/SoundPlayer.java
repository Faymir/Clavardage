package Model;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;
import java.nio.file.Paths;

public class SoundPlayer {
    private static SoundPlayer ourInstance = new SoundPlayer();
    private Media sound = null;
    private Media bellSound = null;
    private String file = "message_tone.wav";
    private String bellFile = "bell.wav";
    public static int MESSAGE_TONE = 0;
    public static int BELL = 1;

    public static SoundPlayer getInstance() {
        return ourInstance;
    }

    private SoundPlayer() {
        String source = new File(FileLoader.getInstance().getPath(file)).toURI().toString();
        sound = new Media(source);
        bellSound = new Media(FileLoader.getInstance().getUri(bellFile).toString());
    }

    public void play(int tone){
        MediaPlayer mediaPlayer = new MediaPlayer(sound);
        if(tone == BELL)
            mediaPlayer = new MediaPlayer(bellSound);

        mediaPlayer.play();
    }
}
