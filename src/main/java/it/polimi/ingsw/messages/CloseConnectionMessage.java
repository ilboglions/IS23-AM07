package it.polimi.ingsw.messages;

import java.io.Serial;

public class CloseConnectionMessage extends NetMessage{

    @Serial
    private static final long serialVersionUID = 3448524243229177507L;
    public CloseConnectionMessage() {
        super(MessageType.CLOSE_CONNECTION);
    }
}