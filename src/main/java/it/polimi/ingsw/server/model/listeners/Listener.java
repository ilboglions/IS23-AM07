package it.polimi.ingsw.server.model.listeners;

import it.polimi.ingsw.remoteInterfaces.ListenerSubscriber;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

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

    /**
     * removes all the subscribers that contains the username
     * @param username the username of the subscriber
     */
    public void removeSubscriber(String username){
        Set<T> subToRemove = subscribers.stream().filter( s -> {
            try {
                return s.getSubscriberUsername().equals(username);
            } catch (RemoteException ignored) {
            }
            return false;
        }).collect(Collectors.toSet());
        this.subscribers.removeAll(subToRemove);
    }

}