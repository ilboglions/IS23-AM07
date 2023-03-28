package it.polimi.ingsw.model.distributable;
import it.polimi.ingsw.model.exceptions.NegativeFieldException;
import it.polimi.ingsw.model.tiles.ItemTile;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class BagHolderTest {

    @Test
    void testExceptions() {
        assertThrows(NegativeFieldException.class, ()-> {
            BagHolder test = new BagHolder();
            test.draw(-1);
        });
    }

    @Test
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