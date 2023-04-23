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

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
public class NequalsSquaresTest {

    private NequalsSquares card;

    /**
     * Testing the costructor of NequalsSquare. Testing on costraint about players and positive arguments
     * @throws PlayersNumberOutOfRange
     * @throws NegativeFieldException
     */
    @BeforeEach
    @DisplayName("Card creation test")
    void  createCard() throws PlayersNumberOutOfRange, NegativeFieldException {
        assertThrows(PlayersNumberOutOfRange.class, () -> {
            new NequalsSquares(0,"description_test",CommonCardType.CORNERS,2,3);});
        assertThrows(PlayersNumberOutOfRange.class, () -> {
            new NequalsSquares(5,"description_test",CommonCardType.CORNERS,2,3);});
        assertThrows(NegativeFieldException.class, () -> {
            new NequalsSquares(3,"description_test",CommonCardType.CORNERS,-2, 3);});
        assertThrows(NegativeFieldException.class, () -> {
            new NequalsSquares(3,"description_test",CommonCardType.CORNERS,2, 0);});

        card = new NequalsSquares(2,"description_test",CommonCardType.CORNERS,2,3);
    }

    /**
     * Verifies the correct functioning of the verifyCostraint method
     * @throws NotEnoughSpaceException
     * @throws InvalidCoordinatesException
     */
    @Test
    @DisplayName("verifyCostraint Tester")
    void verifyCostrainttest() throws NotEnoughSpaceException, InvalidCoordinatesException {
        Map<Coordinates, ItemTile> testtiles = new HashMap<>(); //valid groups
        testtiles.put(new Coordinates(0,0), ItemTile.CAT);
        testtiles.put(new Coordinates(1,0), ItemTile.CAT);
        testtiles.put(new Coordinates(2,0), ItemTile.CAT);
        testtiles.put(new Coordinates(0,1), ItemTile.CAT);
        testtiles.put(new Coordinates(1,1), ItemTile.CAT);
        testtiles.put(new Coordinates(2,1), ItemTile.CAT);
        testtiles.put(new Coordinates(0,2), ItemTile.CAT);
        testtiles.put(new Coordinates(1,2), ItemTile.CAT);
        testtiles.put(new Coordinates(2,2), ItemTile.CAT);

        testtiles.put(new Coordinates(3,2), ItemTile.GAME);
        testtiles.put(new Coordinates(4,2), ItemTile.GAME);
        testtiles.put(new Coordinates(5,2), ItemTile.GAME);
        testtiles.put(new Coordinates(3,3), ItemTile.GAME);
        testtiles.put(new Coordinates(4,3), ItemTile.GAME);
        testtiles.put(new Coordinates(5,3), ItemTile.GAME);
        testtiles.put(new Coordinates(3,4), ItemTile.GAME);
        testtiles.put(new Coordinates(4,4), ItemTile.GAME);
        testtiles.put(new Coordinates(5,4), ItemTile.GAME);
        Bookshelf testingbookshelf = new CardBookshelf(testtiles);
        assertTrue(card.verifyConstraint(testingbookshelf));

        testtiles = new HashMap<>(); //non-complete square
        testtiles.put(new Coordinates(1,0), ItemTile.CAT);
        testtiles.put(new Coordinates(2,0), ItemTile.CAT);
        testtiles.put(new Coordinates(0,1), ItemTile.CAT);
        testtiles.put(new Coordinates(1,1), ItemTile.CAT);
        testtiles.put(new Coordinates(2,1), ItemTile.CAT);
        testtiles.put(new Coordinates(0,2), ItemTile.CAT);
        testtiles.put(new Coordinates(1,2), ItemTile.CAT);
        testtiles.put(new Coordinates(2,2), ItemTile.CAT);

        testtiles.put(new Coordinates(3,2), ItemTile.GAME);
        testtiles.put(new Coordinates(4,2), ItemTile.GAME);
        testtiles.put(new Coordinates(5,2), ItemTile.GAME);
        testtiles.put(new Coordinates(3,3), ItemTile.GAME);
        testtiles.put(new Coordinates(4,3), ItemTile.GAME);
        testtiles.put(new Coordinates(5,3), ItemTile.GAME);
        testtiles.put(new Coordinates(3,4), ItemTile.GAME);
        testtiles.put(new Coordinates(4,4), ItemTile.GAME);
        testtiles.put(new Coordinates(5,4), ItemTile.GAME);

        testingbookshelf = new CardBookshelf(testtiles);
        assertFalse(card.verifyConstraint(testingbookshelf));

        testtiles = new HashMap<>(); //complete squares but same tiles close to one of them
        testtiles.put(new Coordinates(0,0), ItemTile.CAT);
        testtiles.put(new Coordinates(1,0), ItemTile.CAT);
        testtiles.put(new Coordinates(2,0), ItemTile.CAT);
        testtiles.put(new Coordinates(0,1), ItemTile.CAT);
        testtiles.put(new Coordinates(1,1), ItemTile.CAT);
        testtiles.put(new Coordinates(2,1), ItemTile.CAT);
        testtiles.put(new Coordinates(0,2), ItemTile.CAT);
        testtiles.put(new Coordinates(1,2), ItemTile.CAT);
        testtiles.put(new Coordinates(2,2), ItemTile.CAT);
        testtiles.put(new Coordinates(0,3), ItemTile.CAT);

        testtiles.put(new Coordinates(3,2), ItemTile.GAME);
        testtiles.put(new Coordinates(4,2), ItemTile.GAME);
        testtiles.put(new Coordinates(5,2), ItemTile.GAME);
        testtiles.put(new Coordinates(3,3), ItemTile.GAME);
        testtiles.put(new Coordinates(4,3), ItemTile.GAME);
        testtiles.put(new Coordinates(5,3), ItemTile.GAME);
        testtiles.put(new Coordinates(3,4), ItemTile.GAME);
        testtiles.put(new Coordinates(4,4), ItemTile.GAME);
        testtiles.put(new Coordinates(5,4), ItemTile.GAME);

        testingbookshelf = new CardBookshelf(testtiles);
        assertFalse(card.verifyConstraint(testingbookshelf));
    }

    /**
     * verifies if NotEnoughSpaceException works in the correct cases
     * @throws NegativeFieldException
     * @throws PlayersNumberOutOfRange
     * @throws InvalidCoordinatesException
     */
    @Test
    @DisplayName("NotEnoughSpaceExceptionTester")
    void NotEnoughSpaceExceptionTest () throws NegativeFieldException, PlayersNumberOutOfRange, InvalidCoordinatesException {
        card = new NequalsSquares(2,"description_test",CommonCardType.CORNERS,5,7);
        Map<Coordinates, ItemTile> testtiles = new HashMap<>(); //shape valid groups
        testtiles.put(new Coordinates(0,0), ItemTile.CAT);
        Bookshelf testingbookshelf = new CardBookshelf(testtiles);
        assertThrows(NotEnoughSpaceException.class,()->{card.verifyConstraint(testingbookshelf);});
    }
}

