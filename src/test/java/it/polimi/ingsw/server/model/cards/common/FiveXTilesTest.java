package it.polimi.ingsw.server.model.cards.common;

import it.polimi.ingsw.server.model.bookshelf.Bookshelf;
import it.polimi.ingsw.server.model.bookshelf.CardBookshelf;
import it.polimi.ingsw.server.model.coordinate.Coordinates;
import it.polimi.ingsw.server.model.exceptions.InvalidCoordinatesException;
import it.polimi.ingsw.server.model.exceptions.PlayersNumberOutOfRange;
import it.polimi.ingsw.server.model.tiles.ItemTile;
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
            new FiveXTiles(0,"",CommonCardType.CORNERS, true);});
        assertThrows(PlayersNumberOutOfRange.class, () -> {
            new FiveXTiles(5,"",CommonCardType.CORNERS, true);});
        card = new FiveXTiles(2,"description_test",CommonCardType.CORNERS, true);
    }
    /**
     * This test verifies the correct functioning of the method verifyCostraint when sameTiles flag is true
     * @throws InvalidCoordinatesException
     */
    @Test
    @DisplayName("Verify Constraint Tester")
    void verifyCostrainttest() throws InvalidCoordinatesException {
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
     * @throws InvalidCoordinatesException
     */
    @Test
    @DisplayName("sameTiles False test")
    void FalsesameTilesTest() throws InvalidCoordinatesException, PlayersNumberOutOfRange {
        card = new FiveXTiles(2,"description_test",CommonCardType.CORNERS, false);
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
