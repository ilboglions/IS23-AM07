package it.polimi.ingsw.client.view;

import it.polimi.ingsw.server.model.coordinate.Coordinates;
import it.polimi.ingsw.server.model.exceptions.InvalidCoordinatesException;
import it.polimi.ingsw.server.model.tiles.ItemTile;

import java.util.Map;
import java.util.Optional;

public interface ViewInterface {

    void drawPersonalCard(Map<Coordinates, ItemTile> tilesMap, Map<Integer,Integer> pointsReference) throws InvalidCoordinatesException;
    void drawYourBookShelf(Map<Coordinates,ItemTile> tilesMap) throws InvalidCoordinatesException;
    void drawBookShelf(Map<Coordinates,ItemTile> tilesMap, String playerUsername, int order) throws InvalidCoordinatesException;
    void drawChatMessage(String sender, String msg);
    void drawBookShelf(Map<Coordinates,ItemTile> tilesMap, int startR, int startC) throws InvalidCoordinatesException;
    void drawLivingRoom(Map<Coordinates, Optional<ItemTile>> livingRoomMap) throws InvalidCoordinatesException;
    void postNotification(String title, String description);
}
