package it.polimi.ingsw.messages;

import java.io.Serial;

/**
 * This message is used to confirm by the client that the reference to the game has been received successfully
 */
public class GameReceivedMessage extends NetMessage{
    @Serial
    private static final long serialVersionUID = -4268960731762666490L;

    private final Boolean errorOccurred;

    /**
     * Constructor of a GameReceivedMessage
     * @param errorOccurred true if an error occurred in the operation.
     */
    public GameReceivedMessage(Boolean errorOccurred) {
        super(MessageType.GAME_RECEIVED_MESSAGE);
        this.errorOccurred = errorOccurred;
    }

    /**
     *
     * @return true if an error occurred
     */
    public Boolean getErrorOccurred() {
        return errorOccurred;
    }
}
