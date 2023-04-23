package it.polimi.ingsw.server.model.subscriber;

/**
 * the interface that provides the method to be implemented by an observer of the chat service
 */
public interface ChatSubscriber extends ListenerSubscriber {



    /**
     * when a message for the player is posted, the listener will trigger this method
     * @param from the sender of the message
     * @param msg the message
     */
    void receiveMessage(String from, String msg);

}
