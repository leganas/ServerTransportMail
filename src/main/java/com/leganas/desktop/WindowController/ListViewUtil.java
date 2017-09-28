package com.leganas.desktop.WindowController;

import com.leganas.engine.NetClientCard;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Orientation;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.util.Callback;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by AndreyLS on 23.02.2017.
 */
public class ListViewUtil {
    public static ObservableList<NetClientCard> ArrayListToObservableList(ArrayList<NetClientCard> cardlist){
        if (cardlist == null) return null;
        if (cardlist.size() == 0) return null;

        ObservableList<NetClientCard> cat = FXCollections.observableArrayList();
        for (int i=0; i < cardlist.size(); i++){
            // Временно добавляем всех что доступны, а не onlineUsers
            cat.add(cardlist.get(i));
        }
        return cat;
    }

    public static void initListViewFromNetClientCard(ListView<NetClientCard> listView, ObservableList<NetClientCard> cat){
        if (cat == null) return;
//        if (cardlist.size() == 0) return;
//
//        ObservableList<NetClientCard> cat = FXCollections.observableArrayList();
//        for (int i=0; i < cardlist.size(); i++){
//            // Временно добавляем всех что доступны, а не onlineUsers
//            cat.add(cardlist.get(i));
//        }
//        listView = (ListView<NetClientCard>) root.lookup(selector);
//        if (listView == null) return;
        listView.setItems(cat);
        listView.setLayoutX(10);
        listView.setLayoutY(10);
        listView.setCursor(Cursor.OPEN_HAND);
        final DropShadow effect=new DropShadow();
        effect.setOffsetX(10);
        effect.setOffsetY(10);
        listView.setEffect(effect);
        listView.setStyle("-fx-border-width:3pt; -fx-font:bold 10pt Georgia;");
        // -fx-border-color:navy
        listView.setPrefSize(200, 512);
//        listViewC.setTooltip(new Tooltip("Выберите категорию товара"));
        listView.setOrientation(Orientation.VERTICAL);


        listView.setCellFactory(new Callback<ListView<NetClientCard>, ListCell<NetClientCard>>()  {
            public ListCell<NetClientCard> call(ListView<NetClientCard> p) {
                try {
                    Parent r = FXMLLoader.load(getClass().getResource("/fxml/listcell.fxml"));
                    Button btn = (Button) r.lookup("#listbutton");
                    btn.setEffect(effect);
                    btn.setStyle("-fx-background-color:#66ccff;");
                    btn.setPrefSize(170, 50);

//                btn.setCursor(Cursor.NONE);
                    btn.setWrapText(true);
                    final ListCell<NetClientCard>   cell = new ListCell<NetClientCard>(){
                        @Override public void updateItem(NetClientCard item, boolean empty) {
                            super.updateItem(item, empty);
                            if (item != null) {
                                btn.setText(item.getName());
                                try {
                                    Tooltip tt = new Tooltip();
                                    Parent ttP = FXMLLoader.load(getClass().getResource("/fxml/tooltip.fxml"));
                                    tt.setGraphic(ttP);

                                    Label lab = (Label) ttP.lookup("#labelTT");
                                    lab.setText(item.getName());
                                    btn.setTooltip(tt);
                                    this.setGraphic(r);
                                } catch (IOException e) {
                                }
                            }
                        }
                    };
                    return cell;
                } catch (IOException e) {
                    return null;
                }
            }
        });

    }


}
