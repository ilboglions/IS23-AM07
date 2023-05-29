package it.polimi.ingsw.client.view;

import it.polimi.ingsw.Notifications;
import it.polimi.ingsw.remoteInterfaces.RemoteCommonGoalCard;
import it.polimi.ingsw.server.model.coordinate.Coordinates;
import it.polimi.ingsw.server.model.exceptions.InvalidCoordinatesException;
import it.polimi.ingsw.server.model.tiles.ItemTile;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface ViewInterface {

    void drawPersonalCard(Map<Coordinates, ItemTile> tilesMap, Map<Integer,Integer> pointsReference) throws InvalidCoordinatesException;
    void drawBookShelf(Map<Coordinates,ItemTile> tilesMap, String playerUsername, int order) throws InvalidCoordinatesException;
    void drawLivingRoom(Map<Coordinates, ItemTile> livingRoomMap) throws InvalidCoordinatesException;
    void drawCommonCards(ArrayList<RemoteCommonGoalCard> commonGoalCards) throws InvalidCoordinatesException, RemoteException;
    void postNotification(String title, String description);
    void postNotification(Notifications n);
    void drawLeaderboard(Map<String, Integer> playerPoints);
    void drawChat(List<String> outputMessages);
    void drawScene(SceneType scene);
    void drawPlayerInTurn(String userInTurn, String thisUser);
}
