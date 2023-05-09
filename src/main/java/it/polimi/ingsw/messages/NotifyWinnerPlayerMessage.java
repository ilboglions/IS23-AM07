package it.polimi.ingsw.messages;

import java.io.Serial;
import java.util.Map;

public class NotifyWinnerPlayerMessage extends NetMessage{
    @Serial
    private static final long serialVersionUID = 3933634575690113679L;

    private final String winningUser;
    private final int points;
    private final Map<String, Integer> scoreboard;

    public NotifyWinnerPlayerMessage(String winningUser, int points, Map<String,Integer> scoreboard  ) {
        super(MessageType.NOTIFY_WINNING_PLAYER);
        this.winningUser = winningUser;
        this.points = points;
        this.scoreboard = scoreboard;
    }

    public String getWinnerUser() {
        return winningUser;
    }
    public int getWinnerPoints() {
        return points;
    }
    public Map<String,Integer> getScoreboard(){ return scoreboard;}
}
