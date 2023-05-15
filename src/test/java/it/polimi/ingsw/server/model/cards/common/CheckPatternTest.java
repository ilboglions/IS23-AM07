package it.polimi.ingsw.server.model.cards.common;

import it.polimi.ingsw.server.model.bookshelf.Bookshelf;
import it.polimi.ingsw.server.model.bookshelf.CardBookshelf;
import it.polimi.ingsw.server.model.coordinate.Coordinates;
import it.polimi.ingsw.server.model.exceptions.InvalidCoordinatesException;
import it.polimi.ingsw.server.model.exceptions.NotEnoughSpaceException;
import it.polimi.ingsw.server.model.exceptions.PlayersNumberOutOfRange;
import it.polimi.ingsw.server.model.tiles.ItemTile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

class CheckPatternTest {
    private CheckPattern card;
    private ArrayList<ArrayList<Coordinates>> pattern;

    /**
     * This method creates a CheckPattern card, verifies the constraint on the number of players
     * @throws PlayersNumberOutOfRange if nPlayers < 1 || nPLayers > 4
     */
    @BeforeEach
    @DisplayName("Card creation test")
    void  createCard() throws PlayersNumberOutOfRange, InvalidCoordinatesException, RemoteException {
        pattern = new ArrayList<>();
        ArrayList<Coordinates> singlepattern = new ArrayList<>();
        singlepattern.add(new Coordinates(2,1));
        singlepattern.add(new Coordinates(5,0));
        singlepattern.add(new Coordinates(1,3));
        pattern.add(singlepattern);
        assertThrows(PlayersNumberOutOfRange.class, () -> {
            new CheckPattern(0,"", CommonCardType.CORNERS, pattern, true);});
        assertThrows(PlayersNumberOutOfRange.class, () -> {
            new CheckPattern(5,"", CommonCardType.CORNERS, pattern, true);});
        card = new CheckPattern(2,"description_test", CommonCardType.CORNERS, pattern, true);
        assertEquals(CommonCardType.CORNERS, card.getName());
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
        testtiles.put(new Coordinates(5,0), ItemTile.CAT);
        testtiles.put(new Coordinates(1,3), ItemTile.CAT);
        Bookshelf testingbookshelf = new CardBookshelf(testtiles);
        assertTrue(card.verifyConstraint(testingbookshelf));
        testtiles = new HashMap<>();
        testtiles.put(new Coordinates(2,1), ItemTile.CAT);
        testtiles.put(new Coordinates(5,0), ItemTile.TROPHY);
        testtiles.put(new Coordinates(1,3), ItemTile.CAT);
        testingbookshelf = new CardBookshelf(testtiles);
        assertFalse(card.verifyConstraint(testingbookshelf));
    }

    /**
     * This test verifies if the NotEnoughSpaceException is thrown when the pattern can't be represented on the bookshelft
     * @throws InvalidCoordinatesException
     * @throws PlayersNumberOutOfRange
     */
    @Test
    @DisplayName("Exception Tester")
    void NotEnoughSpaceExceptionTester() throws InvalidCoordinatesException, PlayersNumberOutOfRange, RemoteException {
        pattern = new ArrayList<>();
        ArrayList<Coordinates> singlepattern = new ArrayList<>();
        singlepattern.add(new Coordinates(8,8));
        pattern.add(singlepattern);
        CheckPattern testcard = new CheckPattern(2,"description_test",CommonCardType.CORNERS, pattern, true);
        Map<Coordinates, ItemTile> testtiles = new HashMap<>();
        testtiles.put(new Coordinates(2,1), ItemTile.CAT);
        testtiles.put(new Coordinates(5,0), ItemTile.TROPHY);
        testtiles.put(new Coordinates(1,3), ItemTile.CAT);
        Bookshelf testingbookshelf = new CardBookshelf(testtiles);
        assertThrows(NotEnoughSpaceException.class, () -> {
            testcard.verifyConstraint(testingbookshelf);});
    }

    /**
     * This test verifies the correct functioning of the method verifyCostraint when sameTiles flag is false
     * @throws InvalidCoordinatesException
     * @throws PlayersNumberOutOfRange
     * @throws NotEnoughSpaceException
     */
    @Test
    @DisplayName("sameTiles False test")
    void FalsesameTilesTest() throws InvalidCoordinatesException, PlayersNumberOutOfRange, NotEnoughSpaceException, RemoteException {
        pattern = new ArrayList<>();
        ArrayList<Coordinates> singlepattern = new ArrayList<>();
        singlepattern.add(new Coordinates(2,1));
        singlepattern.add(new Coordinates(5,0));
        singlepattern.add(new Coordinates(1,3));
        pattern.add(singlepattern);
        CheckPattern card = new CheckPattern(2,"description_test",CommonCardType.CORNERS, pattern, false);
        Map<Coordinates, ItemTile> testtiles = new HashMap<>();
        testtiles.put(new Coordinates(2,1), ItemTile.CAT);
        testtiles.put(new Coordinates(5,0), ItemTile.TROPHY);
        testtiles.put(new Coordinates(1,3), ItemTile.CAT);
        Bookshelf testingbookshelf = new CardBookshelf(testtiles);
        assertTrue(card.verifyConstraint(testingbookshelf));
        testtiles = new HashMap<>();
        testtiles.put(new Coordinates(5,0), ItemTile.TROPHY);
        testtiles.put(new Coordinates(1,3), ItemTile.CAT);
        testingbookshelf = new CardBookshelf(testtiles);
        assertFalse(card.verifyConstraint(testingbookshelf));
    }

}