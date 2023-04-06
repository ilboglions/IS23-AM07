package it.polimi.ingsw.model.observers;

/**
 * the interface that provides the method to be implemented by an observer of the chat service
 */
public interface ChatObserver extends ListenerObserver {

    /**
     * because of not all the messages are shared for all the players, it is necessary to identify the username of the
     * observer that the client refers to
     * @return the username of the player that is observing the chat
     */
    String getObserverUsername();

    /**
     * when a message for the player is posted, the listener will trigger this method
     * @param from the sender of the message
     * @param msg the message
     */
    void receiveMessage(String from, String msg);

}
