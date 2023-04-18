package it.polimi.ingsw.server.model.listeners;

import it.polimi.ingsw.server.model.observers.ListenerObserver;

import java.util.HashSet;
import java.util.Set;

/**
 * the abstract and generic class that describe a listener
 * @param <T> the type of the ListenerObserver
 */
public abstract class Listener<T extends ListenerObserver> {
    /**
     * the observers subscribed to the listener
     */
    private final Set<T> observers = new HashSet<>();

    /**
     * gets the set of observers
     * @return the set of the subscribed observers
     */
    protected Set<T> getObservers() {
        return observers;
    }

    /**
     * make it possible to insert a new observer to the listener
     * @param observer the observer to be added
     */
    public void addObserver(T observer) {
        observers.add(observer);
    }

}