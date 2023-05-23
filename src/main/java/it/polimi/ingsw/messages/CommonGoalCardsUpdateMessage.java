package it.polimi.ingsw.messages;

import it.polimi.ingsw.remoteInterfaces.RemoteCommonGoalCard;

import java.io.Serial;
import java.util.ArrayList;

public class CommonGoalCardsUpdateMessage extends NetMessage {
    @Serial
    private static final long serialVersionUID = -1987225485286613586L;

    private final ArrayList<RemoteCommonGoalCard> commonGoalCards;

    public CommonGoalCardsUpdateMessage(ArrayList<RemoteCommonGoalCard> commonGoalCards) {
        super(MessageType.COMMON_CARDS_UPDATE);
        this.commonGoalCards = commonGoalCards;
    }

    public ArrayList<RemoteCommonGoalCard> getCommonGoalCards() {
        return commonGoalCards;
    }
}
