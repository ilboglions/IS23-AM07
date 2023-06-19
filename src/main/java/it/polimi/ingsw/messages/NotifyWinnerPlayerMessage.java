package it.polimi.ingsw.messages;

import java.io.Serial;
import java.util.Map;

/**
 * This message notifies the client that a player won the game
 */
public class NotifyWinnerPlayerMessage extends NetMessage{
    @Serial
    private static final long serialVersionUID = 3933634575690113679L;

    private final String winningUser;
    private final int points;
    private final Map<String, Integer> scoreboard;

    /**
     * Constructor of a NotifyWinnerPlayerMessage
     * @param winningUser username of the winner
     * @param points total points of the winner
     * @param scoreboard map with username and points of each player
     */
    public NotifyWinnerPlayerMessage(String winningUser, int points, Map<String,Integer> scoreboard  ) {
        super(MessageType.NOTIFY_WINNING_PLAYER);
        this.winningUser = winningUser;
        this.points = points;
        this.scoreboard = scoreboard;
    }

    /**
     *
     * @return the username of the winner
     */
    public String getWinnerUser() {
        return winningUser;
    }

    /**
     * @return the total points of the winner
     */
    public int getWinnerPoints() {
        return points;
    }

    /**
     *
     * @return a map with username and points of each player
     */
    public Map<String,Integer> getScoreboard(){ return scoreboard;}
}
