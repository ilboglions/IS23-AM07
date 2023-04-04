package it.polimi.ingsw.model.cards.common;

import it.polimi.ingsw.model.bookshelf.Bookshelf;
import it.polimi.ingsw.model.bookshelf.CardBookshelf;
import it.polimi.ingsw.model.coordinate.Coordinates;
import it.polimi.ingsw.model.exceptions.*;
import it.polimi.ingsw.model.tiles.ItemTile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.HashMap;
import java.util.Map;

public class FiveXTilesTest {
    private FiveXTiles card;
    /**
     * This method creates a FiveXTiles card, verifies the constraint on the number of players
     * @throws PlayersNumberOutOfRange if nPlayers < 1 || nPLayers > 4
     */
    @BeforeEach
    @DisplayName("Card creation test")
    void  createCard() throws PlayersNumberOutOfRange {
        assertThrows(PlayersNumberOutOfRange.class, () -> {
            new FiveXTiles(0,"", true);});
        assertThrows(PlayersNumberOutOfRange.class, () -> {
            new FiveXTiles(5,"", true);});
        card = new FiveXTiles(2,"description_test", true);
    }
    /**
     * This test verifies the correct functioning of the method verifyCostraint when sameTiles flag is true
     * @throws NotEnoughSpaceException
     * @throws InvalidCoordinatesException
     */
    @Test
    @DisplayName("Verify Constraint Tester")
    void verifyCostrainttest() throws NotEnoughSpaceException, InvalidCoordinatesException {
        Map<Coordinates, ItemTile> testtiles = new HashMap<>();
        testtiles.put(new Coordinates(2,1), ItemTile.CAT);
        testtiles.put(new Coordinates(2,3), ItemTile.CAT);
        testtiles.put(new Coordinates(3,2), ItemTile.CAT);
        testtiles.put(new Coordinates(4,1), ItemTile.CAT);
        testtiles.put(new Coordinates(4,3), ItemTile.CAT);
        Bookshelf testingbookshelf = new CardBookshelf(testtiles);
        assertTrue(card.verifyConstraint(testingbookshelf));
        testtiles = new HashMap<>();
        testtiles.put(new Coordinates(2,1), ItemTile.CAT);
        testtiles.put(new Coordinates(2,3), ItemTile.GAME);
        testtiles.put(new Coordinates(3,2), ItemTile.CAT);
        testtiles.put(new Coordinates(4,1), ItemTile.CAT);
        testtiles.put(new Coordinates(4,3), ItemTile.CAT);
        testingbookshelf = new CardBookshelf(testtiles);
        assertFalse(card.verifyConstraint(testingbookshelf));
    }
    /**
     * This test verifies the correct functioning of the method verifyCostraint when sameTiles flag is false
     * @throws NotEnoughSpaceException
     * @throws InvalidCoordinatesException
     */
    @Test
    @DisplayName("sameTiles False test")
    void FalsesameTilesTest() throws NotEnoughSpaceException, InvalidCoordinatesException, PlayersNumberOutOfRange {
        card = new FiveXTiles(2,"description_test", false);
        Map<Coordinates, ItemTile> testtiles = new HashMap<>();
        testtiles.put(new Coordinates(1,1), ItemTile.CAT);
        testtiles.put(new Coordinates(1,3), ItemTile.CAT);
        testtiles.put(new Coordinates(2,2), ItemTile.GAME);
        testtiles.put(new Coordinates(3,1), ItemTile.CAT);
        testtiles.put(new Coordinates(3,3), ItemTile.CAT);
        Bookshelf testingbookshelf = new CardBookshelf(testtiles);
        assertTrue(card.verifyConstraint(testingbookshelf));
        testtiles = new HashMap<>();
        testtiles.put(new Coordinates(2,1), ItemTile.CAT);
        testtiles.put(new Coordinates(2,3), ItemTile.GAME);
        testtiles.put(new Coordinates(4,1), ItemTile.CAT);
        testtiles.put(new Coordinates(4,3), ItemTile.CAT);
        testingbookshelf = new CardBookshelf(testtiles);
        assertFalse(card.verifyConstraint(testingbookshelf));
    }



}
