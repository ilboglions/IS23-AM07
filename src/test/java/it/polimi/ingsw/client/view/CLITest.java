package it.polimi.ingsw.client.view;


import it.polimi.ingsw.server.model.coordinate.Coordinates;
import it.polimi.ingsw.server.model.exceptions.InvalidCoordinatesException;
import it.polimi.ingsw.server.model.tiles.ItemTile;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;

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
    @DisplayName("Test the print of the bookshelf")
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
    }
}
