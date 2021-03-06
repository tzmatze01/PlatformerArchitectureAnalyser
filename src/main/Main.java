package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        BorderPane root = FXMLLoader.load(getClass().getResource("/main/resources/Layout.fxml"));

        primaryStage.setTitle("Architecture Analyser");

        Scene scene = new Scene(root, 1600, 1200);
        scene.getStylesheets().add(getClass().getResource("/main/resources/Styling.css").toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.sizeToScene();
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
