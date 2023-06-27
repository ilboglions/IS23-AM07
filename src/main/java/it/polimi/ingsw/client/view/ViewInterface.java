package it.polimi.ingsw.client.view;

import it.polimi.ingsw.Notifications;
import it.polimi.ingsw.remoteInterfaces.RemoteCommonGoalCard;
import it.polimi.ingsw.remoteInterfaces.RemotePersonalGoalCard;
import it.polimi.ingsw.server.model.coordinate.Coordinates;
import it.polimi.ingsw.server.model.exceptions.InvalidCoordinatesException;
import it.polimi.ingsw.server.model.tiles.ItemTile;
import it.polimi.ingsw.server.model.tokens.ScoringToken;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface ViewInterface {

    /**
     * This method is used to draw the player's personalGoalCard
     * @param card reference to a RemotePersonalGoalCard
     * @throws InvalidCoordinatesException if the coordinates given in the card are invalid
     * @throws RemoteException RMI Exception
     */
    void drawPersonalCard(RemotePersonalGoalCard card) throws InvalidCoordinatesException, RemoteException;

    /**
     * Draws a player's personalBookshelf
     * @param tilesMap map of the tiles present in the bookshelf (coordinates is the key, tile is the value)
     * @param playerUsername username of the bookshelf's owner
     * @param order order of turn for positioning correctly the bookshelf
     * @throws InvalidCoordinatesException if the coordinates in the tilesMap are invalid
     */
    void drawBookShelf(Map<Coordinates,ItemTile> tilesMap, String playerUsername, int order) throws InvalidCoordinatesException;

    /**
     * Draws the livingRoom Board
     * @param livingRoomMap map of the tiles present on the board  (coordinates is the key, tile is the value)
     * @throws InvalidCoordinatesException if the coordinates in the map are invalid
     */
    void drawLivingRoom(Map<Coordinates, ItemTile> livingRoomMap) throws InvalidCoordinatesException;

    /**
     * Draws the commonGoal cards
     * @param commonGoalCards list of all the commonGoalCards
     * @throws RemoteException RMI Exception
     */
    void drawCommonCards(ArrayList<RemoteCommonGoalCard> commonGoalCards) throws RemoteException;

    /**
     * Posts a Game notification
     * @param title title of the notification
     * @param description description of the notification
     */
    void postNotification(String title, String description);

    /**
     * Posts a Game notification
     * @param n notification to be displayed
     */
    void postNotification(Notifications n);

    /**
     * Draws the updated leaderboard
     * @param playerPoints map with the username of the players as the key and the points as value
     */
    void drawLeaderboard(Map<String, Integer> playerPoints);

    /**
     * Draws the game chat
     * @param outputMessages list of all the messages in the chat
     */
    void drawChat(List<String> outputMessages);

    /**
     * Draw a scene (changes the scene displayed)
     * @param scene type of the scene
     */
    void drawScene(SceneType scene);

    /**
     * Displays a new player in turn
     * @param userInTurn username of the player in turn
     * @param thisUser username of the local player
     */
    void drawPlayerInTurn(String userInTurn, String thisUser);

    /**
     * Draws the list of players available for private messages on the chat
     * @param players list of the players
     */
    void drawChatPlayersList(ArrayList<String> players);

    /**
     * Draws the final leaderboard (after the end of the game)
     * @param username username of the winner
     * @param playerPoints map with the username of the players as key, the final score as value
     */
    void drawWinnerLeaderboard(String username, Map<String, Integer> playerPoints);

    /**
     * Draws the scoring tokens owned by each player
     * @param playerScoringTokens map with the username of the player as key, a list of the token owned as value
     */
    void drawScoringTokens(Map<String, ArrayList<ScoringToken>> playerScoringTokens);

    /**
     * This method enables the client to join back to the lobby after the end of a game
     */
    void backToLobby();

    /**
     * This method is called when the game is paused and all the players must be frozen (the chat is not frozen)
     */
    void freezeGame();
}
