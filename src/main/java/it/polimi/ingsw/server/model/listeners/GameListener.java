package it.polimi.ingsw.server.model.listeners;
import it.polimi.ingsw.remoteInterfaces.GameSubscriber;
import it.polimi.ingsw.remoteInterfaces.RemoteCommonGoalCard;

import java.util.ArrayList;
import java.util.Set;

public class GameListener extends Listener<GameSubscriber> {
    public void onPlayerJoinGame(String username, ArrayList<RemoteCommonGoalCard> commonGoalCards){
        Set<GameSubscriber> subscribers = this.getSubscribers();
        subscribers.forEach(sub -> {
            if(sub.getSubscriberUsername().equals(username)){
                sub.notifyCommonGoalCards(commonGoalCards);
            } else {
                sub.notifyPlayerJoined(username);
            }
        });
    }

    public void onPlayerWins(String username, int points){
        Set<GameSubscriber> subscribers = this.getSubscribers();
        subscribers.forEach(sub -> sub.notifyWinningPlayer(username));
    }
}
