package it.polimi.ingsw.remoteInterfaces;

import it.polimi.ingsw.server.model.coordinate.Coordinates;
import it.polimi.ingsw.server.model.tiles.ItemTile;

import java.util.Map;
import java.util.Optional;

/**
 * the interface that provides the method to be implemented by an observer of the board
 */
public interface BoardSubscriber extends ListenerSubscriber {

     /**
      * when the board state changes, the listener of the board will trigger this method
      * @param tilesInBoard all the tiles that are in the board
      */
     void UpdateBoardStatus(Map<Coordinates, Optional<ItemTile>> tilesInBoard );


}
