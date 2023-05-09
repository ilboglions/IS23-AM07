package it.polimi.ingsw.messages;

import java.io.Serial;

public class NotifyPlayerInTurnMessage extends NetMessage{
    @Serial
    private static final long serialVersionUID = -4247919821810566385L;

    private final String userInTurn;
    private final boolean yourTurn;

    public NotifyPlayerInTurnMessage(String username, boolean yourTurn) {
        super(MessageType.NOTIFY_PLAYER_IN_TURN);
        this.userInTurn = username;
        this.yourTurn = yourTurn;
    }

    public String getUserInTurn() {
        return userInTurn;
    }

    public boolean isYourTurn() {
        return yourTurn;
    }
}
