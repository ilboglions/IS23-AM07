package it.polimi.ingsw.client.localModel;

import it.polimi.ingsw.client.view.ViewInterface;
import it.polimi.ingsw.GameState;
import it.polimi.ingsw.remoteInterfaces.*;
import it.polimi.ingsw.server.model.chat.Message;
import it.polimi.ingsw.server.model.coordinate.Coordinates;
import it.polimi.ingsw.server.model.exceptions.InvalidCoordinatesException;
import it.polimi.ingsw.server.model.game.GameModelInterface;
import it.polimi.ingsw.server.model.tiles.ItemTile;
import it.polimi.ingsw.server.model.tokens.ScoringToken;

import java.io.Serial;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;


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
    /**
     * Map to contain the scoring tokens of each player
     */
    private final Map<String, Set<ScoringToken>> playerScoringTokens;
    /**
     * Map to contain the current points of each player
     */
    private final ViewInterface view;
    private final String username;
    private Boolean gameStarted;
    private final Map<String, Integer> playerPoints;
    /**
     * Create a new local game to remember the state of the game for a single client
     * @param username the  player that is currently in the game
     * @param view the view of the game
     */
    public Game(ViewInterface view, String username) throws  RemoteException {
        super();

        this.username = username;
        this.view = view;
        this.playerScoringTokens = new HashMap<>();
        this.playerPoints = new HashMap<>();
        this.players = new ArrayList<>();
        this.players.add(username);
        this.gameStarted = false;
        this.playerScoringTokens.put(username, new HashSet<>());
        this.playerPoints.put(username, 0);

        try {
            view.drawBookShelf(new HashMap<>(),username, players.indexOf(username));
        } catch (InvalidCoordinatesException e) {
            throw new RuntimeException(e);
        }

        playerChat = new ArrayList<>();
    }
    /**
     * Insert a new message inside the local history
     * @param msg the message to put in the local chat
     */
    public synchronized void addMessage(Message msg) throws RemoteException{
        playerChat.add(0, msg);
        List<String> outputMessages = this.playerChat
                                            .stream()
                                            .map(
                                                    m ->  m.getRecipient().isPresent() ?
                                                            "<" + m.getSender() + " -> " + m.getRecipient().get() + "> " + m.getContent()
                                                            :"<" + m.getSender() + "> " + m.getContent()
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
            playerScoringTokens.put(player, new HashSet<>());
        }

        view.drawLeaderboard(this.playerPoints);
        try {
            view.drawBookShelf(new HashMap<>(), player, (this.players.indexOf(player) - this.players.indexOf(this.username) + this.players.size()) % this.players.size());
        } catch (InvalidCoordinatesException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Accept a player inside the game and put it in a certain position. Usually used to handle crashed players
     * @param player the username of the joined player
     */
    public void joinPlayer(String player, int index) {
        players.add(index, player);
    }
    /**
     * Remove a crashed player from the game
     * @param player the username of the crashed player
     */
    public void crashPlayer(String player) {
        players.remove(player);
    }

    /**
     * Get the list of the player in the game.
     * @return list of the players in the game.
     */
    public ArrayList<String> getPlayers() {
        return new ArrayList<>(players);
    }

    /**
     * Get the list of the messages in the local chat
     * @return list of the messages in the local chat
     */
    public ArrayList<Message> getPlayerChat() {
        return new ArrayList<>(playerChat);
    }

    /**
     * Get a list of the last N messages from the local history of the chat
     * @param N number of messages to be retrieved
     * @return a list of the last N messages from the local history of the chat
     */
    public ArrayList<Message> getLastNMessages(int N) {
        ArrayList<Message> temp = new ArrayList<>();
        for(int i=0; i<N; i++)
            temp.add(0, playerChat.get(playerChat.size() - 1 - i));
        return temp;
    }

    /**
     * Get the list of the scoring tokens of a certain player
     * @param player the username of the owner of the scoring tokens we want to retrieve
     * @return the list of the scoring tokens of a player
     */
    public Set<ScoringToken> getPlayerScoringToken(String player) {
        return new HashSet<>(playerScoringTokens.get(player));
    }

    /**
     * Retrieve the current points of a player
     * @param player the username of the player
     * @return the current points of a player
     */
    public int getPlayerPoints(String player) {
        return playerPoints.get(player);
    }

    /**
     * Add a new scoring token to a player
     * @param player the player to receive the token
     * @param scoringToken the token to be added to the list
     */
    public void addScoringTokenToPlayer(String player, ScoringToken scoringToken) {
        playerScoringTokens.get(player).add(scoringToken);
    }

    public Map<String, Integer> getMapPlayerPoints() {
        return new HashMap<>(playerPoints);
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
     * Parse the message between private & broadcast
     * @param from the sender of the message
     * @param msg the message
     * @throws RemoteException
     */
    @Override
    public synchronized void receiveMessage(String from, String recipient, String msg) throws RemoteException {
        this.addMessage(new Message(from, recipient, msg));
    }

    /**
     * Notifies the local player that a new one joined the game, updating the viewhe username of the player that has joined
     * @throws RemoteException
     */
    @Override
    public synchronized void receiveMessage(String from, String msg) throws RemoteException {
        this.addMessage(new Message(from, msg));
    }

    @Override
    public synchronized void notifyPlayerJoined(String username) throws RemoteException {
        this.joinPlayer(username);
        view.postNotification("New player joined!",username+" joined the game");
    }

    /**
     * Notifies that a player won the game, updating the view
     * @param username the username of the winning player
     * @param points the points of the player that won the game
     * @param scoreboard the total scoreboard, already ordered, the key is the username and the value the points of the user
     * @throws RemoteException
     */
    @Override
    public synchronized void notifyWinningPlayer(String username, int points, Map<String, Integer> scoreboard) throws RemoteException {
        view.postNotification("Game ended!",username+" won the game!");
        view.drawLeaderboard(scoreboard);
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
        } catch (InvalidCoordinatesException | RemoteException ignored) {

        }
    }

    /**
     * Notifies that is the turn of a player
     * @param username username of the player
     * @throws RemoteException RMI exception
     */
    @Override
    public synchronized void notifyPlayerInTurn(String username) throws RemoteException {
        view.drawPlayerInTurn(username, this.username);
    }

    /**
     * Norifies the player that another player crashed
     * @param userCrashed username of the crashed player
     * @throws RemoteException
     */
    @Override
    public synchronized void notifyPlayerCrashed(String userCrashed) throws RemoteException {
        view.postNotification(userCrashed + "crashed!", "Will skip his turn until he reconnects");
    }

    @Override
    public synchronized void notifyTurnOrder(ArrayList<String> playerOrder) throws RemoteException {
        this.players.sort(Comparator.comparingInt(playerOrder::indexOf));
        this.gameStarted = true;

        for(String player : players){
            try {
                view.drawBookShelf(new HashMap<>(), player, (this.players.indexOf(player) - this.players.indexOf(this.username) + this.players.size()) % this.players.size());
            } catch (InvalidCoordinatesException ignored) {
            }
        }
    }

    /**
     * Get the username of the local player
     * @return username of the player
     * @throws RemoteException
     */
    @Override
    public String getSubscriberUsername() throws RemoteException {
        return this.username;
    }

    /**
     * Update the points of a plater
     * @param player the player which points are updated
     * @param overallPoints the overall points of the player
     * @param addedPoints the points added on this state change
     * @throws RemoteException RMI exception
     */
    @Override
    public synchronized void updatePoints(String player, int overallPoints, int addedPoints) throws RemoteException {
        this.updatePlayerPoints(player,overallPoints);
    }


    @Override
    public synchronized void updateTokens(String player, ArrayList<ScoringToken> tokenPoints) throws RemoteException {
        for(ScoringToken token : tokenPoints){
            this.addScoringTokenToPlayer(player, token);
        }
    }

    /**
     * Updates the player's personal goal card
     * @param player player username
     * @param remotePersonal personal goal card
     * @throws RemoteException
     */
    @Override
    public synchronized void updatePersonalGoalCard(String player, RemotePersonalGoalCard remotePersonal) throws RemoteException {
        try {
            view.drawPersonalCard(remotePersonal);//remotePersonal.getCardPattern(), remotePersonal.getPointsReference());
        } catch (InvalidCoordinatesException ignored) {}
    }

    /**
     * Updates the personal bookshelf statis
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
        } catch (InvalidCoordinatesException e) {
            throw new RuntimeException(e);
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
        } catch (InvalidCoordinatesException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * @param newState
     * @throws RemoteException
     */

    public synchronized void notifyChangeGameStatus(GameState newState) throws RemoteException {
        view.postNotification("Game is" + newState.toString(), "");
    }

    @Override
    public synchronized void notifyAlreadyJoinedPlayers(Set<String> alreadyJoinedPlayers) throws RemoteException {
        for(String p : alreadyJoinedPlayers){
            this.joinPlayer(p);
        }
    }
    /**
     * @param newState
     * @throws RemoteException
     */
    @Override
    public void notifyChangedGameState(GameState newState) throws RemoteException {
        view.postNotification("Game is" + newState.toString(), "");
    }
}
