package it.polimi.ingsw.remoteInterfaces;

import it.polimi.ingsw.server.model.coordinate.Coordinates;
import it.polimi.ingsw.server.model.tiles.ItemTile;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

/**
 * This interface specifies the methods exposed by a PersonalGoalCard either over RMI or TCP
 */
public interface RemotePersonalGoalCard extends Remote, Serializable {

    /**
     *
     * @return the serial ID of this card
     * @throws RemoteException RMI Exception
     */
    int getID() throws RemoteException;
    /**
     * a simple getter method that returns the point reference of the card
     * @return the points reference used to evaluate the points acquired
     * @throws RemoteException RMI Exception
     */
    Map<Integer, Integer> getPointsReference() throws RemoteException;

    /**
     * @return the pattern associated to this card
     * @throws RemoteException RMI Exception
     */
    Map<Coordinates, ItemTile> getCardPattern() throws RemoteException;

}
