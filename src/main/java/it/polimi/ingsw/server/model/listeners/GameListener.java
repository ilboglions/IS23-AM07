package it.polimi.ingsw.server.model.listeners;
import it.polimi.ingsw.remoteInterfaces.GameSubscriber;

import java.util.Set;

public class GameListener extends Listener<GameSubscriber> {
    public void onPlayerJoinGame(String username){
        Set<GameSubscriber> subscribers = this.getSubscribers();
        subscribers.forEach(sub -> sub.notifyPlayerJoined(username));
    }

    public void onPlayerWins(String username, int points){
        Set<GameSubscriber> subscribers = this.getSubscribers();
        subscribers.forEach(sub -> sub.notifyWinningPlayer(username));
    }
}
