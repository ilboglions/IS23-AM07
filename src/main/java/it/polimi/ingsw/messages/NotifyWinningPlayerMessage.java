package it.polimi.ingsw.messages;

import java.io.Serial;

public class NotifyWinningPlayerMessage extends NetMessage{
    @Serial
    private static final long serialVersionUID = 3933634575690113679L;

    private final String winningUser;

    public NotifyWinningPlayerMessage(String winningUser) {
        super(MessageType.NOTIFY_WINNING_PLAYER);
        this.winningUser = winningUser;
    }

    public String getWinningUser() {
        return winningUser;
    }
}
