package it.polimi.ingsw.client.view.GUI;

import it.polimi.ingsw.client.view.ViewInterface;
import it.polimi.ingsw.remoteInterfaces.RemoteCommonGoalCard;
import it.polimi.ingsw.server.model.coordinate.Coordinates;
import it.polimi.ingsw.server.model.exceptions.InvalidCoordinatesException;
import it.polimi.ingsw.server.model.tiles.ItemTile;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class GUIView extends Application implements ViewInterface {
    //private final ConnectionHandler controller;

    /*public GUIView(ConnectionType connectionType) {
        ConnectionHandlerFactory factory = new ConnectionHandlerFactory();
        controller = factory.createConnection(connectionType, this);
    }*/

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(GUIView.class.getResource("/fxml/hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void drawPersonalCard(Map<Coordinates, ItemTile> tilesMap, Map<Integer, Integer> pointsReference) throws InvalidCoordinatesException {

    }

    @Override
    public void drawBookShelf(Map<Coordinates, ItemTile> tilesMap, String playerUsername, int order) throws InvalidCoordinatesException {

    }

    @Override
    public void drawLivingRoom(Map<Coordinates, ItemTile> livingRoomMap) throws InvalidCoordinatesException {

    }

    @Override
    public void drawCommonCards(ArrayList<RemoteCommonGoalCard> commonGoalCards) throws InvalidCoordinatesException, RemoteException {

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
}
