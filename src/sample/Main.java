package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Main extends Application {
    Stage window;
    Button startBtn;
    Button btn1;
    Button btn2;
    Button btn3;
    Button againBtn;
    Label startLabel;
    Label wordLabel;
    Label scoreLabel;
    Label endLabel;
    Label hiscoreLabel;
    Scene gameScene;
    Scene endScene;
    Scene scene;
    int score;
    int hiscore;
    ArrayList<String> wordBank = new ArrayList<>();

    Timer timer;
    int time;

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load words from file
        Scanner scan = new Scanner(new File("src/sample/wordBank.txt"));
        while (scan.hasNext()) {
            wordBank.add(scan.next());
        }

        // Load hiscore
        Scanner scan2 = new Scanner(new File("src/sample/hiscore.txt"));
        hiscore = scan2.nextInt();

        // Title
        window = primaryStage;
        window.setTitle("Speed");

        // Labels
        startLabel = new Label("You have 1 second to pick the matching word.");
        wordLabel = new Label("Hello");
        scoreLabel = new Label("Score: 0");
        endLabel = new Label("You Lost...");
        hiscoreLabel = new Label("Hiscore: " + hiscore);

        // Button to start game
        startBtn = new Button("Start!");
        startBtn.setOnAction(e -> {
            window.setScene(gameScene);
            timer = new Timer();
            timer.schedule(
                new TimerTask() {

                    @Override
                    public void run() {
                        if (time >= 1) {
                            if (score > hiscore) {
                                hiscore = score;
                                hiscoreLabel.setText("Hiscore: " + hiscore);
                                try {
                                    FileWriter writer = new FileWriter("src/sample/hiscore.txt");
                                    writer.write(hiscore + "");
                                    writer.close();
                                } catch (IOException ioException) {
                                    ioException.printStackTrace();
                                }
                            }
                            Platform.runLater(() -> window.setScene(endScene));
                            timer.cancel();
                        }
                        time++;
                    }
                }, 0, 1000
            );
        });

        // Bottom row buttons
        btn1 = new Button("Hello");
        btn2 = new Button("Banana");
        btn3 = new Button("You'll pay");
        randomizeWord(wordLabel);
        btnAction(btn1);
        btnAction(btn2);
        btnAction(btn3);

        // Again btn
        againBtn = new Button("Play again?");
        againBtn.setOnAction(e -> {
            window.setScene(scene);
            randomizeWord(wordLabel);
            time = 0;
            score = 0;
            scoreLabel.setText("Score: " + score);
        });

        // Hbox holds the 3 buttons
        HBox hbox = new HBox(8);
        hbox.setPadding(new Insets(10, 10, 25, 10));
        hbox.setAlignment(Pos.BOTTOM_CENTER);
        hbox.getChildren().addAll(btn1, btn2, btn3);

        // Scene 2 the game
        StackPane gamePane = new StackPane();
        gameScene = new Scene(gamePane, 350, 75);
        gamePane.getChildren().addAll(wordLabel, hbox, scoreLabel);
        StackPane.setAlignment(hbox, Pos.BOTTOM_CENTER);
        StackPane.setAlignment(scoreLabel, Pos.CENTER_LEFT);
        StackPane.setAlignment(wordLabel, Pos.TOP_LEFT);

        /*
        TODO: Make score appear in game and after game, adding same node to different
         pane does not work well
         */
        // Scene 3 end game
        StackPane endPane = new StackPane();
        endScene = new Scene(endPane, 350, 75);
        endPane.getChildren().addAll(endLabel, scoreLabel, hiscoreLabel, againBtn);
        StackPane.setAlignment(endLabel, Pos.CENTER_LEFT);
        StackPane.setAlignment(scoreLabel, Pos.CENTER);
        StackPane.setAlignment(hiscoreLabel, Pos.BOTTOM_CENTER);
        StackPane.setAlignment(againBtn, Pos.BOTTOM_RIGHT);

        // Layout start menu
        StackPane layout = new StackPane();
        layout.getChildren().addAll(startBtn, startLabel);
        StackPane.setAlignment(startLabel, Pos.TOP_LEFT);
        scene = new Scene(layout, 350, 75);
        window.setScene(scene);
        window.show();
    }

    private void btnAction(Button btn) {
        btn.setOnAction(e -> {
            if (btn.getText().equals(wordLabel.getText())) {
                score++;
                time = 0;
                randomizeWord(wordLabel);
                scoreLabel.setText("Score: " + score);
            } else {
                window.setScene(endScene);
            }
        });
    }

    // Randomizes the word at the top of the game
    private void randomizeWord(Label word) {
        Random random = new Random();
        int randInt = random.nextInt(wordBank.size());
        String randomWord = wordBank.get(randInt);
        word.setText(randomWord);
        randomizeButtons(randomWord);
    }

    // Randomizes the buttons, ensures that at least 1 button is the correct answer
    private void randomizeButtons(String randomWord) {
        Random random = new Random();
        // +1 so that randInt matches the button number
        int randInt = random.nextInt(3) + 1;
        if (randInt == 1) {
            btn1.setText(randomWord);
            randomizeOtherButtons(btn2, btn3, wordBank);
        } else if (randInt == 2) {
            btn2.setText(randomWord);
            randomizeOtherButtons(btn1, btn3, wordBank);
        } else {
            btn3.setText(randomWord);
            randomizeOtherButtons(btn1, btn2, wordBank);
        }
    }

    // Randomizes the other buttons. Meant for use after randomizeButtons method
    private void randomizeOtherButtons(Button other1, Button other2, ArrayList<String> wordBank) {
        Random random = new Random();
        int randInt = random.nextInt(wordBank.size());
        String randomWord = wordBank.get(randInt);
        other1.setText(randomWord);
        randInt = random.nextInt(wordBank.size());
        randomWord = wordBank.get(randInt);
        other2.setText(randomWord);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
