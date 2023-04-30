package it.polimi.ingsw.messages;

import it.polimi.ingsw.remoteInterfaces.RemotePersonalGoalCard;

public class PersonalGoalCardUpdateMessage extends NetMessage{
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
