package it.polimi.ingsw.messages;

import java.io.Serial;

/**
 * This message transfers s request to join the game
 */
public class JoinGameMessage extends NetMessage {
    @Serial
    private static final long serialVersionUID = -7532758106393537051L;

    /**
     * Constructor of a JoinGameMessage
     */
    public JoinGameMessage() {
        super(MessageType.JOIN_GAME);
    }
}
