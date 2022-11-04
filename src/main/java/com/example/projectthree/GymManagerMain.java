package com.example.projectthree;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class GymManagerMain extends Application {
    @Override
    public void start(Stage stage){
        FXMLLoader fxmlLoader = new FXMLLoader(GymManagerMain.class.getResource("GymManagerView.fxml"));
        Scene scene;
        Parent root;
        try {
            root = fxmlLoader.load();
            scene = new Scene(root);
            stage.setTitle("Gym Manager");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        } catch (Exception e) {
            System.out.println("fxml file does not exist");
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args){
        launch();
    }
}
