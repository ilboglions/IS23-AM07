package it.polimi.ingsw.remoteControllers;
import it.polimi.ingsw.server.model.coordinate.Coordinates;
import it.polimi.ingsw.server.model.subscriber.BoardSubscriber;
import it.polimi.ingsw.server.model.subscriber.BookshelfSubscriber;
import it.polimi.ingsw.server.model.subscriber.ChatSubscriber;
import it.polimi.ingsw.server.model.subscriber.PlayerSubscriber;

import java.rmi.*;
import java.util.ArrayList;

public interface RemoteGameController extends Remote{

    boolean checkValidRetrieve(String player, ArrayList<Coordinates> coords) throws RemoteException;

    boolean moveTiles(String player, ArrayList<Coordinates> source, int column) throws RemoteException;

    boolean postBroadCastMessage(String player, String message) throws RemoteException;

    boolean postDirectMessage(String player, String receiver,String message) throws RemoteException;

    void subscribeToListener(BoardSubscriber subscriber) throws RemoteException;

    void subscribeToListener(BookshelfSubscriber subscriber) throws RemoteException;

    void subscribeToListener(ChatSubscriber subscriber) throws RemoteException;

    void subscribeToListener(PlayerSubscriber subscriber);


}
