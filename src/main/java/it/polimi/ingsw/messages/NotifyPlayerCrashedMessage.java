package it.polimi.ingsw.messages;

import java.io.Serial;

public class NotifyPlayerCrashedMessage extends NetMessage{
    @Serial
    private static final long serialVersionUID = -1238285077168825085L;

    private final String userCrashed;

    public NotifyPlayerCrashedMessage(String userCrashed) {
        super(MessageType.NOTIFY_PLAYER_CRASHED);
        this.userCrashed = userCrashed;
    }

    public String getUserCrashed() {
        return userCrashed;
    }
}
