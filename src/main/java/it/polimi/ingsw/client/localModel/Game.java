package it.polimi.ingsw.client.localModel;

import it.polimi.ingsw.server.model.chat.Message;
import it.polimi.ingsw.server.model.tokens.ScoringToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Game {

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
    public Game(ArrayList<String> players) {
        this.playerScoringTokens = new HashMap<>();
        this.playerPoints = new HashMap<>();
        this.players = new ArrayList<>(players);
        for (String player : players) {
            playerScoringTokens.put(player,new ArrayList<>());
            playerPoints.put(player, 0);
        }

        playerChat = new ArrayList<>();
    }
    /**
     * Insert a new message inside the local history
     * @param msg the message to put in the local chat
     */
    public void addMessage(Message msg) {
        playerChat.add(msg);
    }
    /**
     * Accept a player inside the game and put it at the end of the list
     * @param player the username of the joined player
     */
    public void joinPlayer(String player) {
        players.add(player);
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
    public ArrayList<ScoringToken> getPlayerScoringToken(String player) {
        return new ArrayList<>(playerScoringTokens.get(player));
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

    /**
     * Update the current points of a player
     * @param player the player to which the points have to be updated
     * @param currentPoints value to replace the current points of the player
     */
    public void updatePlayerPoints(String player, int currentPoints) {
        playerPoints.replace(player,currentPoints);
    }

}
