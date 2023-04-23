package it.polimi.ingsw.server.model.subscriber;


import it.polimi.ingsw.server.model.listeners.Listener;

import java.rmi.Remote;

/**
 * a listener observer is a class that observe a listener in order to receive a status update by a certain class listened
 */
public interface ListenerSubscriber extends Remote {
    /**
     * it is necessary to identify the username of the
     * observer that the client refers to
     * @return the username of the player that is observing the chat
     */
    String getSubscriberUsername();

}
