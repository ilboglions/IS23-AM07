package it.polimi.ingsw.remoteInterfaces;

import it.polimi.ingsw.server.model.coordinate.Coordinates;
import it.polimi.ingsw.server.model.tiles.ItemTile;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

public interface RemotePersonalGoalCard extends Remote, Serializable {
    Map<Integer, Integer> getPointsReference() throws RemoteException;

    Map<Coordinates, ItemTile> getCardPattern() throws RemoteException;

}
