package it.polimi.ingsw.server.model.listeners;
import it.polimi.ingsw.GameState;
import it.polimi.ingsw.remoteInterfaces.GameSubscriber;
import it.polimi.ingsw.remoteInterfaces.RemoteCommonGoalCard;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class GameListener extends Listener<GameSubscriber> {

    public void onPlayerJoinGame(String username){
        Set<GameSubscriber> subscribers = this.getSubscribers();
        for (GameSubscriber sub : subscribers) {
            try {
                sub.notifyPlayerJoined(username);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }
    }
    public void onCommonCardDraw(String username, ArrayList<RemoteCommonGoalCard> commonGoalCards) {
        Set<GameSubscriber> subscribers = this.getSubscribers();
        for( GameSubscriber sub :  subscribers ){
            try {
                if(sub.getSubscriberUsername().equals(username)){
                    sub.notifyCommonGoalCards(commonGoalCards);
                }
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }
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
        subscribers.forEach(sub -> {
            try {
                sub.notifyPlayerInTurn(username);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void notifyPlayerCrashed(String userCrashed){
        Set<GameSubscriber> subscribers = this.getSubscribers();
        subscribers.forEach(sub -> {
            try {
                if(!sub.getSubscriberUsername().equals(userCrashed))
                    sub.notifyPlayerCrashed(userCrashed);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void notifyTurnOrder(ArrayList<String> playerOrder){
        Set<GameSubscriber> subscribers = this.getSubscribers();
        subscribers.forEach(sub -> {
            try {
                sub.notifyTurnOrder(playerOrder);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void notifyGameState(GameState status) {
        Set<GameSubscriber> subscribers = this.getSubscribers();
        subscribers.forEach(sub -> {
            try {
                sub.notifyGameStatus(status);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });
    }


    public void notifyAlreadyJoinedPlayers(Set<String> alreadyJoinedPlayers, String userToBeUpdated) {
        Set<GameSubscriber> subscribers = this.getSubscribers();
        for(GameSubscriber sub : subscribers){
            try {
                if( sub.getSubscriberUsername().equals(userToBeUpdated))
                    sub.notifyAlreadyJoinedPlayers(alreadyJoinedPlayers);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
