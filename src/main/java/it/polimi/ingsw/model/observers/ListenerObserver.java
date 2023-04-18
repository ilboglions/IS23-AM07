package it.polimi.ingsw.model.observers;

import it.polimi.ingsw.model.listeners.Listener;

import java.rmi.server.UnicastRemoteObject;

/**
 * a listener observer is a class that observe a listener in order to receive a status update by a certain class listened
 */
public interface ListenerObserver {
    /**
     * make it possible to subscribe the observer to a certain listener
     * @param listener the listener to be subscribed
     * @return true, if the subscription worked out, false otherwise
     */
    boolean subscribeToListener(Listener listener );
}
