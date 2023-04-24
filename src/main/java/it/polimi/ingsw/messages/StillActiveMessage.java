package it.polimi.ingsw.messages;

import java.io.Serial;

public class StillActiveMessage extends NetMessage {
    @Serial
    private static final long serialVersionUID = -5002383024317087494L;

    StillActiveMessage() {
        super(MessageType.STILL_ACTIVE);
    }
}
