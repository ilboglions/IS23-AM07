package it.polimi.ingsw.model.cards.common;

import it.polimi.ingsw.server.model.bookshelf.Bookshelf;
import it.polimi.ingsw.server.model.bookshelf.CardBookshelf;
import it.polimi.ingsw.server.model.cards.common.NadjacentElements;
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

public class NadjacentElementsTest {
    private NadjacentElements card;

    /**
     * This method creates a NadjacentElements card, verifies the constraint on the number of players
     * and the costraint negative field costraint (nGroups and nElems > 0)
     * @throws PlayersNumberOutOfRange if nPlayers < 1 || nPLayers > 4
     * @throws NegativeFieldException if nGroups <= 0 nElems <=0
     */
    @BeforeEach
    @DisplayName("Card creation test")
    void  createCard() throws PlayersNumberOutOfRange, NegativeFieldException {
        assertThrows(PlayersNumberOutOfRange.class, () -> {
            new NadjacentElements(0,"description_test",2,3);});
        assertThrows(PlayersNumberOutOfRange.class, () -> {
            new NadjacentElements(5,"description_test",2,3);});
        assertThrows(NegativeFieldException.class, () -> {
            new NadjacentElements(3,"description_test",-2, 3);});
        assertThrows(NegativeFieldException.class, () -> {
            new NadjacentElements(3,"description_test",2, 0);});

        card = new NadjacentElements(2,"description_test",3,3);
    }

    /**
     * This test verifies the correct functioning of the verifyCostraint method
     * @throws NotEnoughSpaceException
     * @throws InvalidCoordinatesException
     */
    @Test
    @DisplayName("Verify Constraint Tester")
    void verifyCostrainttest() throws NotEnoughSpaceException, InvalidCoordinatesException {
        Map<Coordinates, ItemTile> testtiles = new HashMap<>(); //valid groups
        testtiles.put(new Coordinates(0,0), ItemTile.CAT);
        testtiles.put(new Coordinates(1,0), ItemTile.CAT);
        testtiles.put(new Coordinates(2,0), ItemTile.CAT);

        testtiles.put(new Coordinates(0,2), ItemTile.GAME);
        testtiles.put(new Coordinates(0,3), ItemTile.GAME);
        testtiles.put(new Coordinates(0,4), ItemTile.GAME);

        testtiles.put(new Coordinates(4,2), ItemTile.PLANT);
        testtiles.put(new Coordinates(4,3), ItemTile.PLANT);
        testtiles.put(new Coordinates(4,4), ItemTile.PLANT);

        Bookshelf testingbookshelf = new CardBookshelf(testtiles);
        assertTrue(card.verifyConstraint(testingbookshelf));
        testtiles = new HashMap<>(); //non-valid groups
        testtiles.put(new Coordinates(0,0), ItemTile.CAT);
        testtiles.put(new Coordinates(1,0), ItemTile.CAT);
        testtiles.put(new Coordinates(2,0), ItemTile.CAT);

        testtiles.put(new Coordinates(0,2), ItemTile.GAME);
        testtiles.put(new Coordinates(0,3), ItemTile.GAME);
        testtiles.put(new Coordinates(0,4), ItemTile.GAME);

        testtiles.put(new Coordinates(4,2), ItemTile.PLANT);
        testtiles.put(new Coordinates(4,4), ItemTile.PLANT);

        testingbookshelf = new CardBookshelf(testtiles);
        assertFalse(card.verifyConstraint(testingbookshelf));
        testtiles = new HashMap<>(); //shape valid groups
        testtiles.put(new Coordinates(0,0), ItemTile.CAT);
        testtiles.put(new Coordinates(1,0), ItemTile.CAT);
        testtiles.put(new Coordinates(1,1), ItemTile.CAT);

        testtiles.put(new Coordinates(0,2), ItemTile.GAME);
        testtiles.put(new Coordinates(0,3), ItemTile.GAME);
        testtiles.put(new Coordinates(0,4), ItemTile.GAME);

        testtiles.put(new Coordinates(4,2), ItemTile.PLANT);
        testtiles.put(new Coordinates(4,3), ItemTile.PLANT);
        testtiles.put(new Coordinates(3,3), ItemTile.PLANT);

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
    public void NotEnoughSpaceExceptionTest () throws NegativeFieldException, PlayersNumberOutOfRange, InvalidCoordinatesException {
        card = new NadjacentElements(2,"description_test",5,7);
        Map<Coordinates, ItemTile> testtiles = new HashMap<>(); //shape valid groups
        testtiles.put(new Coordinates(0,0), ItemTile.CAT);
        Bookshelf testingbookshelf = new CardBookshelf(testtiles);
        assertThrows(NotEnoughSpaceException.class,()->{card.verifyConstraint(testingbookshelf);});
    }
}
