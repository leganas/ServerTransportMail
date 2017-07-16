package com.leganas.desktop;

import com.leganas.desktop.WindowController.AdminWindowController;
import com.leganas.engine.Assets;
import com.leganas.engine.NetClientCard;
import com.leganas.engine.Setting;
import com.leganas.engine.Status;
import com.leganas.engine.controller.ClientController;
import com.leganas.engine.network.packeges.clientTOserver.ClientMessage;
import com.leganas.engine.network.packeges.serverTOclient.ServerMessage;
import com.leganas.engine.utils.Logs;
import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.MapComponentInitializedListener;
import com.lynden.gmapsfx.javascript.event.UIEventType;
import com.lynden.gmapsfx.javascript.object.*;
import com.lynden.gmapsfx.javascript.object.MapOptions;
import com.lynden.gmapsfx.service.directions.*;
import com.lynden.gmapsfx.service.elevation.*;
import com.lynden.gmapsfx.service.geocoding.GeocoderStatus;
import com.lynden.gmapsfx.service.geocoding.GeocodingResult;
import com.lynden.gmapsfx.service.geocoding.GeocodingService;
import com.lynden.gmapsfx.service.geocoding.GeocodingServiceCallback;
import com.lynden.gmapsfx.shapes.Polyline;
import com.lynden.gmapsfx.shapes.PolylineOptions;
import com.lynden.gmapsfx.util.MarkerImageFactory;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Orientation;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import netscape.javascript.JSObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static com.leganas.desktop.WindowController.ListViewUtil.ArrayListToObservableList;
import static com.leganas.desktop.WindowController.ListViewUtil.initListViewFromNetClientCard;

public class AdminWindow extends Application implements MapComponentInitializedListener, DirectionsServiceCallback, ElevationServiceCallback, GeocodingServiceCallback, ClientController.GUIListener {

    protected GoogleMapView mapView;
    protected GoogleMap map;
    protected DirectionsPane directions;

    DirectionsRenderer renderer;
    public Parent root;


    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("../resources/fxml/admin_window.fxml"));
        root = fxmlLoader.load();
        AdminWindowController adminWindowController = fxmlLoader.getController();
        adminWindowController.setMainStage(primaryStage);
        primaryStage.setTitle("Сервер системы управления транспортной логистикой");
        primaryStage.setResizable(true);
        primaryStage.setMinHeight(600);
        primaryStage.setMinWidth(1024);
        primaryStage.setScene(new Scene(root, 1024, 600));
        primaryStage.show();

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                Assets.clientController.dispose();
                if (Setting.programType == Setting.ProgramType.Client) {System.exit(0);} else {
                    Status.statusServer = Status.StatusServer.dispose;
                }
            }
        });

        BorderPane bp = (BorderPane) root.lookup("#root_node");
        mapView = new GoogleMapView();
        mapView.addMapInializedListener(this);
        bp.setCenter(mapView);
        checkMapInit(5000);

        Assets.clientController.setGuiListener(this);
        Assets.clientController.addClientMessageToQuery(new ClientMessage.RequestServerInfo());
        Assets.clientController.addClientMessageToQuery(new ClientMessage.RequestWorkData());
    }

    ListView<NetClientCard> listViewAdminAndDriver;
    ObservableList<NetClientCard> listAdminDriver;
    ListView<NetClientCard> listViewUser;
    ObservableList<NetClientCard> listUsers;
    ListView<NetClientCard> listViewAll;
    ObservableList<NetClientCard> all;


    public void initDriverList(){
        if (listAdminDriver != null) listAdminDriver.clear();
        listAdminDriver = ArrayListToObservableList(Assets.workData.getOnlineDriver());
        listViewAdminAndDriver = (ListView<NetClientCard>) root.lookup("#list_driver");
        initListViewFromNetClientCard(listViewAdminAndDriver,listAdminDriver);
    }

    public void initUserList(){
        if (listUsers != null) listUsers.clear();
        listUsers = ArrayListToObservableList(Assets.workData.getOnlineUsers());
        listViewUser = (ListView<NetClientCard>) root.lookup("#list_users");
        initListViewFromNetClientCard(listViewUser,listUsers);
    }

    public void initAllList(){
        initDriverList();
        initUserList();
        ArrayList<NetClientCard> allCard = new ArrayList<>();
        allCard.addAll(Assets.workData.getOnlineDriver());
        allCard.addAll(Assets.workData.getOnlineUsers());
        all = ArrayListToObservableList(allCard);
        listViewAll = (ListView<NetClientCard>) root.lookup("#list_all");
        initListViewFromNetClientCard(listViewAll,all);
    }

    public static void main(String[] args) {
        launch(args);
    }

    boolean mapIntit = false;
    public void checkMapInit(int pause){
        Thread myThread = new Thread(new Runnable() {
            @Override
            public void run() {
                do {
                    try {
                        Thread.sleep(pause);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Logs.out("Проверка на инициализацию карты");
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            if (!mapIntit) {
                                mapView.getWebview().getEngine().reload();
                            }
                        }
                    });
                } while (!mapIntit);
                Logs.out("Карта прошла инициализацию");
            }
        });
        myThread.setName("checkMapInit");
        myThread.start();
    }

    @Override
    public void mapInitialized() {
        mapIntit = true;
        LatLong center = new LatLong(55.077330, 30.128333);

        //Set the initial properties of the map.
        MapOptions mapOptions = new MapOptions();

        mapOptions.center(new LatLong(55.177330, 30.228333))
                .mapMarker(true)
                .zoom(9)
                .overviewMapControl(true)
                .panControl(false)
                .rotateControl(false)
                .scaleControl(false)
                .streetViewControl(false)
                .zoomControl(false)
                .mapType(MapTypeIdEnum.ROADMAP)
//                .styleString("[{'featureType':'landscape','stylers':[{'saturation':-100},{'lightness':65},{'visibility':'on'}]},{'featureType':'poi','stylers':[{'saturation':-100},{'lightness':51},{'visibility':'simplified'}]},{'featureType':'road.highway','stylers':[{'saturation':-100},{'visibility':'simplified'}]},{\"featureType\":\"road.arterial\",\"stylers\":[{\"saturation\":-100},{\"lightness\":30},{\"visibility\":\"on\"}]},{\"featureType\":\"road.local\",\"stylers\":[{\"saturation\":-100},{\"lightness\":40},{\"visibility\":\"on\"}]},{\"featureType\":\"transit\",\"stylers\":[{\"saturation\":-100},{\"visibility\":\"simplified\"}]},{\"featureType\":\"administrative.province\",\"stylers\":[{\"visibility\":\"off\"}]},{\"featureType\":\"water\",\"elementType\":\"labels\",\"stylers\":[{\"visibility\":\"on\"},{\"lightness\":-25},{\"saturation\":-100}]},{\"featureType\":\"water\",\"elementType\":\"geometry\",\"stylers\":[{\"hue\":\"#ffff00\"},{\"lightness\":-25},{\"saturation\":-97}]}]")
        ;

        map = mapView.createMap(mapOptions, false);
        directions = mapView.getDirec();
        map.setHeading(123.2); // заголовок

// хер знает как работает нужно эксперементировать
//        map.fitBounds(new LatLongBounds(new LatLong(55.177330, 30.228333), center));

        clickToMap();


        //Add a marker to the map
        LatLong markerLatLong = new LatLong(55.177330, 30.228333);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(markerLatLong)
                .label("Хуебень")
                .icon(MarkerImageFactory.createMarkerImage(baseDir()+"image/Arrow_3.png", "png"))
                // если делать так то нужно пихать сразу в пакет com.lynden.gmapsfx.util
                // (а с джар это не представляется возможным, да и не удобно)
                // .icon(MarkerImageFactory.createMarkerImage("../image/Arrow_3.png", "png"))
                .title("My Marker")
                .visible(true);
        Marker marker = new Marker( markerOptions );
        map.addMarker(marker);

        MarkerOptions markerOptions2 = new MarkerOptions();
        LatLong markerLatLong2 = new LatLong(55.377330, 30.528333);
        markerOptions2.position(markerLatLong2)
                .label("Хуебень2")
                .icon(MarkerImageFactory.createMarkerImage(baseDir()+"image/Arrow_3.png", "png"))
//                .icon(MarkerImageFactory.createMarkerImage("image/Arrow_3.png", "png"))
                .title("My Marker")
                .visible(true);
        Marker marker2 = new Marker( markerOptions2 );
        map.addMarker(marker2);

// Информационное окно я так понял привязанное к маркеру
//        InfoWindowOptions infoOptions = new InfoWindowOptions();
//        infoOptions.content("<h2>Here's an info window</h2><h3>with some info</h3>")
//                .position(new LatLong(55.177330, 30.228333));
//
//        InfoWindow window = new InfoWindow(infoOptions);
//        window.open(map, marker);

// Тупо создаём просто линию между точками в которых находятся наши маркеры
        LatLong[] ary = new LatLong[]{markerLatLong, markerLatLong2};
        MVCArray mvc = new MVCArray(ary);

        PolylineOptions polyOpts = new PolylineOptions()
                .path(mvc)
                .strokeColor("red")
                .strokeWeight(2);

        Polyline poly = new Polyline(polyOpts);
        map.addMapShape(poly);
        map.addUIEventHandler(poly, UIEventType.click, (JSObject obj) -> {
            LatLong ll = new LatLong((JSObject) obj.getMember("latLng"));
//            System.out.println("You clicked the line at LatLong: lat: " + ll.getLatitude() + " lng: " + ll.getLongitude());
        });
// тут конец кода рисования линий


        // Тут короче маршрут

        GeocodingService gs = new GeocodingService(); // геокодинг сервисы зачем то тут запускаем

        DirectionsService ds = new DirectionsService(); // подрубаем сервисы построения маршрутов
        // инициализируем рендер для маршрутов наших
        // 1й параметр (можно ли таскать метки которые он создаст)
        // 2й карта с которой работаем
        // 3й ну собственно ссылка Pane на котором будут рисоватся эти маршруты
        renderer = new DirectionsRenderer(true, map, directions);

        // это точки куда нужно заехать (думаю можно и не использовать)
        DirectionsWaypoint[] dw = new DirectionsWaypoint[2];
        dw[0] = new DirectionsWaypoint("Минск");
        dw[1] = new DirectionsWaypoint("Могилёв");


        // тут мы формируем запрос от куда куда и подрубаем наши точки заезда
        DirectionsRequest dr = new DirectionsRequest(
                "Витебск",
                "Орша",
                TravelModes.DRIVING,
                dw);

        // запускаем наш сервис построения маршрута (и примем слушателем результат)
        // renderer - нам это всё нарисует
        // н сам там походу использует в нутри GeocodingService
        // хуй его знает но имено проходят
        ds.getRoute(dr, this, renderer);



        // Это гавно типа определяем высоту в указанной точке
        // нахуй нужно не понтно мне лично :)
        LatLong[] location = new LatLong[1];
        location[0] = new LatLong(-19.744056, -43.958699);
        LocationElevationRequest loc = new LocationElevationRequest(location);
        ElevationService es = new ElevationService();
        es.getElevationForLocations(loc, this);

    }

    private void clickToMap() {
        // тыкаем по карте и получаем тем самым координаты
        map.addUIEventHandler(UIEventType.click, (JSObject obj) -> {
            LatLong ll = new LatLong((JSObject) obj.getMember("latLng"));
            //System.out.println("LatLong: lat: " + ll.getLatitude() + " lng: " + ll.getLongitude());
            System.out.println(ll.toString());
        });
    }

    /**Функция поиска текущей папки программы
     * хуй знает как она работает в линукс (не проверял могут быть проблемы с / и \)*/
    private String baseDir() {
        String baseDir = "";
        try {
            baseDir = new File(".").getCanonicalPath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        baseDir = baseDir.replace('\\','/');
        baseDir = "file:///" + baseDir + "/src/";
        return baseDir;
    }

    @Override
    public void directionsReceived(DirectionsResult results, DirectionStatus status) {
//        MainApp класс хранит метод который отображает путь в панельке от туда можно взять
        if(status.equals(DirectionStatus.OK)){
            // показывает панельку с маршрутом
//            mapView.getMap().showDirectionsPane();
            System.out.println("OK");

            DirectionsResult e = results;
            GeocodingService gs = new GeocodingService();

            System.out.println("SIZE ROUTES: " + e.getRoutes().size() + "\n" + "ORIGIN: " + e.getRoutes().get(0).getLegs().get(0).getStartLocation());

            // если это не за коментить то будут запрашиватся геокоды и потом слушателем примем и выведем в консоль
            // с ними еще нужно разбиратся
            // gs.reverseGeocode(e.getRoutes().get(0).getLegs().get(0).getStartLocation().getLatitude(), e.getRoutes().get(0).getLegs().get(0).getStartLocation().getLongitude(), this);
            System.out.println("LEGS SIZE: " + e.getRoutes().get(0).getLegs().size());
            System.out.println("WAYPOINTS " +e.getGeocodedWaypoints().size());
            /*
            // тут выводит дистанцию между каждой промежуточной точной я так понял
            double d = 0;
            for(DirectionsLeg g : e.getRoutes().get(0).getLegs()){
                d += g.getDistance().getValue();
                System.out.println("DISTANCE " + g.getDistance().getValue());
            }
            */
            try{
                System.out.println("Distancia total = " + e.getRoutes().get(0).getLegs().get(0).getDistance().getText());
            } catch(Exception ex){
                System.out.println("ERRO: " + ex.getMessage());
            }
            System.out.println("LEG(0)");
            System.out.println(e.getRoutes().get(0).getLegs().get(0).getSteps().size());
            /*
            // раставляет путивые ключевые точки (хуй его знает как он это делаем но они есть в результате e )
            for(DirectionsSteps ds : e.getRoutes().get(0).getLegs().get(0).getSteps()){
                System.out.println(ds.getStartLocation().toString() + " x " + ds.getEndLocation().toString());
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(ds.getStartLocation())
                        .title(ds.getInstructions())
                        .animation(Animation.DROP)
                        .visible(true);
                Marker myMarker = new Marker(markerOptions);
                map.addMarker(myMarker);
            }
            */
            System.out.println(renderer.toString());
        }
    }

    @Override
    public void elevationsReceived(ElevationResult[] results, ElevationStatus status) {
        if(status.equals(ElevationStatus.OK)){
            for(ElevationResult e : results){
                System.out.println(" Elevation on "+ e.getLocation().toString() + " is " + e.getElevation());
            }
        }
    }

    @Override
    public void geocodedResultsReceived(GeocodingResult[] results, GeocoderStatus status) {
        if(status.equals(GeocoderStatus.OK)){
            for(GeocodingResult e : results){
                System.out.println(e.getVariableName());
                System.out.println("GEOCODE: " + e.getFormattedAddress() + "\n" + e.toString());
            }

        }
    }

    @Override
    public void GUIMessage(Object msg){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    if (msg instanceof ServerMessage) {
                        if (msg instanceof ServerMessage.ReturnServerInfo) {

                        }
                        if (msg instanceof ServerMessage.ReturnWorkData) {
                            initAllList();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
