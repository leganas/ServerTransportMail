package com.leganas.desktop;/**
 * Created by AndreyLS on 15.02.2017.
 */

import com.leganas.desktop.WindowController.MainMenuWindowController;
import com.leganas.test.TestClass;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;

public class MainMenuWindow extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("../resources/fxml/main_menu.fxml"));
        Parent root = fxmlLoader.load();
        MainMenuWindowController mainMenuWindowController = fxmlLoader.getController();
        mainMenuWindowController.setMainStage(primaryStage);

        primaryStage.setTitle("Главное меню системы управления транспортной логистикой");
        primaryStage.setMinWidth(600);
        primaryStage.setMinHeight(280);
        primaryStage.setResizable(false);
        primaryStage.setScene(new Scene(root, 600, 280));
        primaryStage.show();

        TestClass testClass = new TestClass();
    }

}
