package com.leganas.desktop.WindowController;

import com.leganas.engine.NetClientCard;
import com.leganas.engine.Setting;
import com.leganas.engine.UserCard;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by AndreyLS on 10.02.2017.
 */
public class AdminWindowController implements Initializable{
    private Stage mainStage;

    public Stage getMainStage() {
        return mainStage;
    }

    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public void clickBT(ActionEvent actionEvent) {
        Object bt = actionEvent.getSource();

        if (!(bt instanceof Button)) {return;} // если событие вызвала не кнопка то нахуй

/*        Node nod = (Node) actionEvent.getSource();
        Stage stage = (Stage) nod.getScene().getWindow();
        stage.setTitle(((Button) bt).getText());*/

        ((Button) bt).setText("Click");

    }
    boolean ready;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    public void clickMenuAcountManager(ActionEvent actionEvent) throws IOException {
        if (Setting.permission == UserCard.Permission.ServerDispether ||
                Setting.permission == UserCard.Permission.ServerAdmin) {
            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("../../resources/fxml/acount_manager.fxml"));
            Parent root = fxmlLoader.load();
//            ListView<NetClientCard> listView = (ListView<NetClientCard>) root.lookup("#listView_admin");
            AccountWindowController accountWindowController = fxmlLoader.getController();
            accountWindowController.setMainStage(stage);
            stage.setTitle("Управление акуантами");
            stage.setResizable(false);
            stage.setScene(new Scene(root));
            accountWindowController.initAllList();


            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(mainStage);
            stage.show();


        }
    }
}
