package it.polimi.ingsw.remoteInterfaces;


import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * a listener observer is a class that observe a listener in order to receive a status update by a certain class listened
 */
public interface ListenerSubscriber extends Remote, Serializable {
    /**
     * it is necessary to identify the username of the
     * observer that the client refers to
     * @return the username of the player that is observing the chat
     */
    String getSubscriberUsername() throws RemoteException;

}
