package it.polimi.ingsw.messages;

import java.io.Serial;

/**
 * This message notifies the turn of a player to the client
 */
public class NotifyPlayerInTurnMessage extends NetMessage{
    @Serial
    private static final long serialVersionUID = -4247919821810566385L;

    private final String userInTurn;
    private final boolean yourTurn;


    /**
     * Constructor of a NotifyPlayerInTurnMessage
     * @param username username of the player in turn
     * @param yourTurn true if the player in turn is the one related to this client itself
     */
    public NotifyPlayerInTurnMessage(String username, boolean yourTurn) {
        super(MessageType.NOTIFY_PLAYER_IN_TURN);
        this.userInTurn = username;
        this.yourTurn = yourTurn;
    }

    /**
     *
     * @return the username of the player in turn
     */
    public String getUserInTurn() {
        return userInTurn;
    }

    /**
     *
     * @return if it's the turn of the player related to this client
     */
    public boolean isYourTurn() {
        return yourTurn;
    }
}
