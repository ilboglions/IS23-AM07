package it.polimi.ingsw.client.view;


import it.polimi.ingsw.server.model.coordinate.Coordinates;
import it.polimi.ingsw.server.model.exceptions.InvalidCoordinatesException;
import it.polimi.ingsw.server.model.tiles.ItemTile;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


public class CLITest {


    /**
     * test for the title print
     */
    @Test
    @DisplayName("Test the title print of the CLI")
    void testTitle(){
        CliView cli = new CliView();
        cli.printTitle();
    }


    @Test
    @DisplayName("Test the print of the bookshelfs and the personal goal card")
    void testBookshelfPrint() throws InvalidCoordinatesException {
        CliView cli = new CliView();
        Map<Coordinates, ItemTile> testPattern = new HashMap<>();

        testPattern.put(new Coordinates(0,0), ItemTile.GAME);
        testPattern.put(new Coordinates(5,4), ItemTile.CAT);
        testPattern.put(new Coordinates(3,3), ItemTile.TROPHY);
        testPattern.put(new Coordinates(2,0), ItemTile.PLANT);
        testPattern.put(new Coordinates(0,4), ItemTile.GAME);

        cli.printYourBookShelf(testPattern);
        cli.printBookShelf(testPattern,"Piero Pelu");

        Map<Integer,Integer> pointReference = new HashMap<>();

        pointReference.put(1,1);
        pointReference.put(2,2);
        pointReference.put(3,4);
        pointReference.put(4,6);
        pointReference.put(5,9);
        pointReference.put(6,12);

        cli.printPersonalCard(testPattern,pointReference);
    }
    @Test
    @DisplayName("Test the print of the livingRoom")
    void testLivingRoomPrint() throws InvalidCoordinatesException {
        CliView cli = new CliView();
        Map<Coordinates, Optional<ItemTile>> testBoardPattern = new HashMap<>();
        testBoardPattern.put(new Coordinates(7,3), Optional.of(ItemTile.GAME));
        testBoardPattern.put(new Coordinates(7,4), Optional.of(ItemTile.CAT));

        testBoardPattern.put(new Coordinates(6,3), Optional.of(ItemTile.TROPHY));
        testBoardPattern.put(new Coordinates(6,4), Optional.of(ItemTile.TROPHY));
        testBoardPattern.put(new Coordinates(6,5), Optional.empty());

        testBoardPattern.put(new Coordinates(5,2), Optional.of(ItemTile.PLANT));
        testBoardPattern.put(new Coordinates(5,3), Optional.of(ItemTile.GAME));
        testBoardPattern.put(new Coordinates(5,4), Optional.of(ItemTile.GAME));
        testBoardPattern.put(new Coordinates(5,5), Optional.empty());
        testBoardPattern.put(new Coordinates(5,6), Optional.empty());
        testBoardPattern.put(new Coordinates(5,7), Optional.empty());

        testBoardPattern.put(new Coordinates(4,1), Optional.of(ItemTile.PLANT));
        testBoardPattern.put(new Coordinates(4,2), Optional.of(ItemTile.GAME));
        testBoardPattern.put(new Coordinates(4,3), Optional.of(ItemTile.GAME));
        testBoardPattern.put(new Coordinates(4,4), Optional.empty());
        testBoardPattern.put(new Coordinates(4,5), Optional.empty());
        testBoardPattern.put(new Coordinates(4,6), Optional.empty());
        testBoardPattern.put(new Coordinates(4,7), Optional.empty());

        testBoardPattern.put(new Coordinates(3,1), Optional.of(ItemTile.CAT));
        testBoardPattern.put(new Coordinates(3,2), Optional.of(ItemTile.CAT));
        testBoardPattern.put(new Coordinates(3,3), Optional.of(ItemTile.PLANT));
        testBoardPattern.put(new Coordinates(3,4), Optional.empty());
        testBoardPattern.put(new Coordinates(3,5), Optional.empty());
        testBoardPattern.put(new Coordinates(3,6), Optional.of(ItemTile.PLANT));

        testBoardPattern.put(new Coordinates(2,3), Optional.of(ItemTile.TROPHY));
        testBoardPattern.put(new Coordinates(2,4), Optional.of(ItemTile.TROPHY));
        testBoardPattern.put(new Coordinates(2,5), Optional.empty());

        testBoardPattern.put(new Coordinates(1,4), Optional.empty());
        testBoardPattern.put(new Coordinates(1,5), Optional.empty());

        cli.printLivingRoom(testBoardPattern);

    }
}
