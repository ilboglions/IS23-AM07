package it.polimi.ingsw.messages;

import java.io.Serial;

/**
 * This message transfers the communication about the end of the connection between server and client
 */
public class CloseConnectionMessage extends NetMessage{

    @Serial
    private static final long serialVersionUID = 3448524243229177507L;

    /**
     * Constructor of a CloseConnectionMessage
     */
    public CloseConnectionMessage() {
        super(MessageType.CLOSE_CONNECTION);
    }
}
