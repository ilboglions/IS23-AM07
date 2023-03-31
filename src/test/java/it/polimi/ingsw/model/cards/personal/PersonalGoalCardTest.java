package it.polimi.ingsw.model.cards.personal;

import it.polimi.ingsw.model.coordinate.Coordinates;
import it.polimi.ingsw.model.tiles.ItemTile;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class PersonalGoalCardTest {

    @Test
    @DisplayName("Test all the exceptions")
    void exceptionTest() {
        assertThrows(NullPointerException.class, ()->{
           PersonalGoalCard test = new PersonalGoalCard(null, new HashMap<>());
        });

        assertThrows(NullPointerException.class, ()->{
            PersonalGoalCard test = new PersonalGoalCard(new HashMap<>(), null);
        });
    }

    @Test
    @DisplayName("Test that the personal card is instantiated correctly")
    void getBookshelf() {
        Map<Coordinates, ItemTile> testPattern = new HashMap<>();
        Map<Integer, Integer> testStdPoints = new HashMap<>();

        testPattern.put(new Coordinates(0,0), ItemTile.CAT);
        testPattern.put(new Coordinates(5,4), ItemTile.TROPHY);
        testPattern.put(new Coordinates(2,2), ItemTile.GAME);
        testPattern.put(new Coordinates(0,4), ItemTile.BOOK);
        testPattern.put(new Coordinates(5,0), ItemTile.FRAME);

        testStdPoints.put(1,1);
        testStdPoints.put(3,5);
        testStdPoints.put(4,6);
        testStdPoints.put(6,8);

        PersonalGoalCard test = new PersonalGoalCard(testPattern, testStdPoints);

        for(Map.Entry<Coordinates, ItemTile> entry : testPattern.entrySet()) {
            assertTrue(test.getBookshelf().getItemTile(entry.getKey()).isPresent() &&
                    test.getBookshelf().getItemTile(entry.getKey()).get().equals(testPattern.get(entry.getKey())));
        }

        for(Map.Entry<Integer, Integer> entry : testStdPoints.entrySet()) {
            assertEquals(test.getPointsReference().get(entry.getKey()), entry.getValue());
        }
    }
}