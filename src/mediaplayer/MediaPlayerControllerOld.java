package mediaplayer;

import java.io.File;
import java.io.FilenameFilter;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.util.Duration;

public class MediaPlayerControllerOld implements Initializable {

    private String path;

    private MediaPlayer mediaPlayer;

    @FXML
    private MediaView mediaView;

    @FXML
    private Button openFile;

    @FXML
    private Slider volumeSlider;

    @FXML
    private Slider progressBar;

    @FXML
    private Label playTime;

    @FXML
    private Label fileTitle;

    @FXML
    private StackPane pane;

    private Duration duration;

    File file;

    Map<Integer, String> fileMap = new HashMap<Integer, String>();

    int currentFileNumber;

    @FXML
    private void OpenFileMethod(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();

        if (file != null) {
            File existDirectory = file.getParentFile();
            fileChooser.setInitialDirectory(existDirectory);
        }
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("MP4 files (*.mp4)", "*.mp4");
        fileChooser.getExtensionFilters().add(extFilter);

        file = fileChooser.showOpenDialog(null);
        if (file.exists()) {
            String currentFileName = file.getName();
            String currentFileAbsolutePath = file.getAbsolutePath();
            currentFileNumber = getFileNumber(currentFileName);
            File parentFile = file.getParentFile();
            FilenameFilter fileNameFilter = new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    if (name.lastIndexOf('.') > 0) {
                        int lastIndex = name.lastIndexOf('.');
                        String str = name.substring(lastIndex);
                        if (str.equals(".mp4")) {
                            return true;
                        }
                    }
                    return false;
                }
            };
            if (parentFile.isDirectory()) {
                File[] listFiles = parentFile.listFiles(fileNameFilter);
                for (File file : listFiles) {
                    int fileNumber = getFileNumber(file.getName());
                    fileMap.put(fileNumber, file.getAbsolutePath());
                }
            }

        }

        path = file.toURI().toString();
        if (path != null) {
            Media media = new Media(path);
            if (mediaPlayer != null) {
                if (mediaPlayer.getStatus() != Status.STOPPED) {
                    mediaPlayer.stop();

                }
            }
            mediaPlayer = new MediaPlayer(media);
            String source = mediaPlayer.getMedia().getSource();
            // source = source.substring(0, source.length() - ".mp4".length());
            source = source.substring(source.lastIndexOf("/") + 1).replaceAll("%20", " ");
            fileTitle.setText(source);
            // fileTitle.setText(path);

            mediaView.setMediaPlayer(mediaPlayer);

            // creating bindings
            DoubleProperty widthProp = mediaView.fitWidthProperty();
            DoubleProperty heightProp = mediaView.fitHeightProperty();

            widthProp.bind(Bindings.selectDouble(mediaView.sceneProperty(), "width"));
            heightProp.bind(Bindings.selectDouble(mediaView.sceneProperty(), "height"));

            volumeSlider.setValue(mediaPlayer.getVolume() * 100);
            volumeSlider.valueProperty().addListener(new InvalidationListener() {
                @Override
                public void invalidated(Observable observable) {
                    mediaPlayer.setVolume(volumeSlider.getValue() / 100);
                }
            });

            mediaPlayer.currentTimeProperty().addListener(new ChangeListener<javafx.util.Duration>() {
                @Override
                public void changed(ObservableValue<? extends javafx.util.Duration> observable,
                        javafx.util.Duration oldValue, javafx.util.Duration newValue) {
                    progressBar.setValue(newValue.toSeconds());
                }
            });

            mediaPlayer.currentTimeProperty().addListener(new InvalidationListener() {
                public void invalidated(Observable ov) {
                    updateValues();
                }
            });

            progressBar.setOnMousePressed(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    mediaPlayer.seek(javafx.util.Duration.seconds(progressBar.getValue()));
                }
            });

            progressBar.setOnMouseDragged(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    mediaPlayer.seek(javafx.util.Duration.seconds(progressBar.getValue()));
                }
            });

            mediaPlayer.setOnEndOfMedia(new Runnable() {
                public void run() {
                    System.out.println("Load new file here");

                }
            });

            mediaPlayer.setOnReady(new Runnable() {
                @Override
                public void run() {
                    javafx.util.Duration total = media.getDuration();
                    progressBar.setMax(total.toSeconds());
                    duration = mediaPlayer.getMedia().getDuration();
                }
            });

            mediaPlayer.play();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    public void pauseVideo(ActionEvent event) {
        mediaPlayer.pause();
    }

    public void stopVideo(ActionEvent event) {
        mediaPlayer.stop();
    }

    public void playVideo(ActionEvent event) {
        mediaPlayer.play();
        mediaPlayer.setRate(1);
    }

    public void skip5(ActionEvent event) {
        mediaPlayer.seek(mediaPlayer.getCurrentTime().add(javafx.util.Duration.seconds(5)));
    }

    public void furtherSpeedUpVideo(ActionEvent event) {
        mediaPlayer.setRate(mediaPlayer.getRate() + 0.05);
    }

    public void back5(ActionEvent event) {
        mediaPlayer.seek(mediaPlayer.getCurrentTime().add(javafx.util.Duration.seconds(-5)));
    }

    public void furtherSlowDownVideo(ActionEvent event) {

        mediaPlayer.setRate(mediaPlayer.getRate() - 0.05);

    }

    protected void updateValues() {
        if (playTime != null && progressBar != null && volumeSlider != null) {
            Platform.runLater(new Runnable() {
                public void run() {
                    Duration currentTime = mediaPlayer.getCurrentTime();
                    playTime.setText(formatTime(currentTime, duration));
                    progressBar.setDisable(duration.isUnknown());
                    /*
                     * if (!progressBar.isDisabled() && duration.greaterThan(Duration.ZERO) &&
                     * !progressBar.isValueChanging()) {
                     * progressBar.setValue(currentTime.divide(duration).toMillis() * 100.0); }
                     */
                    if (!volumeSlider.isValueChanging()) {
                        volumeSlider.setValue((int) Math.round(mediaPlayer.getVolume() * 100));
                    }
                }
            });
        }

    }

    private static String formatTime(Duration elapsed, Duration duration) {
        int intElapsed = (int) Math.floor(elapsed.toSeconds());
        int elapsedHours = intElapsed / (60 * 60);
        if (elapsedHours > 0) {
            intElapsed -= elapsedHours * 60 * 60;
        }
        int elapsedMinutes = intElapsed / 60;
        int elapsedSeconds = intElapsed - elapsedHours * 60 * 60 - elapsedMinutes * 60;

        if (duration.greaterThan(Duration.ZERO)) {
            int intDuration = (int) Math.floor(duration.toSeconds());
            int durationHours = intDuration / (60 * 60);
            if (durationHours > 0) {
                intDuration -= durationHours * 60 * 60;
            }
            int durationMinutes = intDuration / 60;
            int durationSeconds = intDuration - durationHours * 60 * 60 - durationMinutes * 60;
            if (durationHours > 0) {
                return String.format("%d:%02d:%02d/%d:%02d:%02d", elapsedHours, elapsedMinutes, elapsedSeconds,
                        durationHours, durationMinutes, durationSeconds);
            } else {
                return String.format("%02d:%02d/%02d:%02d", elapsedMinutes, elapsedSeconds, durationMinutes,
                        durationSeconds);
            }
        } else {
            if (elapsedHours > 0) {
                return String.format("%d:%02d:%02d", elapsedHours, elapsedMinutes, elapsedSeconds);
            } else {
                return String.format("%02d:%02d", elapsedMinutes, elapsedSeconds);
            }
        }
    }

    private int getFileNumber(String fileName) {
        int fileNumber = 0;
        String[] fileNameSplits = fileName.split(" ");
        if (fileNameSplits.length > 0) {
            String s = fileNameSplits[0];
            s = s.replaceFirst("^0+(?!$)", "");
            fileNumber = Integer.parseInt(s);
        }
        return fileNumber;
    }
}
