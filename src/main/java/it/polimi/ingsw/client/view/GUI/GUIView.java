package it.polimi.ingsw.client.view.GUI;

import it.polimi.ingsw.client.connection.ConnectionHandler;
import it.polimi.ingsw.client.connection.ConnectionHandlerFactory;
import it.polimi.ingsw.client.connection.ConnectionType;
import it.polimi.ingsw.client.view.ViewInterface;
import it.polimi.ingsw.remoteInterfaces.RemoteCommonGoalCard;
import it.polimi.ingsw.server.model.coordinate.Coordinates;
import it.polimi.ingsw.server.model.exceptions.InvalidCoordinatesException;
import it.polimi.ingsw.server.model.tiles.ItemTile;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GUIView extends Application implements ViewInterface {
    private ConnectionHandler controller;
    private GUIController guiController;

    /*public GUIView(ConnectionType connectionType) {
        ConnectionHandlerFactory factory = new ConnectionHandlerFactory();
        controller = factory.createConnection(connectionType, this);
    }*/

    @Override
    public void start(Stage stage) throws Exception {

        Parameters parameters = getParameters();
        List<String> args = parameters.getRaw();
        ConnectionType connectionType;
        if ( args.size() == 1) {
            connectionType = args.get(0).equals("--TCP") ? ConnectionType.TCP : ConnectionType.RMI;
        } else {
            connectionType = ConnectionType.RMI;

        }
        ConnectionHandlerFactory factory = new ConnectionHandlerFactory();
        controller = factory.createConnection(connectionType, this);
        FXMLLoader fxmlLoader = new FXMLLoader(GUIView.class.getResource("/fxml/lobby-view.fxml"));
        guiController = fxmlLoader.getController();
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);

        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {

        launch(args);
    }

    @Override
    public void drawPersonalCard(Map<Coordinates, ItemTile> tilesMap, Map<Integer, Integer> pointsReference) {

    }

    @Override
    public void drawBookShelf(Map<Coordinates, ItemTile> tilesMap, String playerUsername, int order) {

    }

    @Override
    public void drawLivingRoom(Map<Coordinates, ItemTile> livingRoomMap) throws InvalidCoordinatesException {

    }

    @Override
    public void drawCommonCards(ArrayList<RemoteCommonGoalCard> commonGoalCards) {

    }


    @Override
    public void postNotification(String title, String description) {

    }

    @Override
    public void drawLeaderboard(Map<String, Integer> playerPoints) {

    }

    @Override
    public void drawChat(List<String> outputMessages) {

    }

    @Override
    public void drawGameScene() {

    }

    @Override
    public void drawPlayerInTurn(String userInTurn, String thisUser) {

    }
}
