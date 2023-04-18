package it.polimi.ingsw.server.model.distributable;

import it.polimi.ingsw.server.model.cards.personal.PersonalGoalCard;
import it.polimi.ingsw.server.model.distributable.DeckPersonal;
import it.polimi.ingsw.server.model.exceptions.NegativeFieldException;
import it.polimi.ingsw.server.model.exceptions.NotEnoughCardsException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Class used to test DeckPersonal class
 */
class DeckPersonalTest {

    /**
     * This tests if the exception are firing correctly for negative number of cards to draw, null values or unknown file path
     */
    @Test
    @DisplayName("Test all the exceptions")
    void testExceptions() {
        assertThrows(FileNotFoundException.class, ()->{
            DeckPersonal test = new DeckPersonal("", "");
            test.draw(3);
        });
        assertThrows(FileNotFoundException.class, ()->{
            DeckPersonal test = new DeckPersonal("src/test/java/it/polimi/ingsw/model/distributable/testConfFiles/deckPersonalTest.json", "");
            test.draw(3);
        });
        assertThrows(FileNotFoundException.class, ()->{
            DeckPersonal test = new DeckPersonal("", "src/test/java/it/polimi/ingsw/model/distributable/testConfFiles/pointsReferenceTest.json");
            test.draw(3);
        });
        assertThrows(NotEnoughCardsException.class, ()->{
            DeckPersonal test = new DeckPersonal("src/test/java/it/polimi/ingsw/model/distributable/testConfFiles/deckPersonalTest.json", "src/test/java/it/polimi/ingsw/model/distributable/testConfFiles/pointsReferenceTest.json");
            test.draw(100);
        });
        assertThrows(NegativeFieldException.class, ()->{
            DeckPersonal test = new DeckPersonal("src/test/java/it/polimi/ingsw/model/distributable/testConfFiles/deckPersonalTest.json", "src/test/java/it/polimi/ingsw/model/distributable/testConfFiles/pointsReferenceTest.json");
            test.draw(-1);
        });
        assertThrows(NullPointerException.class, ()->{
            DeckPersonal test = new DeckPersonal(null, "src/test/java/it/polimi/ingsw/model/distributable/testConfFiles/pointsReferenceTest.json");
        });
        assertThrows(NullPointerException.class, ()->{
            DeckPersonal test = new DeckPersonal("src/test/java/it/polimi/ingsw/model/distributable/testConfFiles/deckPersonalTest.json", null);
        });
    }

    /**
     * This tests if the draw method is working correctly
     * @throws FileNotFoundException if the path for the configuration file is not found
     * @throws NotEnoughCardsException if there aren't as many cards as the ones asked for
     * @throws NegativeFieldException if the number of cards to draw is negative
     */
    @Test
    @DisplayName("Test the limits cases of draw method")
    void draw() throws FileNotFoundException, NotEnoughCardsException, NegativeFieldException {
        DeckPersonal test = new DeckPersonal("src/test/java/it/polimi/ingsw/model/distributable/testConfFiles/deckPersonalTest.json", "src/test/java/it/polimi/ingsw/model/distributable/testConfFiles/pointsReferenceTest.json");

        ArrayList<PersonalGoalCard> extracted = test.draw(3);
        assertEquals(3, extracted.size());

        extracted = test.draw(0);
        assertEquals(0, extracted.size());

        extracted = test.draw(1);
        assertInstanceOf(PersonalGoalCard.class, extracted.get(0));
    }
}