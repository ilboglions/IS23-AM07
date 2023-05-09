package it.polimi.ingsw.messages;

import java.io.Serial;

public class NotifyPlayerCrashed extends NetMessage{
    @Serial
    private static final long serialVersionUID = -1238285077168825085L;

    private final String userCrashed;

    public NotifyPlayerCrashed(String userCrashed) {
        super(MessageType.NOTIFY_PLAYER_CRASHED);
        this.userCrashed = userCrashed;
    }

    public String getUserCrashed() {
        return userCrashed;
    }
}
