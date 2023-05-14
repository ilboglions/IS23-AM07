package it.polimi.ingsw.client.localModel;

import it.polimi.ingsw.client.connection.ConnectionHandler;
import it.polimi.ingsw.client.view.ViewInterface;
import it.polimi.ingsw.remoteInterfaces.*;
import it.polimi.ingsw.server.model.chat.Message;
import it.polimi.ingsw.server.model.coordinate.Coordinates;
import it.polimi.ingsw.server.model.exceptions.InvalidCoordinatesException;
import it.polimi.ingsw.server.model.listeners.GameListener;
import it.polimi.ingsw.server.model.tiles.ItemTile;
import it.polimi.ingsw.server.model.tokens.ScoringToken;

import java.io.Serial;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.stream.Collectors;

public class Game extends UnicastRemoteObject implements GameSubscriber, PlayerSubscriber, ChatSubscriber, BookshelfSubscriber, Serializable, BoardSubscriber {

    @Serial
    private static final long serialVersionUID = 2121073513337926446L;
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
    private final Map<String, ArrayList<ScoringToken>> playerScoringTokens;
    /**
     * Map to contain the current points of each player
     */
    private final Map<String, Integer> playerPoints;
    /**
     * Create a new local game to remember the state of the game for a single client
     * @param players the list of players that are currently in the game
     */
    private final ViewInterface view;
    private final String username;



    public Game(ArrayList<String> players, ViewInterface view, String username) throws RemoteException {
        super();

        this.username = username;
        this.view = view;
        this.playerScoringTokens = new HashMap<>();
        this.playerPoints = new HashMap<>();
        this.players = new ArrayList<>(players);
        for (String player : players) {
            playerScoringTokens.put(player,new ArrayList<>());
            playerPoints.put(player, 0);
            Map<Coordinates,ItemTile> bookshelfMap = new HashMap<>();
            try {
                view.drawBookShelf(bookshelfMap,username,players.indexOf(username));
            } catch (InvalidCoordinatesException e) {
                throw new RuntimeException(e);
            }
        }

        playerChat = new ArrayList<>();
    }
    /**
     * Insert a new message inside the local history
     * @param msg the message to put in the local chat
     */
    public void addMessage(Message msg) throws RemoteException{
        playerChat.add(msg);
        List<String> outputMessages = getLastNMessages(playerChat.size())
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
    public void joinPlayer(String player) throws RemoteException{
        players.add(player);
    }
    /**
     * Accept a player inside the game and put it in a certain position. Usually used to handle crashed players
     * @param player the username of the joined player
     */
    public void joinPlayer(String player, int index)throws RemoteException {
        players.add(index, player);
    }
    /**
     * Remove a crashed player from the game
     * @param player the username of the crashed player
     */
    public void crashPlayer(String player)throws RemoteException {
        players.remove(player);
    }

    /**
     * Get the list of the player in the game.
     * @return list of the players in the game.
     */
    public ArrayList<String> getPlayers() throws RemoteException {
        return new ArrayList<>(players);
    }

    /**
     * Get the list of the messages in the local chat
     * @return list of the messages in the local chat
     */
    public ArrayList<Message> getPlayerChat() throws RemoteException {
        return new ArrayList<>(playerChat);
    }

    /**
     * Get a list of the last N messages from the local history of the chat
     * @param N number of messages to be retrieved
     * @return a list of the last N messages from the local history of the chat
     */
    public ArrayList<Message> getLastNMessages(int N) throws RemoteException {
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
    public ArrayList<ScoringToken> getPlayerScoringToken(String player) throws RemoteException {
        return new ArrayList<>(playerScoringTokens.get(player));
    }

    /**
     * Retrieve the current points of a player
     * @param player the username of the player
     * @return the current points of a player
     */
    public int getPlayerPoints(String player) throws RemoteException {
        return playerPoints.get(player);
    }

    /**
     * Add a new scoring token to a player
     * @param player the player to receive the token
     * @param scoringToken the token to be added to the list
     */
    public void addScoringTokenToPlayer(String player, ScoringToken scoringToken) throws RemoteException {
        playerScoringTokens.get(player).add(scoringToken);
    }

    /**
     * Update the current points of a player
     * @param player the player to which the points have to be updated
     * @param currentPoints value to replace the current points of the player
     */
    public void updatePlayerPoints(String player, int currentPoints) throws RemoteException {
        playerPoints.replace(player,currentPoints);
        view.drawLeaderboard(playerPoints);
    }

    @Override
    public void receiveMessage(String from, String msg, Boolean privateMessage) throws RemoteException {
        if( privateMessage){
            this.addMessage(new Message(from, this.username, msg));
        } else {
            this.addMessage(new Message(from, msg));
        }

    }

    @Override
    public void notifyPlayerJoined(String username) throws RemoteException {
        this.joinPlayer(username);
        view.drawLeaderboard(this.playerPoints);
        Map<Coordinates,ItemTile> bookshelfMap = new HashMap<>();
        try {
            view.drawBookShelf(bookshelfMap,username,players.indexOf(username));
        } catch (InvalidCoordinatesException e) {
            throw new RuntimeException(e);
        }
        view.postNotification("New player joined!",username+" joined the game");
    }

    @Override
    public void notifyWinningPlayer(String username, int points, Map<String, Integer> scoreboard) throws RemoteException {
        view.postNotification("Game ended!",username+" won the game!");
        view.drawLeaderboard(scoreboard);
    }

    @Override
    public void notifyCommonGoalCards(ArrayList<RemoteCommonGoalCard> commonGoalCards) throws RemoteException {
        try {
            view.drawCommonCards(commonGoalCards);
        } catch (InvalidCoordinatesException | RemoteException ignored) {

        }
    }

    @Override
    public void notifyPlayerInTurn(String username) throws RemoteException {
        if(username.equals(this.username))
            view.postNotification("It's your turn!","choose the tiles on the board!");
        else
            view.postNotification("It's the turn of "+username,"let's see what's happen!");
    }

    @Override
    public void notifyPlayerCrashed(String userCrashed) throws RemoteException {
        view.postNotification(username+" crashed!","hope is ok :(");
    }

    @Override
    public String getSubscriberUsername() throws RemoteException {
        return this.username;
    }

    @Override
    public void updatePoints(String player, int overallPoints, int addedPoints) throws RemoteException {
        this.updatePlayerPoints(player,overallPoints);
    }

    @Override
    public void updateTokens(String player, ArrayList<ScoringToken> tokenPoints) throws RemoteException {

    }

    @Override
    public void updatePersonalGoalCard(String player, RemotePersonalGoalCard remotePersonal) throws RemoteException {
        try {
            view.drawPersonalCard(remotePersonal.getCardPattern(), remotePersonal.getPointsReference());
        } catch (InvalidCoordinatesException ignored) {
        }
    }

    @Override
    public void updateBookshelfStatus(String player, ArrayList<ItemTile> tilesInserted, int colChosen, Map<Coordinates, ItemTile> currentTilesMap) {
        try {
            view.drawBookShelf(currentTilesMap,player,this.players.indexOf(player));
        } catch (InvalidCoordinatesException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateBoardStatus(Map<Coordinates, ItemTile> tilesInBoard) throws RemoteException {
        try {
            view.drawLivingRoom(tilesInBoard);
        } catch (InvalidCoordinatesException e) {
            throw new RuntimeException(e);
        }
    }
}
