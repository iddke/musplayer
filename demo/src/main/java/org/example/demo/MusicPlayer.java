package org.example.demo;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;

public class MusicPlayer extends Application {

    private MediaPlayer mediaPlayer;

    @Override
    public void start(Stage primaryStage) {
        Button openButton = new Button("Выбрать трек");
        Button playButton = new Button("Играть");
        Button stopButton = new Button("Стоп");
        Slider volumeSlider = new Slider(0, 1, 0.5);
        Slider startTimeSlider = new Slider();
        Slider endTimeSlider = new Slider();
        Label startTimeLabel = new Label("Start Time: 0:00");
        Label endTimeLabel = new Label("End Time: 0:00");
        Label volumeLabel = new Label("Volume:");

        openButton.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Music File");
            File file = fileChooser.showOpenDialog(primaryStage);
            if (file != null) {
                Media media = new Media(file.toURI().toString());
                mediaPlayer = new MediaPlayer(media);

                mediaPlayer.setOnReady(() -> {
                    Duration duration = mediaPlayer.getMedia().getDuration();
                    startTimeSlider.setMax(duration.toSeconds());
                    endTimeSlider.setMax(duration.toSeconds());
                    endTimeSlider.setValue(duration.toSeconds());
                });

                mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
                    if (!startTimeSlider.isValueChanging() && !endTimeSlider.isValueChanging()) {
                        startTimeSlider.setValue(newValue.toSeconds());
                    }
                });

                mediaPlayer.setOnEndOfMedia(() -> mediaPlayer.stop());

                playButton.setOnAction(e -> {
                    mediaPlayer.setVolume(volumeSlider.getValue());
                    mediaPlayer.play();
                });

                stopButton.setOnAction(e -> mediaPlayer.stop());

                startTimeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
                    if (!endTimeSlider.isValueChanging()) {
                        mediaPlayer.seek(Duration.seconds(newValue.doubleValue()));
                    }
                    startTimeLabel.setText("Start Time: " + formatTime(newValue));
                });

                endTimeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
                    if (!startTimeSlider.isValueChanging()) {
                        mediaPlayer.setStopTime(Duration.seconds(newValue.doubleValue()));
                    }
                    endTimeLabel.setText("End Time: " + formatTime(newValue));
                });
            }
        });

        volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (mediaPlayer != null) {
                mediaPlayer.setVolume(newValue.doubleValue());
            }
        });

        HBox buttonsBox = new HBox(10, openButton, playButton, stopButton);
        HBox volumeBox = new HBox(10, volumeLabel, volumeSlider);
        VBox slidersBox = new VBox(10, startTimeSlider, startTimeLabel, endTimeSlider, endTimeLabel);

        VBox root = new VBox(10, buttonsBox, volumeBox, slidersBox);
        Scene scene = new Scene(root, 300, 300);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Music Player");
        primaryStage.show();
    }

    private String formatTime(Number seconds) {
        int totalSeconds = (int) Math.floor(seconds.doubleValue());
        int minutes = totalSeconds / 60;
        int remainderSeconds = totalSeconds % 60;
        return String.format("%d:%02d", minutes, remainderSeconds);
    }

    public static void main(String[] args) {
        launch(args);
    }
}


