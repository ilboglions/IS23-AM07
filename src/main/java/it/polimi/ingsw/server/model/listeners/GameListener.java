package it.polimi.ingsw.server.model.listeners;
import it.polimi.ingsw.remoteInterfaces.GameSubscriber;
import it.polimi.ingsw.remoteInterfaces.RemoteCommonGoalCard;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class GameListener extends Listener<GameSubscriber> {
    public void onPlayerJoinGame(String username, ArrayList<RemoteCommonGoalCard> commonGoalCards) {
        Set<GameSubscriber> subscribers = this.getSubscribers();
        subscribers.forEach(sub -> {
            try {
                if(sub.getSubscriberUsername().equals(username)){
                    sub.notifyCommonGoalCards(commonGoalCards);
                } else {
                    sub.notifyPlayerJoined(username);
                }
            } catch (RemoteException ignored) {
            }
        });
    }

    public void onPlayerWins(String username, int points, Map<String,Integer> scoreboard){
        Set<GameSubscriber> subscribers = this.getSubscribers();
        subscribers.forEach(sub -> {
            try {
                sub.notifyWinningPlayer(username, points, scoreboard);
            } catch (RemoteException ignored) {
            }
        });
    }

    public void notifyPlayerInTurn(String username){
        Set<GameSubscriber> subscribers = this.getSubscribers();
        subscribers.forEach(sub -> sub.notifyPlayerInTurn(username));
    }

    public void notifyPlayerCrashed(String userCrashed){
        Set<GameSubscriber> subscribers = this.getSubscribers();
        subscribers.forEach(sub -> sub.notifyPlayerCrashed(userCrashed));
    }
}
