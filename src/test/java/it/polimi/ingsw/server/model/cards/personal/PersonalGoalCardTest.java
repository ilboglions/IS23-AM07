package it.polimi.ingsw.server.model.cards.personal;

import it.polimi.ingsw.server.model.coordinate.Coordinates;
import it.polimi.ingsw.server.model.exceptions.InvalidCoordinatesException;
import it.polimi.ingsw.server.model.tiles.ItemTile;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Class used to test PersonalGoalCard class
 */
class PersonalGoalCardTest {

    /**
     * This tests that all the exception are firing correctly, such as null values or empty arraylist passed
     */
    @Test
    @DisplayName("Test all the exceptions")
    void exceptionTest() {
        assertThrows(NullPointerException.class, ()->{
           PersonalGoalCard test = new PersonalGoalCard(null, new HashMap<>(), 0);
        });

        assertThrows(NullPointerException.class, ()->{
            PersonalGoalCard test = new PersonalGoalCard(new HashMap<>(), null, 1);
        });

        assertThrows(IllegalArgumentException.class, ()->{
            Map<Integer,Integer> testPointsReference = new HashMap<>();
            testPointsReference.put(1,1);

            PersonalGoalCard test = new PersonalGoalCard(new HashMap<>(), testPointsReference, 1);
        });

        assertThrows(IllegalArgumentException.class, ()->{
            Map<Coordinates, ItemTile> testPattern = new HashMap<>();
            testPattern.put(new Coordinates(1,1), ItemTile.BOOK);

            PersonalGoalCard test = new PersonalGoalCard(testPattern, new HashMap<>(), 1);
        });
    }

    /**
     * This tests if the initialization of PersonalGoalCard is done accordingly to the pattern and the pointsReference passed to the constructor
     * @throws InvalidCoordinatesException if the coordinates are out of range
     */
    @Test
    @DisplayName("Test that the personal card is instantiated correctly")
    void getBookshelf() throws InvalidCoordinatesException, RemoteException {
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

        PersonalGoalCard test = new PersonalGoalCard(testPattern, testStdPoints, 0);
        test.getID();

        for(Map.Entry<Coordinates, ItemTile> entry : testPattern.entrySet()) {
            assertTrue(test.getBookshelf().getItemTile(entry.getKey()).isPresent() &&
                    test.getBookshelf().getItemTile(entry.getKey()).get().equals(testPattern.get(entry.getKey())));
        }

        for(Map.Entry<Integer, Integer> entry : testStdPoints.entrySet()) {
            assertEquals(test.getPointsReference().get(entry.getKey()), entry.getValue());
        }

        Map<Coordinates, ItemTile> itemTileMap = test.getCardPattern();
        for(Map.Entry<Coordinates, ItemTile> entry : testPattern.entrySet()) {
            assertTrue(itemTileMap.containsKey(entry.getKey()));
            assertEquals(entry.getValue(), itemTileMap.get(entry.getKey()));
        }
    }
}