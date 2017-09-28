package com.leganas.desktop.WindowController;

import com.leganas.engine.Assets;
import com.leganas.engine.NetClientCard;
import com.leganas.engine.UserCard;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import static com.leganas.desktop.WindowController.ListViewUtil.ArrayListToObservableList;

/**
 * Created by AndreyLS on 16.02.2017.
 */
public class AccountWindowController  implements Initializable {
    private Stage mainStage;

    public Stage getMainStage() {
        return mainStage;
    }

    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;
    }

    /**Сохранить изенения в users.json*/
    public void bt_save(ActionEvent actionEvent) {

    }
    @FXML
    Button driver_add;
    @FXML
    ListView<NetClientCard> listView_driver;
    ObservableList<NetClientCard> listDriver;
    @FXML
    Button dispether_add;
    @FXML
    ListView<NetClientCard> listView_dispetsherr;
    ObservableList<NetClientCard> listDispether;
    @FXML
    Button admin_add;
    @FXML
    ListView<NetClientCard> listView_admin;
    ObservableList<NetClientCard> listAdmin;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }


    public void initListAdmin(){
        if (listAdmin != null) listAdmin.clear();
        ArrayList<NetClientCard> temp = new ArrayList<>();
        for (int i=0;i<Assets.workData.getOnlineDriver().size();i++){
            if (Assets.workData.getOnlineDriver().get(i).getPermission() == UserCard.Permission.ServerAdmin) {
                temp.add(Assets.workData.getOnlineDriver().get(i));
            }
        }
        listAdmin = ArrayListToObservableList(temp);
//        listView_admin = (ListView<NetClientCard>) root.lookup("#listView_admin");
        if (listAdmin != null) ListViewUtil.initListViewFromNetClientCard(listView_admin,listAdmin);
    };

    public void initListDispether(){
        if (listDispether != null) listDispether.clear();
        ArrayList<NetClientCard> temp = new ArrayList<>();
        for (int i=0;i<Assets.workData.getOnlineDriver().size();i++){
            if (Assets.workData.getOnlineDriver().get(i).getPermission() == UserCard.Permission.ServerDispether) {
                temp.add(Assets.workData.getOnlineDriver().get(i));
            }
        }
        listDispether = ArrayListToObservableList(temp);
//        listView_admin = (ListView<NetClientCard>) root.lookup("#listView_admin");
        if (listDispether != null) ListViewUtil.initListViewFromNetClientCard(listView_dispetsherr,listDispether);
    }

    public void initListDriver(){
        if (listDriver != null) listDriver.clear();
        ArrayList<NetClientCard> temp = new ArrayList<>();
        for (int i=0;i<Assets.workData.getOnlineDriver().size();i++){
            if (Assets.workData.getOnlineDriver().get(i).getPermission() == UserCard.Permission.MobileDriver) {
                temp.add(Assets.workData.getOnlineDriver().get(i));
            }
        }
        listDriver = ArrayListToObservableList(temp);
//        listView_admin = (ListView<NetClientCard>) root.lookup("#listView_admin");
        if (listDriver != null) ListViewUtil.initListViewFromNetClientCard(listView_driver,listDriver);
    }

    public void initAllList(){
        initListDriver();
        initListAdmin();
        initListDispether();
    }

}
