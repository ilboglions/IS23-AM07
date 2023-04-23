package it.polimi.ingsw.messages;

import java.io.Serial;

public class ClientActiveMessage extends NetMessage {
    @Serial
    private static final long serialVersionUID = 3448524243229177507L;

    ClientActiveMessage(String username) {
        super(username, MessageType.CLIENT_ACTIVE);
    }
}
