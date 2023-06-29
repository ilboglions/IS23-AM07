package it.polimi.ingsw.client.localModel;

import it.polimi.ingsw.client.view.ViewInterface;
import it.polimi.ingsw.GameState;
import it.polimi.ingsw.remoteInterfaces.*;
import it.polimi.ingsw.server.model.chat.Message;
import it.polimi.ingsw.server.model.coordinate.Coordinates;
import it.polimi.ingsw.server.model.exceptions.InvalidCoordinatesException;
import it.polimi.ingsw.server.model.tiles.ItemTile;
import it.polimi.ingsw.server.model.tokens.ScoringToken;

import java.io.Serial;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;


/**
 * This is the class that handles all the logic of the game and its status
 */
public class Game extends UnicastRemoteObject implements GameSubscriber, PlayerSubscriber, ChatSubscriber, BookshelfSubscriber, BoardSubscriber {

    @Serial
    private static final long serialVersionUID = 2696723449577327966L;
    /**
     * Chronologically ordered list of message in the player chat. The first one is the oldest received/sent
     */
    private final ArrayList<Message> playerChat;
    /**
     * ordered list of usernames in the game. Ordered by turn from fist to last.
     */
    private final ArrayList<String> players;
    private GameState gameState;
    /**
     * Map to contain the scoring tokens of each player
     */
    private final Map<String, ArrayList<ScoringToken>> playerScoringTokens;
    /**
     * Map to contain the current points of each player
     */
    private final ViewInterface view;
    private final String username;

    private final Map<String, Integer> playerPoints;
    /**
     * Create a new local game to remember the state of the game for a single client
     * @param username the  player that is currently in the game
     * @param view the view of the game
     * @throws RemoteException RMI exception
     */

    public Game(ViewInterface view, String username) throws  RemoteException {
        super();

        this.username = username;
        this.view = view;
        this.playerScoringTokens = new HashMap<>();
        this.playerPoints = new HashMap<>();
        this.players = new ArrayList<>();
        this.players.add(username);
        this.playerScoringTokens.put(username, new ArrayList<>());
        this.playerPoints.put(username, 0);

        try {
            view.drawBookShelf(new HashMap<>(),username, players.indexOf(username));
        } catch (InvalidCoordinatesException ignored) {}

        playerChat = new ArrayList<>();
    }
    /**
     * Insert a new message inside the local history
     * @param msg the message to put in the local chat
     */
    public synchronized void addMessage(Message msg){
        playerChat.add(0, msg);
        List<String> outputMessages = this.playerChat
                                            .stream()
                                            .map(
                                                    m -> m.getRecipient().isPresent() ?
                                                            m.getSender() + " (privately to " + m.getRecipient().get() +"): " + m.getContent() :
                                                            m.getSender() + ": " + m.getContent()
                                            ).toList();
        view.drawChat(outputMessages);
    }
    /**
     * Accept a player inside the game and put it at the end of the list
     * @param player the username of the joined player
     */
    public void joinPlayer(String player) {
        if(!players.contains(player)){
            players.add(player);
            playerPoints.put(player, 0);
            playerScoringTokens.put(player, new ArrayList<>());
        }
        ArrayList<String> chatPlayers = new ArrayList<>(players);
        chatPlayers.remove(this.username);
        view.drawLeaderboard(this.playerPoints);
        view.drawChatPlayersList(chatPlayers);
        try {
            view.drawBookShelf(new HashMap<>(), player, (this.players.indexOf(player) - this.players.indexOf(this.username) + this.players.size()) % this.players.size());
        } catch (InvalidCoordinatesException ignored) {}
    }


    /**
     * Get the list of the player in the game.
     * @return list of the players in the game.
     */
    public ArrayList<String> getPlayers() {
        return new ArrayList<>(players);
    }


    /**
     * Add a new scoring token to a player
     * @param player the player to receive the token
     * @param scoringToken the token to be added to the list
     */
    public void addScoringTokenToPlayer(String player, ScoringToken scoringToken) {
        playerScoringTokens.get(player).add(scoringToken);
        view.drawScoringTokens(new HashMap<>(playerScoringTokens));
    }

    /**
     * Update the current points of a player
     * @param player the player to which the points have to be updated
     * @param currentPoints value to replace the current points of the player
     */
    public synchronized void updatePlayerPoints(String player, int currentPoints) {
        playerPoints.put(player, currentPoints);
        view.drawLeaderboard(playerPoints);
    }

    /**
     * Parse the message between private and broadcast
     * @param from the sender of the message
     * @param msg the message
     */
    @Override
    public synchronized void receiveMessage(String from, String recipient, String msg){
        this.addMessage(new Message(from, recipient, msg));
    }

    /**
     * Notifies the local player that a new one joined the game
     */
    @Override
    public synchronized void receiveMessage(String from, String msg){
        this.addMessage(new Message(from, msg));
    }

    @Override
    public synchronized void notifyPlayerJoined(String username){
        this.joinPlayer(username);
        view.postNotification("New player joined!",username+" joined the game");
    }

    /**
     * Notifies that a player won the game, updating the view
     * @param username the username of the winning player
     * @param points the points of the player that won the game
     * @param scoreboard the total scoreboard, already ordered, the key is the username and the value the points of the user
     * @throws RemoteException RMI Exception
     */
    @Override
    public synchronized void notifyWinningPlayer(String username, int points, Map<String, Integer> scoreboard) throws RemoteException {
        view.postNotification("Game ended!",username+" won the game!");
        //view.drawLeaderboard(scoreboard);
        view.drawWinnerLeaderboard(username, scoreboard);
    }

    /**
     * Notifies the presence of  common goal cards
     * @param commonGoalCards is the list of the common goals of the game
     * @throws RemoteException RMI exception
     */
    @Override
    public synchronized void notifyCommonGoalCards(ArrayList<RemoteCommonGoalCard> commonGoalCards) throws RemoteException {
        try {
            view.drawCommonCards(commonGoalCards);
        } catch (RemoteException ignored) {

        }
    }

    /**
     * Notifies that is the turn of a player
     * @param username username of the player
     * @throws RemoteException RMI exception
     */
    @Override
    public synchronized void notifyPlayerInTurn(String username) throws RemoteException {
        if(this.gameState != GameState.PAUSED) {
            view.drawPlayerInTurn(username, this.username);
        }
    }

    /**
     * Norifies the player that another player crashed
     * @param userCrashed username of the crashed player
     * @throws RemoteException RMI Exception
     */
    @Override
    public synchronized void notifyPlayerCrashed(String userCrashed) throws RemoteException {
        view.postNotification(userCrashed + " crashed!", "Will skip his turn until he reconnects");
    }

    @Override
    public synchronized void notifyTurnOrder(ArrayList<String> playerOrder) throws RemoteException {
        this.players.sort(Comparator.comparingInt(playerOrder::indexOf));

        for(String player : players){
            try {
                view.drawBookShelf(new HashMap<>(), player, (this.players.indexOf(player) - this.players.indexOf(this.username) + this.players.size()) % this.players.size());
            } catch (InvalidCoordinatesException ignored) {}
        }
    }

    /**
     * Get the username of the local player
     * @return username of the player
     */
    @Override
    public String getSubscriberUsername(){
        return this.username;
    }

    /**
     * Update the points of a player
     * @param player the player which points are updated
     * @param overallPoints the overall points of the player
     * @param addedPoints the points added on this state change
     */
    @Override
    public synchronized void updatePoints(String player, int overallPoints, int addedPoints){
        this.updatePlayerPoints(player,overallPoints);
    }


    /**
     * Updates the scoring tokens assigned to a player
     * @param player the username of the player
     * @param tokenPoints the updated List of ScoringToken received by the player
     */
    @Override
    public synchronized void updateTokens(String player, ArrayList<ScoringToken> tokenPoints){
        if(playerScoringTokens.containsKey(player)) {
            playerScoringTokens.replace(player, new ArrayList<>());
        } else {
            playerScoringTokens.put(player, new ArrayList<>());
        }
        for(ScoringToken token : tokenPoints){
            this.addScoringTokenToPlayer(player, token);
        }
    }

    /**
     * Updates the player's personal goal card
     * @param player player username
     * @param remotePersonal personal goal card
     * @throws RemoteException RMI Exception
     */
    @Override
    public synchronized void updatePersonalGoalCard(String player, RemotePersonalGoalCard remotePersonal) throws RemoteException {
        try {
            view.drawPersonalCard(remotePersonal);//remotePersonal.getCardPattern(), remotePersonal.getPointsReference());
        } catch (InvalidCoordinatesException ignored) {}
    }

    /**
     * Updates the personal bookshelf status
     * @param player the username of the players that owns the bookshelf
     * @param tilesInserted the tile inserted by the player
     * @param colChosen the column chosen for the insertion
     * @param currentTilesMap old bookshelf status
     * @throws RemoteException RMI exception
     */
    @Override
    public synchronized void updateBookshelfStatus(String player, ArrayList<ItemTile> tilesInserted, int colChosen, Map<Coordinates, ItemTile> currentTilesMap) throws RemoteException {
        try {
            /*if(this.gameStarted)
                view.drawBookShelf(currentTilesMap, player, (this.players.indexOf(player) - this.players.indexOf(this.username) + this.players.size()) % this.players.size());
            else*/
            view.drawBookShelf(currentTilesMap, player, (this.players.indexOf(player) - this.players.indexOf(this.username) + this.players.size()) % this.players.size());
        } catch (InvalidCoordinatesException ignored) {
        }
    }

    /**
     * Updates the livingroom board notifing the view
     * @param tilesInBoard all the tiles that are in the board, key is the coordinate, tile is the value
     * @throws RemoteException RMI exception
     */
    @Override
    public synchronized void updateBoardStatus(Map<Coordinates, ItemTile> tilesInBoard) throws RemoteException {
        try {
            view.drawLivingRoom(tilesInBoard);
        } catch (InvalidCoordinatesException ignored) {
        }
    }

    /**
     * This method adds all the player that are already in the game in the local list of players
     * @param alreadyJoinedPlayers set of the players already in the game
     */
    @Override
    public synchronized void notifyAlreadyJoinedPlayers(Set<String> alreadyJoinedPlayers){
        for(String p : alreadyJoinedPlayers){
            this.joinPlayer(p);
        }
    }
    /**
     * Updates the view about a  gameState change
     * @param newState new state of the game
     */
    @Override
    public void notifyChangedGameState(GameState newState){
        this.gameState = newState;
        view.postNotification("Game is " + newState.toString(), "");
        if(newState == GameState.PAUSED)
            view.freezeGame();
    }
}
