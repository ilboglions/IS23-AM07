package it.polimi.ingsw.messages;

import it.polimi.ingsw.remoteInterfaces.RemoteCommonGoalCard;
import it.polimi.ingsw.remoteInterfaces.RemotePersonalGoalCard;

import java.io.Serial;
import java.util.ArrayList;

public class UserGameCards extends NetMessage {
    @Serial
    private static final long serialVersionUID = -1987225485286613586L;

    private final ArrayList<RemoteCommonGoalCard> commonGoalCards;
    private final RemotePersonalGoalCard personalGoalCard;
    private final String details;

    UserGameCards(ArrayList<RemoteCommonGoalCard> commonGoalCards, RemotePersonalGoalCard personalGoalCard, String details) {
        super(MessageType.USER_GAME_CARDS);
        this.commonGoalCards = commonGoalCards;
        this.personalGoalCard = personalGoalCard;
        this.details = details;
    }

    public ArrayList<RemoteCommonGoalCard> getCommonGoalCards() {
        return commonGoalCards;
    }

    public RemotePersonalGoalCard getPersonalGoalCard() {
        return personalGoalCard;
    }

    public String getDetails() {
        return details;
    }
}
