package it.polimi.ingsw.model.distributable;
import it.polimi.ingsw.model.exceptions.NegativeFieldException;
import it.polimi.ingsw.model.tiles.ItemTile;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class BagHolderTest {

    @Test
    @DisplayName("Test all the exceptions")
    void testExceptions() {
        assertThrows(NegativeFieldException.class, ()-> {
            BagHolder test = new BagHolder();
            test.draw(-1);
        });
    }

    @Test
    @DisplayName("Test the limits cases of draw method")
    void draw() throws NegativeFieldException {
        BagHolder test = new BagHolder();

        ArrayList<ItemTile> selected = test.draw(150);
        assertEquals(150, selected.size());

        selected = test.draw(0);
        assertEquals(0, selected.size());

        ItemTile testClass = ItemTile.FRAME;
        selected = test.draw(1);
        assertEquals(testClass.getClass(), selected.get(0).getClass());
    }
}