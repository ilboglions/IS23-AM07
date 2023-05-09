package it.polimi.ingsw.server.model.bookshelf;

import it.polimi.ingsw.server.model.coordinate.Coordinates;
import it.polimi.ingsw.server.model.exceptions.InvalidCoordinatesException;
import it.polimi.ingsw.server.model.exceptions.NotEnoughSpaceException;
import it.polimi.ingsw.server.model.player.Player;
import it.polimi.ingsw.server.model.tiles.ItemTile;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Class used to test PlayerBookshelf class
 */
class PlayerBookshelfTest {

    /**
     * This tests that all the exception are firing correctly, such as null values, coordinates out of range or not enough space in a column to add tiles
     */
    @Test
    @DisplayName("Test all the exceptions")
    void testExceptions() {
        PlayerBookshelf test = new PlayerBookshelf(new Player("mario"));

        assertThrows(NotEnoughSpaceException.class, ()->{
           ArrayList<ItemTile> testArray = new ArrayList<>();

           testArray.add(ItemTile.GAME);
           testArray.add(ItemTile.GAME);
           testArray.add(ItemTile.GAME);

           test.insertItemTile(0, testArray);
           test.insertItemTile(0, testArray);
           test.insertItemTile(0, testArray);
        });

        assertThrows(IllegalArgumentException.class, ()->{
            ArrayList<ItemTile> testArray = new ArrayList<>();

            testArray.add(ItemTile.GAME);
            testArray.add(ItemTile.GAME);
            testArray.add(ItemTile.GAME);
            testArray.add(ItemTile.GAME);

            test.insertItemTile(0, testArray);
        });

        assertThrows(IllegalArgumentException.class, ()->{
            test.insertItemTile(-1, new ArrayList<>());
        });

        assertThrows(IllegalArgumentException.class, ()->{
            test.insertItemTile(10, new ArrayList<>());
        });

        assertThrows(NullPointerException.class, ()->{
            test.insertItemTile(0, null);
        });

        assertThrows(NullPointerException.class, ()->{
            test.nElementsOverlapped(null);
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

    /**
     * This tests if the method checkComplete works correctly if the bookshelf is complete or not
     * @throws NotEnoughSpaceException  if there is not enough space in the selected column
     * @throws InvalidCoordinatesException if the coordinates are out of range
     */
    @Test
    @DisplayName("Test for a complete bookshelf")
    void testFull() throws NotEnoughSpaceException, InvalidCoordinatesException {
        PlayerBookshelf test = new PlayerBookshelf(new Player("Mario"));
        ArrayList<ItemTile> testArray = new ArrayList<>();

        testArray.add(ItemTile.GAME);
        testArray.add(ItemTile.BOOK);
        testArray.add(ItemTile.CAT);

        test.insertItemTile(0, testArray);
        test.insertItemTile(0, testArray);
        test.insertItemTile(1, testArray);
        test.insertItemTile(1, testArray);
        test.insertItemTile(2, testArray);
        test.insertItemTile(2, testArray);
        test.insertItemTile(3, testArray);
        test.insertItemTile(3, testArray);
        test.insertItemTile(4, testArray);
        test.insertItemTile(4, testArray);

        assertTrue(test.checkComplete());

       for(int i=0; i<test.getRows(); i++) {
           for(int j=0; j<test.getColumns(); j++) {
               assertTrue(test.getItemTile(new Coordinates(i, j)).isPresent() &&
                       test.getItemTile(new Coordinates(i, j)).get().equals(testArray.get(i % testArray.size())));
           }
       }

       Map<ItemTile, Integer> allTiles = test.getAllItemTiles();
       for(Map.Entry<ItemTile, Integer> entry : allTiles.entrySet()) {
           if(testArray.contains(entry.getKey()))
                assertEquals(2*5, entry.getValue());
           else
               assertEquals(0, entry.getValue());
       }
    }

    /**
     * This tests if retrieving tiles and checking if the bookshelf is completed after adding an empty arraylist of ItemTile is working correctly
     * @throws NotEnoughSpaceException if there is not enough space in the selected column
     * @throws InvalidCoordinatesException if the coordinates are out of range
     */
    @Test
    @DisplayName("Test empty Arraylist of tiles")
    void testEmpty() throws NotEnoughSpaceException, InvalidCoordinatesException {
        PlayerBookshelf test = new PlayerBookshelf(new Player("mario"));

        test.insertItemTile(0, new ArrayList<>());

        for(int i=0; i<test.getRows(); i++) {
            for(int j=0; j<test.getColumns(); j++) {
                assertTrue(test.getItemTile(new Coordinates(i, j)).isEmpty());
            }
        }

        Map<ItemTile, Integer> allTiles = test.getAllItemTiles();
        for(Map.Entry<ItemTile, Integer> entry : allTiles.entrySet()) {
            assertEquals(0, entry.getValue());
        }
        assertFalse(test.checkComplete());
    }

    /**
     * This tests if the number of ItemTile in the same position in two bookshelf are counted correctly with the nElementsOverlapped() method
     * @throws NotEnoughSpaceException if there is not enough space in the selected column
     */
    @Test
    @DisplayName("Test overlapping elements")
    void testOverlapped() throws NotEnoughSpaceException {
        PlayerBookshelf test = new PlayerBookshelf(new Player("mario"));
        ArrayList<ItemTile> testArray = new ArrayList<>();

        testArray.add(ItemTile.GAME);
        testArray.add(ItemTile.BOOK);
        testArray.add(ItemTile.CAT);

        test.insertItemTile(0, testArray);
        test.insertItemTile(0, testArray);
        test.insertItemTile(1, testArray);
        test.insertItemTile(1, testArray);
        test.insertItemTile(2, testArray);
        test.insertItemTile(2, testArray);
        test.insertItemTile(3, testArray);
        test.insertItemTile(3, testArray);
        test.insertItemTile(4, testArray);
        test.insertItemTile(4, testArray);

        assertEquals(testArray.size() * 2 * 5, test.nElementsOverlapped(test));
        assertEquals(0, test.nElementsOverlapped(new PlayerBookshelf(new Player("mario"))));

        PlayerBookshelf secondBookshelf = new PlayerBookshelf(new Player("mario"));
        ArrayList<ItemTile> testSingle = new ArrayList<>();
        testSingle.add(ItemTile.GAME);
        secondBookshelf.insertItemTile(0, testSingle);

        assertEquals(1, test.nElementsOverlapped(secondBookshelf));
    }
}