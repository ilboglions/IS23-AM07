package it.polimi.ingsw.client.view;

import it.polimi.ingsw.remoteInterfaces.BoardSubscriber;
import it.polimi.ingsw.remoteInterfaces.BookshelfSubscriber;
import it.polimi.ingsw.remoteInterfaces.RemoteCommonGoalCard;
import it.polimi.ingsw.server.model.coordinate.Coordinates;
import it.polimi.ingsw.server.model.exceptions.InvalidCoordinatesException;
import it.polimi.ingsw.server.model.tiles.ItemTile;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public interface ViewInterface  {

    void drawPersonalCard(Map<Coordinates, ItemTile> tilesMap, Map<Integer,Integer> pointsReference) throws InvalidCoordinatesException;
    void drawBookShelf(Map<Coordinates,ItemTile> tilesMap, String playerUsername, int order) throws InvalidCoordinatesException;
    //void drawChatMessage(String sender, String msg, Boolean privateMessage);
    void drawLivingRoom(Map<Coordinates, ItemTile> livingRoomMap) throws InvalidCoordinatesException;
    void drawCommonCards(ArrayList<RemoteCommonGoalCard> commonGoalCards) throws InvalidCoordinatesException, RemoteException;

    void drawChat(List<String> chat);

    void drawLeaderboard(Map<String,Integer> scoreBoard);

    void postNotification(String title, String description);
}
