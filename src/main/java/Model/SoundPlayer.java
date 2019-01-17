package Model;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;
import java.nio.file.Paths;

public class SoundPlayer {
    private static SoundPlayer ourInstance = new SoundPlayer();
    private Media sound = null;
    private String file = "message_tone.wav";

    public static SoundPlayer getInstance() {
        return ourInstance;
    }

    private SoundPlayer() {
        String source = new File(FileLoader.getInstance().getPath(file)).toURI().toString();
        sound = new Media(source);
    }

    public void play(){
        MediaPlayer mediaPlayer = new MediaPlayer(sound);
        mediaPlayer.play();
    }
}
