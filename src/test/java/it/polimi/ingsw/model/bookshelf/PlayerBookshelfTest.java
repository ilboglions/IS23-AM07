package it.polimi.ingsw.model.bookshelf;

import it.polimi.ingsw.model.coordinate.Coordinates;
import it.polimi.ingsw.model.exceptions.NotEnoughSpaceException;
import it.polimi.ingsw.model.tiles.ItemTile;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class PlayerBookshelfTest {

    @Test
    @DisplayName("Test all the exceptions")
    void testExceptions() {
        PlayerBookshelf test = new PlayerBookshelf();

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

    @Test
    @DisplayName("Test for a complete bookshelf")
    void testFull() throws NotEnoughSpaceException {
        PlayerBookshelf test = new PlayerBookshelf();
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
           assertEquals(entry.getValue(), 2 * 5);
       }
    }

    @Test
    @DisplayName("Test empty Arraylist of tiles")
    void testEmpty() throws NotEnoughSpaceException {
        PlayerBookshelf test = new PlayerBookshelf();

        test.insertItemTile(0, new ArrayList<>());

        for(int i=0; i<test.getRows(); i++) {
            for(int j=0; j<test.getColumns(); j++) {
                assertTrue(test.getItemTile(new Coordinates(i, j)).isEmpty());
            }
        }

        assertTrue(test.getAllItemTiles().isEmpty());
        assertFalse(test.checkComplete());
    }

    @Test
    @DisplayName("Test overlapping elements")
    void testOverlapped() throws NotEnoughSpaceException {
        PlayerBookshelf test = new PlayerBookshelf();
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
        assertEquals(0, test.nElementsOverlapped(new PlayerBookshelf()));

        PlayerBookshelf secondBookshelf = new PlayerBookshelf();
        ArrayList<ItemTile> testSingle = new ArrayList<>();
        testSingle.add(ItemTile.GAME);
        secondBookshelf.insertItemTile(0, testSingle);

        assertEquals(1, test.nElementsOverlapped(secondBookshelf));
    }
}