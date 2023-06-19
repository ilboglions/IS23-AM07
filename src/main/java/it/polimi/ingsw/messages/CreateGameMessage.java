package it.polimi.ingsw.messages;

import java.io.Serial;

/**
 * This message represents the request to create a game
 */
public class CreateGameMessage extends NetMessage {
    @Serial
    private static final long serialVersionUID = -7527502624377210050L;

    private final int playerNumber;

    /**
     * Constrcutor of a CreateGameMessage
     * @param playerNumber number of player of the game
     */
    public CreateGameMessage(int playerNumber) {
        super(MessageType.CREATE_GAME);
        this.playerNumber = playerNumber;
    }

    /**
     * @return the number of player inserted
     */
    public int getPlayerNumber() {
        return playerNumber;
    }
}
