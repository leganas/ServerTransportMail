package com.leganas.desktop.WindowController;

import com.leganas.engine.Assets;
import com.leganas.desktop.ServerServiceWindow;
import com.leganas.engine.Setting;
import com.leganas.engine.Status;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;


import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


/**
 * Created by AndreyLS on 15.02.2017.
 */
public class MainMenuWindowController implements Initializable{

    private Stage mainStage;

    public Stage getMainStage() {
        return mainStage;
    }

    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;
        mainStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                if ( Assets.clientController != null)  Assets.clientController.dispose();
                if (Assets.serverController != null) {
                    Status.statusServer = Status.StatusServer.dispose;
                }
            }
        });
    }

    @FXML
    public TextField ip_adress_tf;

    public void clickServer(ActionEvent actionEvent) throws Exception {
        Setting.programType = Setting.ProgramType.Server;
        startServer(actionEvent);
        hideMainWindow(actionEvent);
    }

    public void startServer(ActionEvent actionEvent) throws Exception {
        if (Status.statusServer == Status.StatusServer.off) {
            Application apServer = new ServerServiceWindow();
            apServer.start(new Stage());
        }
    }

    public void clickClientServer(ActionEvent actionEvent) throws Exception {
        Setting.programType = Setting.ProgramType.CientServer;
        Setting.IPAdressFromClient = "localhost";
        startServer(actionEvent);
        getLoginPasswordWindow(actionEvent);
    }

    public void clickClient(ActionEvent actionEvent) throws Exception {
        Setting.programType = Setting.ProgramType.Client;
        Setting.IPAdressFromClient = ip_adress_tf.getText();
        getLoginPasswordWindow(actionEvent);
        // hideMainWindow(actionEvent);
    }

    /**Скрывает родительское окно*/
    private void hideMainWindow(ActionEvent actionEvent) {
        ((Stage)((Node)actionEvent.getSource()).getScene().getWindow()).hide();
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ip_adress_tf.setText(Setting.IPAdressFromClient);
    }

    public void getLoginPasswordWindow(ActionEvent actionEvent) throws IOException {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("../../resources/fxml/loging.fxml"));
        Parent root = fxmlLoader.load();
        LoginWindowController loginWindowController = fxmlLoader.getController();

        loginWindowController.setMainStage(stage);
        stage.setTitle("Введите логин и пароль");
        stage.setMinWidth(300);
        stage.setMinHeight(150);
        stage.setResizable(false);
        stage.setScene(new Scene(root));

        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(mainStage);
        stage.show();
    }
}
