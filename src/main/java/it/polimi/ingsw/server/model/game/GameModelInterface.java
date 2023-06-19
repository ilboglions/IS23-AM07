package it.polimi.ingsw.server.model.game;

import it.polimi.ingsw.remoteInterfaces.*;
import it.polimi.ingsw.server.model.chat.Message;
import it.polimi.ingsw.server.model.exceptions.SenderEqualsRecipientException;
import it.polimi.ingsw.server.model.coordinate.Coordinates;
import it.polimi.ingsw.server.model.exceptions.*;

import java.util.ArrayList;

public interface GameModelInterface {

    /**
     * get if the game is already started
     * @return true, if the game is started
     */
    boolean isStarted();
    /**
     * Add a new BoardSubscriber to the listener for livingRoom updates
     * @param subscriber the BoardSubscriber to be added
     */
    void subscribeToListener(BoardSubscriber subscriber);
    /**
     * Add a new BookshelfSubscriber to the listener for all the players' bookshelf
     * @param subscriber the BookshelfSubscriber to be added
     */
    void subscribeToListener(BookshelfSubscriber subscriber);
    /**
     * Add a new ChatSubscriber to the chatListener
     * @param subscriber the ChatSubscriber to be added
     */
    void subscribeToListener(ChatSubscriber subscriber);
    /**
     * Add a new PlayerSubscriber to each player's listener
     * @param subscriber the PlayerSubscriber to be added
     */
    void subscribeToListener(PlayerSubscriber subscriber);
    /**
     * Add a new GameSubscriber to the gameListener
     * @param subscriber the GameSubscriber to be added
     */
    void subscribeToListener(GameSubscriber subscriber);
    /**
     * Add a new GameStateSubscriber to the gameStateListener
     * @param subscriber the GameStateSubscriber to be added
     */
    void subscribeToListener(GameStateSubscriber subscriber);
    /**
     * This method is used to start the game, it chooses randomly a starting player
     * @throws NotAllPlayersHaveJoinedException if the number of player that have joined is less than the number set when the game was created
     * @throws GameNotEndedException if the game has already started
     */
    void start() throws NotAllPlayersHaveJoinedException, GameNotEndedException;
    /**
     * gets the player that is in the turn
     * @return a String representing the name of the player
     * @throws GameEndedException if the game is ended
     * @throws GameNotStartedException if the game has not started yet
     */
    String getPlayerInTurn() throws GameEndedException, GameNotStartedException;
    /**
     * make it possible to move tiles from the livingRoomBoard to a column of the current player in  turn
     * @param source the source coordinates
     * @param column the column chosen by the player
     * @throws InvalidCoordinatesException if the coordinates chosen don't follow the constraints
     * @throws EmptySlotException if one of the coordinate is empty
     * @throws NotEnoughSpaceException it the column has no enough space left
     * @throws GameNotStartedException if the game has not started yet
     */
    void moveTiles(ArrayList<Coordinates> source, int column) throws InvalidCoordinatesException, EmptySlotException, NotEnoughSpaceException, GameNotStartedException;
    /**
     * checks if the item tiles in a certain coordinate can be chosen by the player
     * @param coords the coordinates in the livingroomboard containing the interested tiles
     * @return true, if is possible to get that tiles, false otherwise
     * @throws EmptySlotException if one of the  coordinates referes to an empty slot
     * @throws GameNotStartedException if the game hasn't started yet
     */
    boolean checkValidRetrieve(ArrayList<Coordinates> coords) throws EmptySlotException, GameNotStartedException;
    /**
     * refills the living room
     */
    void refillLivingRoom();
    /**
     * checks if the bookshelf of the player in turn is completed
     * @return true, if the bookshelf is full, false otherwise
     */
    boolean checkBookshelfComplete();
    /**
     * if the game is ended, it returns the username of the winner player
     * @throws GameNotEndedException if the game is not yet ended
     * @throws GameNotStartedException if the game has not started yet
     */
    String getWinner() throws GameNotEndedException, GameNotStartedException;
    /**
     * checks if the livingroom need to be refilled
     * @return true, if is necessary to refill the livingroom, false otherwise
     */
    boolean checkRefill();
    /**
     * This method is used to update the points of the player passed as argument
     * @param username the username of the player whose points you wish to update
     * @throws InvalidPlayerException if there isn't a player with that username inside the game
     * @throws NotEnoughSpaceException if there was an error with the CommonGoalCard
     * @throws GameNotStartedException if the game is not started yet
     */
    void updatePlayerPoints(String username) throws InvalidPlayerException, NotEnoughSpaceException, TokenAlreadyGivenException, GameNotStartedException;
    /**
     * switches the turn to the next player
     * @return true, if the switch can be done, false otherwise
     */
    boolean setPlayerTurn();
    /**
     * check if the game can start
     * @return true, if the game have the right conditions to start, false otherwise
     */
    boolean canStart();
     // CHAT FUNCTIONALITIES
    /**
     * This is the method to retrieve all the messages relative to a player, either sent and received
     * @param player a String that represent the username of the player for which to search the messages
     * @return an Arraylist of messages relative to the player
     * @throws InvalidPlayerException if there isn't a player with that username inside the game
     */
    ArrayList<Message> getPlayerMessages(String player) throws InvalidPlayerException;
    /**
     * This method is used to send a private message to another user
     * @param sender is the username of the sender
     * @param receiver is the username of the receiver
     * @param message is the message object that will be added to the list
     * @throws SenderEqualsRecipientException if the recipient and the sender of the message is the same user
     * @throws InvalidPlayerException if there isn't a player with that username inside the game
     */
    void postMessage(String sender, String receiver, String message) throws SenderEqualsRecipientException, InvalidPlayerException;
    /**
     * This method is used to send a global chat message
     * @param sender is the username of the sender
     * @param message is the message object that will be added to the list
     * @throws InvalidPlayerException if there isn't a player with that username inside the game
     */
    void postMessage(String sender, String message) throws InvalidPlayerException;
    /**
     * Handle the changes in the game when a user rejoins the game after a crash
     * @param username the username of the player that has crashed
     * @throws PlayerNotFoundException if there isn't a player with that username inside the game
     */
    void handleRejoinedPlayer(String username) throws PlayerNotFoundException;
    /**
     * Handle the changes in the game when a user crashes
     * @param username the username of the player that has crashed
     * @throws PlayerNotFoundException if there isn't a player with that username inside the game
     */
    void handleCrashedPlayer(String username) throws PlayerNotFoundException;
    /**
     * This method is used to retrieve if a player is currently crashed or is active
     * @param player the username of the player
     * @return if the player is crashed
     */
    boolean isCrashedPlayer(String player);
    /**
     * Method used to trigger all the listeners when a player joins or re-joins a game after a crash, to receive the complete status of the game such as players bookshelf's, points or livingRoomBoard
     * @param userToBeUpdated the username of the user that needs to receive the updates
     */
    void triggerAllListeners(String userToBeUpdated);
}
