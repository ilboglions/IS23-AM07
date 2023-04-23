package it.polimi.ingsw.remoteControllers;

import it.polimi.ingsw.server.model.coordinate.Coordinates;
import it.polimi.ingsw.server.model.tiles.ItemTile;

import java.io.Serializable;
import java.rmi.Remote;
import java.util.Map;

public interface RemotePersonalGoalCard extends Remote, Serializable {
    public Map<Integer, Integer> getPointsReference();

    public Map<Coordinates, ItemTile> getCardPattern();

}
