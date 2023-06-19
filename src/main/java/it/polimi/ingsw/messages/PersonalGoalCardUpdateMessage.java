package it.polimi.ingsw.messages;

import it.polimi.ingsw.remoteInterfaces.RemotePersonalGoalCard;

import java.io.Serial;

/**
 * This message notifies to the client an update in the personal goal card
 */
public class PersonalGoalCardUpdateMessage extends NetMessage{
    @Serial
    private static final long serialVersionUID = 1045258623521239540L;
    private final String player;
    private final RemotePersonalGoalCard card;

    /**
     * Constructor of a PersonalGoalCardUpdateMessage
     * @param player owner of the PersonalGoalCard
     * @param card updated RemotePersonalGoalCard
     */
    public PersonalGoalCardUpdateMessage(String player, RemotePersonalGoalCard card) {
        super(MessageType.PERSONAL_CARD_UPDATE);
        this.player = player;
        this.card = card;
    }

    /**
     *
     * @return the owner of the card
     */
    public String getPlayer() {
        return player;
    }

    /**
     *
     * @return the updated RemotePersonalGoalCard
     */
    public RemotePersonalGoalCard getCard() {
        return card;
    }
}
