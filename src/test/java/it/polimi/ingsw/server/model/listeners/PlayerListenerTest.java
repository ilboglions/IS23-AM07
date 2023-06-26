package it.polimi.ingsw.server.model.listeners;

import it.polimi.ingsw.server.model.cards.personal.PersonalGoalCard;
import it.polimi.ingsw.server.model.coordinate.Coordinates;
import it.polimi.ingsw.server.model.exceptions.InvalidCoordinatesException;
import it.polimi.ingsw.server.model.tiles.ItemTile;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class PlayerListenerTest {
    /**
     * Test to call all the methods of the listener
     */
    @Test
    @DisplayName("Test all the methods")
    void fullTest() throws RemoteException, InvalidCoordinatesException {
        PlayerListener test = new PlayerListener();
        GeneralSubscriber sub1 = new GeneralSubscriber("sub1");
        GeneralSubscriber sub2 = new GeneralSubscriber("sub2");

        test.addSubscriber(sub1);
        test.addSubscriber(sub2);

        Map<Coordinates, ItemTile> testPattern = new HashMap<>();
        Map<Integer, Integer> testStdPoints = new HashMap<>();

        testPattern.put(new Coordinates(0,0), ItemTile.CAT);
        testPattern.put(new Coordinates(5,4), ItemTile.TROPHY);
        testPattern.put(new Coordinates(2,2), ItemTile.GAME);
        testPattern.put(new Coordinates(0,4), ItemTile.BOOK);
        testPattern.put(new Coordinates(5,0), ItemTile.FRAME);

        testStdPoints.put(1,1);
        testStdPoints.put(2,2);
        testStdPoints.put(3,4);
        testStdPoints.put(4,6);
        testStdPoints.put(5,9);
        testStdPoints.put(6,12);

        test.onPointsUpdate("sub1", 0, 0);
        test.onPersonalGoalCardAssigned("sub1", new PersonalGoalCard(testPattern, testStdPoints, 0));
        test.onTokenPointAcquired("sub1", new ArrayList<>());
        test.triggerListener("sub1", "sub1", 0, new ArrayList<>(), new PersonalGoalCard(testPattern, testStdPoints, 0));
    }

}