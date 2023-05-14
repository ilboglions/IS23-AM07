package it.polimi.ingsw.server.model.listeners;
import it.polimi.ingsw.server.model.chat.Message;
import it.polimi.ingsw.remoteInterfaces.ChatSubscriber;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * the chatListener is used to notify the client about the new messages posted
 */
public class ChatListener extends Listener<ChatSubscriber> {


    /**
     * when a new message is posted, the listener will notify the clients observers that are interested
     * @param msg the message that has been posted on the chat
     */
    public void onNewMessage(Message msg) {

        Set<ChatSubscriber> observers = this.getSubscribers();

        if( msg.getRecipient().isPresent() ){

            String recipient = msg.getRecipient().get();
            String sender = msg.getSender();
            List<ChatSubscriber> interestedObs =   observers.stream()
                                                    .filter(obs -> {
                                                        try {
                                                            return obs.getSubscriberUsername().equals(recipient) || obs.getSubscriberUsername().equals(sender);
                                                        } catch (RemoteException ignored) {

                                                        }
                                                        return false;
                                                    }).toList();
            interestedObs.forEach( ob -> {
                try {
                    ob.receiveMessage(msg.getSender(), msg.getContent(), true);
                } catch (RemoteException ignored) {

                }
            });
        } else{
            observers.forEach(obs -> {
                try {
                    obs.receiveMessage(msg.getSender(), msg.getContent(), false);
                } catch (RemoteException ignored) {

                }
            });
        }
    }
}
