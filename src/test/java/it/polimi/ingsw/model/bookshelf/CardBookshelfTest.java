package it.polimi.ingsw.model.bookshelf;

import it.polimi.ingsw.model.coordinate.Coordinates;
import it.polimi.ingsw.model.exceptions.InvalidCoordinatesException;
import it.polimi.ingsw.model.exceptions.NotEnoughSpaceException;
import it.polimi.ingsw.model.tiles.ItemTile;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CardBookshelfTest {

    @Test
    @DisplayName("Test all the exceptions")
    void testExceptions() {
        CardBookshelf test = new CardBookshelf(new HashMap<>());

        assertThrows(NullPointerException.class, ()->{
            test.nElementsOverlapped(null);
        });

        assertThrows(NullPointerException.class, ()->{
            CardBookshelf testNull = new CardBookshelf(null);
        });

        assertThrows(NullPointerException.class, ()->{
            test.getItemTile(null);
        });

        assertThrows(IndexOutOfBoundsException.class, ()->{
            test.getItemTile(new Coordinates(6,0));
        });

        assertThrows(IndexOutOfBoundsException.class, ()->{
            test.getItemTile(new Coordinates(0,5));
        });
    }

    @Test
    @DisplayName("Test for correct pattern initialization")
    void testInit() throws InvalidCoordinatesException {
        Map<Coordinates, ItemTile> testPattern = new HashMap<>();

        testPattern.put(new Coordinates(0,0), ItemTile.GAME);
        testPattern.put(new Coordinates(5,4), ItemTile.CAT);
        testPattern.put(new Coordinates(3,3), ItemTile.TROPHY);
        testPattern.put(new Coordinates(2,0), ItemTile.PLANT);
        testPattern.put(new Coordinates(0,4), ItemTile.GAME);

        CardBookshelf test = new CardBookshelf(testPattern);

        for(Map.Entry<Coordinates, ItemTile> entry : testPattern.entrySet()) {
            assertTrue(test.getItemTile(entry.getKey()).isPresent() && entry.getValue().equals(test.getItemTile(entry.getKey()).get()));
        }

        Map<ItemTile, Integer> insertedTiles = new HashMap<>();
        for(ItemTile itemTile : testPattern.values()) {
            if(insertedTiles.containsKey(itemTile)) {
                insertedTiles.put(itemTile, insertedTiles.get(itemTile) + 1);
            } else {
                insertedTiles.put(itemTile, 1);
            }
        }

        Map<ItemTile, Integer> allTiles = test.getAllItemTiles();
        for(Map.Entry<ItemTile, Integer> entry : allTiles.entrySet()) {
            assertEquals(entry.getValue(), insertedTiles.get(entry.getKey()));
        }
    }

    @Test
    @DisplayName("Test overlapping elements")
    void testOverlapped() throws InvalidCoordinatesException {
        Map<Coordinates, ItemTile> testPattern = new HashMap<>();

        testPattern.put(new Coordinates(0,0), ItemTile.GAME);
        testPattern.put(new Coordinates(5,4), ItemTile.CAT);
        testPattern.put(new Coordinates(3,3), ItemTile.TROPHY);
        testPattern.put(new Coordinates(2,0), ItemTile.PLANT);
        testPattern.put(new Coordinates(0,4), ItemTile.GAME);

        CardBookshelf test = new CardBookshelf(testPattern);

        assertEquals(testPattern.size(), test.nElementsOverlapped(test));
        assertEquals(0, test.nElementsOverlapped(new CardBookshelf(new HashMap<>())));

        Map<Coordinates, ItemTile> secondTestPattern = new HashMap<>();
        secondTestPattern.put(new Coordinates(0,0), ItemTile.GAME);
        CardBookshelf secondBookshelf = new CardBookshelf(secondTestPattern);

        assertEquals(1, test.nElementsOverlapped(secondBookshelf));
    }

}