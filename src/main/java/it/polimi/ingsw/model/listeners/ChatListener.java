package it.polimi.ingsw.model.listeners;
import it.polimi.ingsw.model.chat.Message;
import it.polimi.ingsw.model.observers.ChatObserver;

import java.util.Optional;
import java.util.Set;

/**
 * the chatListener is used to notify the client about the new messages posted
 */
public class ChatListener extends Listener<ChatObserver> {


    /**
     * when a new message is posted, the listener will notify the clients observers that are interested
     * @param msg the message that has been posted on the chat
     */
    public void onNewMessage(Message msg) {

        Set<ChatObserver> observers = this.getObservers();

        if( msg.getRecipient().isPresent() ){

            String recipient = msg.getRecipient().get();
            Optional<ChatObserver> interestedOb =   observers.stream()
                                                    .filter(obs -> obs.getObserverUsername().equals(recipient))
                                                    .findFirst();
            interestedOb.ifPresent( ob -> ob.receiveMessage(msg.getSender(), msg.getContent()));

        } else{
            observers.stream()
                    .filter( obs -> !obs.getObserverUsername().equals(msg.getSender()))
                    .forEach( obs -> obs.receiveMessage(msg.getSender(), msg.getContent()));
        }
    }
}
