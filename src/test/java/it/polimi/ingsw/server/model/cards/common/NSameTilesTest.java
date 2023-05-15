package it.polimi.ingsw.server.model.cards.common;

import it.polimi.ingsw.server.model.bookshelf.Bookshelf;
import it.polimi.ingsw.server.model.bookshelf.CardBookshelf;
import it.polimi.ingsw.server.model.coordinate.Coordinates;
import it.polimi.ingsw.server.model.exceptions.InvalidCoordinatesException;
import it.polimi.ingsw.server.model.exceptions.NegativeFieldException;
import it.polimi.ingsw.server.model.exceptions.NotEnoughSpaceException;
import it.polimi.ingsw.server.model.exceptions.PlayersNumberOutOfRange;
import it.polimi.ingsw.server.model.tiles.ItemTile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class NSameTilesTest {
    private NsameTiles card;
    /**
     * Testing the costructor of NsameTiles. Testing on costraint about players and positive arguments
     * @throws PlayersNumberOutOfRange
     * @throws NegativeFieldException
     */
    @BeforeEach
    @DisplayName("Card creation test")
    void  createCard() throws PlayersNumberOutOfRange, NegativeFieldException, RemoteException {
        assertThrows(PlayersNumberOutOfRange.class, () -> {
            new NsameTiles(0,"description_test", CommonCardType.CORNERS ,3);});
        assertThrows(PlayersNumberOutOfRange.class, () -> {
            new NsameTiles(5,"description_test", CommonCardType.CORNERS,3);});
        assertThrows(NegativeFieldException.class, () -> {
            new NsameTiles(3,"description_test",CommonCardType.CORNERS,-2);});
        card = new NsameTiles(2,"description_test", CommonCardType.CORNERS,5);
    }

    @Test
    @DisplayName("verifyCostraint Tester")
    void verifyCostraintTest() throws InvalidCoordinatesException, NotEnoughSpaceException {
        Map<Coordinates, ItemTile> testtiles = new HashMap<>(); //valid 5 game elements
        testtiles.put(new Coordinates(3,2), ItemTile.GAME);
        testtiles.put(new Coordinates(0,0), ItemTile.GAME);
        testtiles.put(new Coordinates(4,4), ItemTile.GAME);
        testtiles.put(new Coordinates(5,0), ItemTile.GAME);
        testtiles.put(new Coordinates(2,1), ItemTile.GAME);
        Bookshelf testingbookshelf = new CardBookshelf(testtiles);
        assertTrue(card.verifyConstraint(testingbookshelf));

        testtiles = new HashMap<>(); //valid 4 game elements --> false expected
        testtiles.put(new Coordinates(3,2), ItemTile.GAME);
        testtiles.put(new Coordinates(0,0), ItemTile.GAME);
        testtiles.put(new Coordinates(4,4), ItemTile.GAME);
        testtiles.put(new Coordinates(5,0), ItemTile.GAME);
        testingbookshelf = new CardBookshelf(testtiles);
        assertFalse(card.verifyConstraint(testingbookshelf));

        testtiles = new HashMap<>(); //valid 4 game elements --> false expected
        testtiles.put(new Coordinates(3,2), ItemTile.GAME);
        testtiles.put(new Coordinates(0,0), ItemTile.GAME);
        testtiles.put(new Coordinates(4,4), ItemTile.GAME);
        testtiles.put(new Coordinates(5,0), ItemTile.GAME);
        testtiles.put(new Coordinates(2,2), ItemTile.GAME);
        testtiles.put(new Coordinates(1,1), ItemTile.GAME);
        testingbookshelf = new CardBookshelf(testtiles);
        assertTrue(card.verifyConstraint(testingbookshelf));
    }
    /**
     * verifies if NotEnoughSpaceException works in the correct cases
     * @throws NegativeFieldException
     * @throws PlayersNumberOutOfRange
     * @throws InvalidCoordinatesException
     */
    @Test
    @DisplayName("NotEnoughSpaceExceptionTester")
    void NotEnoughSpaceExceptionTest () throws NegativeFieldException, PlayersNumberOutOfRange, InvalidCoordinatesException, RemoteException {
        card = new NsameTiles(2,"description_test",CommonCardType.CORNERS,31);
        Map<Coordinates, ItemTile> testtiles = new HashMap<>(); //shape valid groups
        testtiles.put(new Coordinates(0,0), ItemTile.CAT);
        Bookshelf testingbookshelf = new CardBookshelf(testtiles);
        assertThrows(NotEnoughSpaceException.class,()->{card.verifyConstraint(testingbookshelf);});
    }
}
