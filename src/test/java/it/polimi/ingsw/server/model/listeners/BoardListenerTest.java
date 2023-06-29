package it.polimi.ingsw.server.model.listeners;

import it.polimi.ingsw.server.model.coordinate.Coordinates;
import it.polimi.ingsw.server.model.tiles.ItemTile;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Tests for the class BoardListener
 */
public class BoardListenerTest {

    /**
     * Test to call all the methods of the listener
     */
    @Test
    @DisplayName("Test all the methods")
    void fullTest(){
        BoardListener test = new BoardListener();
        GeneralSubscriber sub1 = new GeneralSubscriber("sub1");
        GeneralSubscriber sub2 = new GeneralSubscriber("sub2");

        test.addSubscriber(sub1);
        test.addSubscriber(sub2);

        Map<Coordinates, ItemTile> tilesInBoard = new HashMap<>();

        test.onBoardChange(tilesInBoard);
        test.triggerListener("sub1", tilesInBoard);
    }
}