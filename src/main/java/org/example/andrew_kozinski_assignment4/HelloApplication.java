package org.example.andrew_kozinski_assignment4;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Main entry point into the program, launches the GUI
 * @author Andrew Kozinski
 */
public class HelloApplication extends Application {
    /**
     * Creates the primary stage object
     * @param stage
     * @throws IOException
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Movie Database");
        stage.setScene(scene);
        stage.show();
    }


    /**
     * Main entry point into the program, launches the program
     * @param args
     */
    public static void main(String[] args) {
        launch();
    }
}