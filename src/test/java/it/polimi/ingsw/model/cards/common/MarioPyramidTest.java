package it.polimi.ingsw.model.cards.common;

import it.polimi.ingsw.server.model.bookshelf.Bookshelf;
import it.polimi.ingsw.server.model.bookshelf.CardBookshelf;
import it.polimi.ingsw.server.model.cards.common.MarioPyramid;
import it.polimi.ingsw.server.model.coordinate.Coordinates;
import it.polimi.ingsw.server.model.exceptions.InvalidCoordinatesException;
import it.polimi.ingsw.server.model.exceptions.NotEnoughSpaceException;
import it.polimi.ingsw.server.model.exceptions.PlayersNumberOutOfRange;
import it.polimi.ingsw.server.model.tiles.ItemTile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
public class MarioPyramidTest {

    private MarioPyramid card;
    /**
     * This method creates a MarioPyramid card, verifies the constraint on the number of players
     * @throws PlayersNumberOutOfRange if nPlayers < 1 || nPLayers > 4
     */
    @BeforeEach
    @DisplayName("Card creation test")
    void  createCard() throws PlayersNumberOutOfRange, InvalidCoordinatesException {
        assertThrows(PlayersNumberOutOfRange.class, () -> {
            new MarioPyramid(0,"");});
        assertThrows(PlayersNumberOutOfRange.class, () -> {
            new MarioPyramid(5,"");});
        card = new MarioPyramid(2,"description_test");
    }

    /**
     * This test verifies the correct functioning of the method verifyCostraint
     * @throws NotEnoughSpaceException
     * @throws InvalidCoordinatesException
     */
    @Test
    @DisplayName("Verify Constraint Tester")
    public void verifyCostrainttest() throws InvalidCoordinatesException {
        Map<Coordinates, ItemTile> testtiles = new HashMap<>();  //LEFT PYRAMID
        testtiles.put(new Coordinates(0,0), ItemTile.CAT);
        testtiles.put(new Coordinates(0,1), ItemTile.CAT);
        testtiles.put(new Coordinates(0,2), ItemTile.CAT);
        testtiles.put(new Coordinates(0,3), ItemTile.CAT);
        testtiles.put(new Coordinates(0,4), ItemTile.CAT);
        testtiles.put(new Coordinates(1,0), ItemTile.CAT);
        testtiles.put(new Coordinates(1,1), ItemTile.CAT);
        testtiles.put(new Coordinates(1,2), ItemTile.CAT);
        testtiles.put(new Coordinates(1,3), ItemTile.CAT);
        testtiles.put(new Coordinates(2,0), ItemTile.CAT);
        testtiles.put(new Coordinates(2,1), ItemTile.CAT);
        testtiles.put(new Coordinates(2,2), ItemTile.CAT);
        testtiles.put(new Coordinates(3,0), ItemTile.CAT);
        testtiles.put(new Coordinates(3,1), ItemTile.CAT);
        testtiles.put(new Coordinates(4,0), ItemTile.CAT);
        Bookshelf testingbookshelf = new CardBookshelf(testtiles);
        assertTrue(card.verifyConstraint(testingbookshelf));

        testtiles = new HashMap<>();  //RIGHT PYRAMID
        testtiles.put(new Coordinates(0,0), ItemTile.CAT);
        testtiles.put(new Coordinates(0,1), ItemTile.CAT);
        testtiles.put(new Coordinates(0,2), ItemTile.CAT);
        testtiles.put(new Coordinates(0,3), ItemTile.CAT);
        testtiles.put(new Coordinates(0,4), ItemTile.CAT);
        testtiles.put(new Coordinates(1,4), ItemTile.CAT);
        testtiles.put(new Coordinates(1,3), ItemTile.CAT);
        testtiles.put(new Coordinates(1,2), ItemTile.CAT);
        testtiles.put(new Coordinates(1,1), ItemTile.CAT);
        testtiles.put(new Coordinates(2,4), ItemTile.CAT);
        testtiles.put(new Coordinates(2,3), ItemTile.CAT);
        testtiles.put(new Coordinates(2,2), ItemTile.CAT);
        testtiles.put(new Coordinates(3,4), ItemTile.CAT);
        testtiles.put(new Coordinates(3,3), ItemTile.CAT);
        testtiles.put(new Coordinates(4,4), ItemTile.CAT);
        testingbookshelf = new CardBookshelf(testtiles);
        assertTrue(card.verifyConstraint(testingbookshelf));
        testtiles = new HashMap<>();  //NO PYRAMID
        testtiles.put(new Coordinates(0,0), ItemTile.CAT);
        testtiles.put(new Coordinates(0,1), ItemTile.CAT);
        testtiles.put(new Coordinates(0,2), ItemTile.CAT);
        testtiles.put(new Coordinates(0,3), ItemTile.CAT);
        testtiles.put(new Coordinates(1,0), ItemTile.CAT);
        testtiles.put(new Coordinates(1,1), ItemTile.CAT);
        testtiles.put(new Coordinates(1,2), ItemTile.CAT);
        testtiles.put(new Coordinates(1,3), ItemTile.CAT);
        testtiles.put(new Coordinates(2,0), ItemTile.CAT);
        testtiles.put(new Coordinates(2,1), ItemTile.CAT);
        testtiles.put(new Coordinates(2,2), ItemTile.CAT);
        testtiles.put(new Coordinates(3,0), ItemTile.CAT);
        testtiles.put(new Coordinates(3,1), ItemTile.CAT);
        testtiles.put(new Coordinates(4,0), ItemTile.CAT);
        testingbookshelf = new CardBookshelf(testtiles);
        assertFalse(card.verifyConstraint(testingbookshelf));
        testtiles = new HashMap<>();  //HIGHER RIGHT PYRAMID
        testtiles.put(new Coordinates(0,0), ItemTile.CAT);
        testtiles.put(new Coordinates(0,1), ItemTile.CAT);
        testtiles.put(new Coordinates(0,2), ItemTile.CAT);
        testtiles.put(new Coordinates(0,3), ItemTile.CAT);
        testtiles.put(new Coordinates(0,4), ItemTile.CAT);
        testtiles.put(new Coordinates(1,0), ItemTile.CAT);
        testtiles.put(new Coordinates(1,1), ItemTile.CAT);
        testtiles.put(new Coordinates(1,2), ItemTile.CAT);
        testtiles.put(new Coordinates(1,3), ItemTile.CAT);
        testtiles.put(new Coordinates(1,4), ItemTile.CAT);
        testtiles.put(new Coordinates(2,4), ItemTile.CAT);
        testtiles.put(new Coordinates(2,3), ItemTile.CAT);
        testtiles.put(new Coordinates(2,2), ItemTile.CAT);
        testtiles.put(new Coordinates(2,1), ItemTile.CAT);
        testtiles.put(new Coordinates(3,4), ItemTile.CAT);
        testtiles.put(new Coordinates(3,3), ItemTile.CAT);
        testtiles.put(new Coordinates(3,2), ItemTile.CAT);
        testtiles.put(new Coordinates(4,4), ItemTile.CAT);
        testtiles.put(new Coordinates(4,3), ItemTile.CAT);
        testtiles.put(new Coordinates(5,4), ItemTile.CAT);
        testingbookshelf = new CardBookshelf(testtiles);
        assertTrue(card.verifyConstraint(testingbookshelf));
        testtiles = new HashMap<>();  //HIGHER LEFT PYRAMID
        testtiles.put(new Coordinates(0,0), ItemTile.CAT);
        testtiles.put(new Coordinates(0,1), ItemTile.CAT);
        testtiles.put(new Coordinates(0,2), ItemTile.CAT);
        testtiles.put(new Coordinates(0,3), ItemTile.CAT);
        testtiles.put(new Coordinates(0,4), ItemTile.CAT);
        testtiles.put(new Coordinates(1,0), ItemTile.CAT);
        testtiles.put(new Coordinates(1,1), ItemTile.CAT);
        testtiles.put(new Coordinates(1,2), ItemTile.CAT);
        testtiles.put(new Coordinates(1,3), ItemTile.CAT);
        testtiles.put(new Coordinates(1,4), ItemTile.CAT);
        testtiles.put(new Coordinates(2,0), ItemTile.CAT);
        testtiles.put(new Coordinates(2,1), ItemTile.CAT);
        testtiles.put(new Coordinates(2,2), ItemTile.CAT);
        testtiles.put(new Coordinates(2,3), ItemTile.CAT);
        testtiles.put(new Coordinates(3,0), ItemTile.CAT);
        testtiles.put(new Coordinates(3,1), ItemTile.CAT);
        testtiles.put(new Coordinates(3,2), ItemTile.CAT);
        testtiles.put(new Coordinates(4,0), ItemTile.CAT);
        testtiles.put(new Coordinates(4,1), ItemTile.CAT);
        testtiles.put(new Coordinates(5,0), ItemTile.CAT);
        testingbookshelf = new CardBookshelf(testtiles);
        assertTrue(card.verifyConstraint(testingbookshelf));
        testtiles = new HashMap<>();  //NO HIGHER RIGHT PYRAMID
        testtiles.put(new Coordinates(0,0), ItemTile.CAT);
        testtiles.put(new Coordinates(0,1), ItemTile.CAT);
        testtiles.put(new Coordinates(0,2), ItemTile.CAT);
        testtiles.put(new Coordinates(0,3), ItemTile.CAT);
        testtiles.put(new Coordinates(0,4), ItemTile.CAT);
        testtiles.put(new Coordinates(1,0), ItemTile.CAT);
        testtiles.put(new Coordinates(1,1), ItemTile.CAT);
        testtiles.put(new Coordinates(1,2), ItemTile.CAT);
        testtiles.put(new Coordinates(1,3), ItemTile.CAT);
        testtiles.put(new Coordinates(1,4), ItemTile.CAT);
        testtiles.put(new Coordinates(2,4), ItemTile.CAT);
        testtiles.put(new Coordinates(2,3), ItemTile.CAT);
        testtiles.put(new Coordinates(2,2), ItemTile.CAT);
        testtiles.put(new Coordinates(2,1), ItemTile.CAT);
        testtiles.put(new Coordinates(3,4), ItemTile.CAT);
        testtiles.put(new Coordinates(3,3), ItemTile.CAT);
        testtiles.put(new Coordinates(3,2), ItemTile.CAT);
        testtiles.put(new Coordinates(4,4), ItemTile.CAT);
        testtiles.put(new Coordinates(4,3), ItemTile.CAT);
        testingbookshelf = new CardBookshelf(testtiles);
        assertFalse(card.verifyConstraint(testingbookshelf));
    }
}
