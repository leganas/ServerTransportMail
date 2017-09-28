package com.leganas.desktop.WindowController;

import com.leganas.engine.Assets;
import com.leganas.engine.Setting;
import com.leganas.engine.Status;
import com.leganas.engine.UserCard;
import com.leganas.engine.controller.ClientController;
import com.leganas.engine.network.packeges.clientTOserver.ClientMessage;
import com.leganas.engine.network.packeges.serverTOclient.ServerMessage;
import com.leganas.desktop.AdminWindow;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * Created by AndreyLS on 16.02.2017.
 */
public class LoginWindowController implements ClientController.GUIListener {
    private Stage mainStage;

    public LoginWindowController() {
        Assets.clientController = new ClientController("Client");
        Assets.clientController.setGuiListener(this);
    }

    public Stage getMainStage() {
        return mainStage;
    }

    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;
        mainStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                Status.clientStatus = Status.ClientStatus.dispose;
                Assets.clientController.dispose();
            }
        });
    }

    public void clickOk(ActionEvent actionEvent)  throws Exception {
        ClientMessage.RequestAutorization msg = new ClientMessage.RequestAutorization();
        msg.login = login.getText();
        msg.password = pass.getText();
        Assets.clientController.addClientMessageToQuery(msg);
    }

    @FXML
    TextField login;
    @FXML
    TextField pass;

    private void runAdminWindow() throws Exception {
        Application apClient = new AdminWindow();
        apClient.start(new Stage());
        hideMainWindow();
    }

    public void clickCansel(ActionEvent actionEvent)  throws Exception {
        Status.clientStatus = Status.ClientStatus.dispose;
        Assets.clientController.dispose();
        ((Stage)((Node)actionEvent.getSource()).getScene().getWindow()).close();
    }

    /**Скрывает родительское окно*/
    private void hideMainWindow() {
        mainStage.getOwner().hide();
        mainStage.hide();
    }

    @Override
    public void GUIMessage(Object msg){
        if (msg instanceof ServerMessage) {
            if (msg instanceof ServerMessage.ReturnAutorizationStatus) {
                ServerMessage.ReturnAutorizationStatus tempMsg = (ServerMessage.ReturnAutorizationStatus) msg;
                Setting.permission = ((ServerMessage.ReturnAutorizationStatus) msg).permission;
                if (tempMsg.flag) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (Setting.permission == UserCard.Permission.ServerDispether ||
                                        Setting.permission == UserCard.Permission.ServerAdmin)
                                runAdminWindow();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        }
    }
}
