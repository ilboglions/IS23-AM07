package it.polimi.ingsw.model.player;

import it.polimi.ingsw.model.cards.personal.PersonalGoalCard;
import it.polimi.ingsw.model.coordinate.Coordinates;
import it.polimi.ingsw.model.exceptions.InvalidCoordinatesException;
import it.polimi.ingsw.model.exceptions.NotEnoughSpaceException;
import it.polimi.ingsw.model.tiles.ItemTile;
import it.polimi.ingsw.model.tokens.ScoringToken;
import it.polimi.ingsw.model.tokens.TokenPoint;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Class used to test Player class
 */
class PlayerTest {

    /**
     * This tests that the exceptions for null or invalid values work correctly
     */
    @Test
    @DisplayName("Test all the exceptions")
    void testException(){
        assertThrows(NullPointerException.class, ()->{
            Player test = new Player(null);
        });

        assertThrows(NullPointerException.class, ()->{
            Player test = new Player("test");
            test.updatePoints(null);
        });

        assertThrows(NullPointerException.class, ()->{
            Player test = new Player("test");
            test.addToken(null);
        });

        assertThrows(NullPointerException.class, ()->{
            Player test = new Player("test");
            test.assignPersonalCard(null);
        });

        assertThrows(IllegalArgumentException.class, ()->{
            Player test = new Player("test");
            test.updatePoints(new HashMap<>());
        });
    }

    /**
     * This tests all the getter and equals methods
     * @throws InvalidCoordinatesException if the coordinates are out of range
     */
    @Test
    @DisplayName("Test getter and equals")
    void testGetter() throws InvalidCoordinatesException {
        ScoringToken testToken = new ScoringToken(TokenPoint.EIGHT);
        Map<Integer,Integer> testPointsReference = new HashMap<>();
        Map<Coordinates, ItemTile> testPattern = new HashMap<>();

        testPattern.put(new Coordinates(1,1), ItemTile.BOOK);
        testPointsReference.put(1,1);

        PersonalGoalCard testCard = new PersonalGoalCard(testPattern, testPointsReference);

        Player test = new Player("test");
        test.addToken(testToken);
        test.assignPersonalCard(testCard);

        assertEquals(testCard, test.getPersonalCard());
        assertEquals(test.getTokenAcquired().get(0), testToken);
        assertEquals(1, test.getTokenAcquired().size());
        assertEquals("test", test.getUsername());
        assertEquals(0, test.getPoints());
        assertEquals(test, new Player("test"));
        assertNotEquals(test, new Player("notEquals"));
    }

    /**
     * This tests if the points are calculated correctly in case a player has not reached any goal
     * @throws InvalidCoordinatesException if the coordinates are out of range
     */
    @Test
    @DisplayName("Test zero points calculation")
    void testZeroPoints() throws InvalidCoordinatesException {
        Player test = new Player("test");
        Map<Integer,Integer> testPointsReference = new HashMap<>();
        Map<Coordinates, ItemTile> testPattern = new HashMap<>();

        testPattern.put(new Coordinates(1,1), ItemTile.BOOK);
        testPointsReference.put(1,1);

        PersonalGoalCard testCard = new PersonalGoalCard(testPattern, testPointsReference);

        test.assignPersonalCard(testCard);

        assertEquals(test.updatePoints(testPointsReference), test.getPoints());
        assertEquals(0, test.updatePoints(testPointsReference));
    }

    /**
     * This is a test for generic values of scoring tokens and personal goal reached
     * @throws NotEnoughSpaceException if there isn't enough space in the column to insert the tiles
     * @throws InvalidCoordinatesException if the coordinates are out of range
     */
    @Test
    @DisplayName("Test generic scoring token and personalCard points calculation")
    void testPoints() throws NotEnoughSpaceException, InvalidCoordinatesException {
        Player test = new Player("test");
        Map<Coordinates, ItemTile> testPattern = new HashMap<>();
        Map<Integer, Integer> testCardPointsReference = new HashMap<>();
        Map<Integer, Integer> testAdjacentPointsReference = new HashMap<>();

        testPattern.put(new Coordinates(2,0), ItemTile.CAT);
        testPattern.put(new Coordinates(5,1), ItemTile.TROPHY);
        testPattern.put(new Coordinates(2,2), ItemTile.GAME);
        testPattern.put(new Coordinates(1,4), ItemTile.BOOK);
        testPattern.put(new Coordinates(5,0), ItemTile.FRAME);

        testCardPointsReference.put(1,1);
        testCardPointsReference.put(2,2);
        testCardPointsReference.put(3,4);
        testCardPointsReference.put(4,6);

        testAdjacentPointsReference.put(0,0);

        PersonalGoalCard testCard = new PersonalGoalCard(testPattern, testCardPointsReference);

        test.addToken(new ScoringToken(TokenPoint.EIGHT));
        test.assignPersonalCard(testCard);

        ArrayList<ItemTile> testArray = new ArrayList<>();
        testArray.add(ItemTile.GAME);
        testArray.add(ItemTile.BOOK);
        testArray.add(ItemTile.CAT);

        test.getBookshelf().insertItemTile(0, testArray);
        test.getBookshelf().insertItemTile(4, testArray);

        assertEquals(test.updatePoints(testAdjacentPointsReference), test.getPoints());
        assertEquals(8 + 2, test.updatePoints(testAdjacentPointsReference)); //8 points for the scoring token, 2 for the personalGoalCard and 0 for adjacency groups
    }

    /**
     * This tests again scoring tokens and personal goal card points with a number of elements overlapping that is exceeding the maximum number of points
     * @throws NotEnoughSpaceException if there isn't enough space in the column to insert the tiles
     * @throws InvalidCoordinatesException if the coordinates are out of range
     */
    @Test
    @DisplayName("Test scoring token and personalCard points calculation exceeding max elements overlapped")
    void testPersonalCardPoints() throws NotEnoughSpaceException, InvalidCoordinatesException {
        Player test = new Player("test");
        Map<Coordinates, ItemTile> testPattern = new HashMap<>();
        Map<Integer, Integer> testCardPointsReference = new HashMap<>();
        Map<Integer, Integer> testAdjacentPointsReference = new HashMap<>();

        testPattern.put(new Coordinates(2,0), ItemTile.CAT);
        testPattern.put(new Coordinates(5,1), ItemTile.TROPHY);
        testPattern.put(new Coordinates(2,2), ItemTile.GAME);
        testPattern.put(new Coordinates(1,4), ItemTile.BOOK);
        testPattern.put(new Coordinates(5,0), ItemTile.FRAME);
        testPattern.put(new Coordinates(0,3), ItemTile.FRAME);
        testPattern.put(new Coordinates(1,3), ItemTile.FRAME);
        testPattern.put(new Coordinates(2,3), ItemTile.FRAME);

        testCardPointsReference.put(1,1);
        testCardPointsReference.put(2,2);
        testCardPointsReference.put(3,4);
        testCardPointsReference.put(4,6);

        testAdjacentPointsReference.put(0,0);

        PersonalGoalCard testCard = new PersonalGoalCard(testPattern, testCardPointsReference);

        test.addToken(new ScoringToken(TokenPoint.EIGHT));
        test.assignPersonalCard(testCard);

        ArrayList<ItemTile> testArray = new ArrayList<>();
        testArray.add(ItemTile.GAME);
        testArray.add(ItemTile.BOOK);
        testArray.add(ItemTile.CAT);

        test.getBookshelf().insertItemTile(0, testArray);
        test.getBookshelf().insertItemTile(4, testArray);

        testArray.clear();
        testArray.add(ItemTile.FRAME);
        testArray.add(ItemTile.FRAME);
        testArray.add(ItemTile.FRAME);

        test.getBookshelf().insertItemTile(3, testArray);

        testArray.clear();
        testArray.add(ItemTile.FRAME);

        test.getBookshelf().insertItemTile(3, testArray);

        assertEquals(test.updatePoints(testAdjacentPointsReference), test.getPoints());
        assertEquals(8 + 6, test.updatePoints(testAdjacentPointsReference)); //8 points for the scoring token, 8 for the personalGoalCard and 0 for adjacency groups
    }

    /**
     * This tests for the correct calculation of points for the adjacent ItemTile groups inside the PlayerBookshelf
     * @throws NotEnoughSpaceException if there isn't enough space in the column to insert the tiles
     * @throws InvalidCoordinatesException if the coordinates are out of range
     */
    @Test
    @DisplayName("Test adjacency points calculation")
    void testPointsAdjacency() throws NotEnoughSpaceException, InvalidCoordinatesException {
        Player test = new Player("test");
        Map<Integer,Integer> testCardPointsReference = new HashMap<>();
        Map<Coordinates, ItemTile> testPattern = new HashMap<>();
        Map<Integer, Integer> testAdjacentPointsReference = new HashMap<>();

        testPattern.put(new Coordinates(1,1), ItemTile.BOOK);
        testCardPointsReference.put(1,1);

        testAdjacentPointsReference.put(3,2);
        testAdjacentPointsReference.put(4,3);
        testAdjacentPointsReference.put(5,5);
        testAdjacentPointsReference.put(6,8);

        PersonalGoalCard testCard = new PersonalGoalCard(testPattern, testCardPointsReference);

        test.assignPersonalCard(testCard);

        ArrayList<ItemTile> testArray = new ArrayList<>();
        testArray.add(ItemTile.GAME);
        testArray.add(ItemTile.GAME);
        testArray.add(ItemTile.GAME);

        test.getBookshelf().insertItemTile(0, testArray);
        test.getBookshelf().insertItemTile(0, testArray);
        test.getBookshelf().insertItemTile(1, testArray);
        test.getBookshelf().insertItemTile(1, testArray);

        assertEquals(8, test.updatePoints(testAdjacentPointsReference)); //8 points for adjacency group of 12 of ItemTile.GAME

        testArray.clear();
        testArray.add(ItemTile.BOOK);
        testArray.add(ItemTile.BOOK);
        testArray.add(ItemTile.BOOK);

        test.getBookshelf().insertItemTile(4, testArray);

        assertEquals(8 + 2, test.updatePoints(testAdjacentPointsReference));  //8 points for adjacency group of 12 of ItemTile.GAME, 2 points for adjacency group of 3 ItemTile.BOOK

        testArray.clear();
        testArray.add(ItemTile.BOOK);
        testArray.add(ItemTile.PLANT);
        testArray.add(ItemTile.TROPHY);
        test.getBookshelf().insertItemTile(3, testArray);
        test.getBookshelf().insertItemTile(2, testArray);

        assertEquals(8 + 5, test.updatePoints(testAdjacentPointsReference));
        //8 points for adjacency group of 12 of ItemTile.GAME, 5 points for adjacency group of 5 ItemTile.BOOK, 0 points for adjacency of ItemTile.PLANT and ItemTile.TROPHY
        //because group size is smaller that the minimum group size set by the testAdjacentPointsReference Map
    }
}