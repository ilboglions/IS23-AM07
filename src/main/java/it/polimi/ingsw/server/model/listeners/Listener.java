package it.polimi.ingsw.server.model.listeners;

import it.polimi.ingsw.server.model.subscriber.ListenerSubscriber;

import java.rmi.Remote;
import java.util.HashSet;
import java.util.Set;

/**
 * the abstract and generic class that describe a listener
 * @param <T> the type of the ListenerObserver
 */
public abstract class Listener<T extends ListenerSubscriber> implements Remote {
    /**
     * the observers subscribed to the listener
     */
    private final Set<T> subscribers = new HashSet<>();

    /**
     * gets the set of observers
     * @return the set of the subscribed observers
     */
    protected Set<T> getSubscribers() {
        return subscribers;
    }

    /**
     * make it possible to insert a new observer to the listener
     * @param subscriber the observer to be added
     */
    public void addSubscriber(T subscriber) {
        subscribers.add(subscriber);
    }

}