package it.polimi.ingsw.remoteInterfaces;

import java.rmi.RemoteException;

/**
 * the interface that provides the method to be implemented by an observer of the chat service
 */
public interface ChatSubscriber extends ListenerSubscriber {



    /**
     * when a message for the player is posted, the listener will trigger this method
     * @param from the sender of the message
     * @param recipient the recipient of the message
     * @param msg the message
     * @throws RemoteException RMI Exception
     */
    void receiveMessage(String from, String recipient, String msg) throws RemoteException;

    void receiveMessage(String from, String msg) throws RemoteException;
}
