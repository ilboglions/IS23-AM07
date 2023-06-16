package it.polimi.ingsw.messages;

import it.polimi.ingsw.remoteInterfaces.RemoteCommonGoalCard;

import java.io.Serial;
import java.util.ArrayList;

/**
 * This message transfers the communication of an update about the CommonGoalCards of the game
 */
public class CommonGoalCardsUpdateMessage extends NetMessage {
    @Serial
    private static final long serialVersionUID = -1987225485286613586L;

    private final ArrayList<RemoteCommonGoalCard> commonGoalCards;

    /**
     * Constructor of a CommonGoalCardsUpdateMessage
     * @param commonGoalCards list of the updated RemoteCommonGoalCards
     */
    public CommonGoalCardsUpdateMessage(ArrayList<RemoteCommonGoalCard> commonGoalCards) {
        super(MessageType.COMMON_CARDS_UPDATE);
        this.commonGoalCards = commonGoalCards;
    }

    /**
     *
     * @return a list of the updated RemoteCommonGoalCards
     */
    public ArrayList<RemoteCommonGoalCard> getCommonGoalCards() {
        return commonGoalCards;
    }
}
