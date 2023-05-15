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
import static org.junit.jupiter.api.Assertions.*;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;
public class FullRowsTest {
    private FullRows card;
    /**
     * This method creates a FullColumns card, verifies the constraint on the number of players
     * and the costraint negative field costraint (nCols and maxTilesFrule > 0)
     * @throws PlayersNumberOutOfRange if nPlayers < 1 || nPLayers > 4
     */
    @BeforeEach
    @DisplayName("Card creation test")
    void  createCard() throws PlayersNumberOutOfRange, NegativeFieldException, RemoteException {
        assertThrows(PlayersNumberOutOfRange.class, () -> {
            new FullColumns(0,"description_test",CommonCardType.CORNERS,2,true , 3);});
        assertThrows(PlayersNumberOutOfRange.class, () -> {
            new FullColumns(5,"description_test",CommonCardType.CORNERS,2,true , 3);});
        assertThrows(NegativeFieldException.class, () -> {
            new FullColumns(3,"description_test",CommonCardType.CORNERS,-2,true , 3);});
        assertThrows(NegativeFieldException.class, () -> {
            new FullColumns(3,"description_test",CommonCardType.CORNERS,2,true , 0);});

        card = new FullRows(2,"description_test",CommonCardType.CORNERS,2,true , 3);
    }

    /**
     * This test verifies the correct functioning of the method verifyCostraint when sameTiles flag is true
     *
     * @throws NotEnoughSpaceException
     * @throws InvalidCoordinatesException
     */
    @Test
    @DisplayName("Verify Constraint Tester")
    void verifyCostrainttest() throws NotEnoughSpaceException, InvalidCoordinatesException {
        Map<Coordinates, ItemTile> testtiles = new HashMap<>(); //both rows valid
        testtiles.put(new Coordinates(0,0), ItemTile.CAT);
        testtiles.put(new Coordinates(0,1), ItemTile.CAT);
        testtiles.put(new Coordinates(0,2), ItemTile.CAT);
        testtiles.put(new Coordinates(0,3), ItemTile.TROPHY);
        testtiles.put(new Coordinates(0,4), ItemTile.PLANT);
        testtiles.put(new Coordinates(1,0), ItemTile.CAT);
        testtiles.put(new Coordinates(1,1), ItemTile.CAT);
        testtiles.put(new Coordinates(1,2), ItemTile.CAT);
        testtiles.put(new Coordinates(1,3), ItemTile.FRAME);
        testtiles.put(new Coordinates(1,4), ItemTile.PLANT);
        Bookshelf testingbookshelf = new CardBookshelf(testtiles);
        assertTrue(card.verifyConstraint(testingbookshelf));
        testtiles = new HashMap<>(); //first row valid, second valid
        testtiles.put(new Coordinates(0,0), ItemTile.CAT);
        testtiles.put(new Coordinates(0,1), ItemTile.CAT);
        testtiles.put(new Coordinates(0,2), ItemTile.CAT);
        testtiles.put(new Coordinates(0,3), ItemTile.CAT);
        testtiles.put(new Coordinates(0,4), ItemTile.PLANT);
        testtiles.put(new Coordinates(1,0), ItemTile.CAT);
        testtiles.put(new Coordinates(1,1), ItemTile.CAT);
        testtiles.put(new Coordinates(1,2), ItemTile.GAME);
        testtiles.put(new Coordinates(1,3), ItemTile.FRAME);
        testtiles.put(new Coordinates(1,4), ItemTile.PLANT);
        testingbookshelf = new CardBookshelf(testtiles);
        assertFalse(card.verifyConstraint(testingbookshelf));
        testtiles = new HashMap<>(); //first row non valid, second valid
        testtiles.put(new Coordinates(0,0), ItemTile.CAT);
        testtiles.put(new Coordinates(0,1), ItemTile.BOOK);
        testtiles.put(new Coordinates(0,2), ItemTile.FRAME);
        testtiles.put(new Coordinates(0,3), ItemTile.TROPHY);
        testtiles.put(new Coordinates(0,4), ItemTile.PLANT);
        testtiles.put(new Coordinates(1,0), ItemTile.CAT);
        testtiles.put(new Coordinates(1,1), ItemTile.CAT);
        testtiles.put(new Coordinates(1,2), ItemTile.CAT);
        testtiles.put(new Coordinates(1,3), ItemTile.CAT);
        testtiles.put(new Coordinates(1,4), ItemTile.PLANT);
        testingbookshelf = new CardBookshelf(testtiles);
        assertFalse(card.verifyConstraint(testingbookshelf));
        testtiles = new HashMap<>(); //none valid
        testtiles.put(new Coordinates(0,0), ItemTile.CAT);
        testtiles.put(new Coordinates(0,1), ItemTile.BOOK);
        testtiles.put(new Coordinates(0,2), ItemTile.GAME);
        testtiles.put(new Coordinates(0,3), ItemTile.FRAME);
        testtiles.put(new Coordinates(0,4), ItemTile.PLANT);
        testtiles.put(new Coordinates(1,0), ItemTile.CAT);
        testtiles.put(new Coordinates(1,1), ItemTile.BOOK);
        testtiles.put(new Coordinates(1,2), ItemTile.GAME);
        testtiles.put(new Coordinates(1,3), ItemTile.FRAME);
        testtiles.put(new Coordinates(1,4), ItemTile.PLANT);
        testingbookshelf = new CardBookshelf(testtiles);
        assertFalse(card.verifyConstraint(testingbookshelf));
        testtiles = new HashMap<>(); //3 rows valid
        testtiles.put(new Coordinates(0,0), ItemTile.CAT);
        testtiles.put(new Coordinates(0,1), ItemTile.CAT);
        testtiles.put(new Coordinates(0,2), ItemTile.CAT);
        testtiles.put(new Coordinates(0,3), ItemTile.CAT);
        testtiles.put(new Coordinates(0,4), ItemTile.PLANT);
        testtiles.put(new Coordinates(1,0), ItemTile.CAT);
        testtiles.put(new Coordinates(1,1), ItemTile.CAT);
        testtiles.put(new Coordinates(1,2), ItemTile.CAT);
        testtiles.put(new Coordinates(1,3), ItemTile.FRAME);
        testtiles.put(new Coordinates(1,4), ItemTile.PLANT);
        testtiles.put(new Coordinates(2,0), ItemTile.CAT);
        testtiles.put(new Coordinates(2,1), ItemTile.CAT);
        testtiles.put(new Coordinates(2,2), ItemTile.CAT);
        testtiles.put(new Coordinates(2,3), ItemTile.FRAME);
        testtiles.put(new Coordinates(2,4), ItemTile.PLANT);
        testingbookshelf = new CardBookshelf(testtiles);
        assertTrue(card.verifyConstraint(testingbookshelf));

    }
    /**
     * This test verifies the correct functioning of the method verifyCostraint when sameTiles flag is false
     * @throws InvalidCoordinatesException
     * @throws PlayersNumberOutOfRange
     * @throws NotEnoughSpaceException
     */
    @Test
    @DisplayName("sameTiles False test")
    void FalsesameTilesTest() throws NegativeFieldException, PlayersNumberOutOfRange, InvalidCoordinatesException, NotEnoughSpaceException, RemoteException {
        card = new FullRows(2,"description_test",CommonCardType.CORNERS,2,false , 5);
        Map<Coordinates, ItemTile> testtiles = new HashMap<>(); //both rows valid
        testtiles.put(new Coordinates(0,0), ItemTile.CAT);
        testtiles.put(new Coordinates(0,1), ItemTile.BOOK);
        testtiles.put(new Coordinates(0,2), ItemTile.GAME);
        testtiles.put(new Coordinates(0,3), ItemTile.FRAME);
        testtiles.put(new Coordinates(0,4), ItemTile.PLANT);
        testtiles.put(new Coordinates(1,0), ItemTile.CAT);
        testtiles.put(new Coordinates(1,1), ItemTile.BOOK);
        testtiles.put(new Coordinates(1,2), ItemTile.GAME);
        testtiles.put(new Coordinates(1,3), ItemTile.FRAME);
        testtiles.put(new Coordinates(1,4), ItemTile.PLANT);
        Bookshelf testingbookshelf = new CardBookshelf(testtiles);
        assertTrue(card.verifyConstraint(testingbookshelf));
        testtiles = new HashMap<>(); //1 column valid
        testtiles.put(new Coordinates(0,0), ItemTile.CAT);
        testtiles.put(new Coordinates(0,1), ItemTile.CAT);
        testtiles.put(new Coordinates(0,2), ItemTile.GAME);
        testtiles.put(new Coordinates(0,3), ItemTile.FRAME);
        testtiles.put(new Coordinates(0,4), ItemTile.PLANT);
        testtiles.put(new Coordinates(1,0), ItemTile.CAT);
        testtiles.put(new Coordinates(1,1), ItemTile.BOOK);
        testtiles.put(new Coordinates(1,2), ItemTile.GAME);
        testtiles.put(new Coordinates(1,3), ItemTile.FRAME);
        testtiles.put(new Coordinates(1,4), ItemTile.PLANT);
        testingbookshelf = new CardBookshelf(testtiles);
        assertFalse(card.verifyConstraint(testingbookshelf));
    }

    @Test
    @DisplayName("NotEnoughSpaceException")
    public void NotEnoughSpaceException () throws InvalidCoordinatesException, NegativeFieldException, PlayersNumberOutOfRange, RemoteException {
        card = new FullRows(2,"description_test",CommonCardType.CORNERS,8,false , 1);
        Map<Coordinates, ItemTile> testtiles = new HashMap<>();
        testtiles.put(new Coordinates(0,0), ItemTile.CAT);
        testtiles.put(new Coordinates(0,1), ItemTile.BOOK);
        testtiles.put(new Coordinates(0,2), ItemTile.GAME);
        testtiles.put(new Coordinates(0,3), ItemTile.FRAME);
        testtiles.put(new Coordinates(0,4), ItemTile.TROPHY);
        Bookshelf testingbookshelf = new CardBookshelf(testtiles);
        assertThrows(NotEnoughSpaceException.class, () -> {
            card.verifyConstraint(testingbookshelf);});
    }

}
