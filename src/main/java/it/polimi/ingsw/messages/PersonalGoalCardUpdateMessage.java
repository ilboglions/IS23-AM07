package it.polimi.ingsw.messages;

import it.polimi.ingsw.remoteInterfaces.RemotePersonalGoalCard;

import java.io.Serial;

public class PersonalGoalCardUpdateMessage extends NetMessage{
    @Serial
    private static final long serialVersionUID = 1045258623521239540L;
    private final String player;
    private final RemotePersonalGoalCard card;
    public PersonalGoalCardUpdateMessage(String player, RemotePersonalGoalCard card) {
        super(MessageType.PERSONAL_CARD_UPDATE);
        this.player = player;
        this.card = card;
    }

    public String getPlayer() {
        return player;
    }

    public RemotePersonalGoalCard getCard() {
        return card;
    }
}
