package it.polimi.ingsw.messages;

import java.io.Serial;

/**
 * This message notifies a client that another player crashed
 */
public class NotifyPlayerCrashedMessage extends NetMessage{
    @Serial
    private static final long serialVersionUID = -1238285077168825085L;

    private final String userCrashed;

    /**
     * Constructor of a NotifyPlayerCrashedMessage
     * @param userCrashed username of the crashed player
     */
    public NotifyPlayerCrashedMessage(String userCrashed) {
        super(MessageType.NOTIFY_PLAYER_CRASHED);
        this.userCrashed = userCrashed;
    }

    /**
     *
     * @return the username of the crashed player
     */
    public String getUserCrashed() {
        return userCrashed;
    }
}
