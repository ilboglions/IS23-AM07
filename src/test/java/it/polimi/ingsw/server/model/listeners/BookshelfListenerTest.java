package it.polimi.ingsw.server.model.listeners;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;


public class BookshelfListenerTest {
    /**
     * Test to call all the methods of the listener
     */
    @Test
    @DisplayName("Test all the methods")
    void fullTest(){
        BookshelfListener test = new BookshelfListener();
        GeneralSubscriber sub1 = new GeneralSubscriber("sub1");
        GeneralSubscriber sub2 = new GeneralSubscriber("sub2");

        test.addSubscriber(sub1);
        test.addSubscriber(sub2);

        test.onBookshelfChange("sub1", new ArrayList<>(), 0, new HashMap<>());
        test.triggerListener("sub2", new HashMap<>(), "sub1");
    }

}