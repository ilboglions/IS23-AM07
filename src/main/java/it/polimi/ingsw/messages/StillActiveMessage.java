package it.polimi.ingsw.messages;

import java.io.Serial;

/**
 * This message is to notify that the connection is still active
 */
public class StillActiveMessage extends NetMessage {
    @Serial
    private static final long serialVersionUID = -5002383024317087494L;

    /**
     * Constructor of a StillActiveMessage
     */
    public StillActiveMessage() {
        super(MessageType.STILL_ACTIVE);
    }
}
