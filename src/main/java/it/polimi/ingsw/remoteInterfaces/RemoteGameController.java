package it.polimi.ingsw.remoteInterfaces;
import it.polimi.ingsw.server.model.coordinate.Coordinates;
import it.polimi.ingsw.server.model.exceptions.PlayerNotFoundException;

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

    void subscribeToListener(PlayerSubscriber subscriber) throws RemoteException;

    void subscribeToListener(GameSubscriber subscriber) throws RemoteException;

    void handleCrashedPlayer(String username) throws RemoteException, PlayerNotFoundException;

    void handleRejoinedPlayer(String username) throws RemoteException, PlayerNotFoundException;
}
