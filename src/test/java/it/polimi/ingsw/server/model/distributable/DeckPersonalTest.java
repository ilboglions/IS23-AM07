package it.polimi.ingsw.server.model.distributable;

import it.polimi.ingsw.server.model.cards.personal.PersonalGoalCard;
import it.polimi.ingsw.server.model.exceptions.IllegalFilePathException;
import it.polimi.ingsw.server.model.exceptions.NegativeFieldException;
import it.polimi.ingsw.server.model.exceptions.NotEnoughCardsException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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
        assertThrows(IllegalFilePathException.class, ()->{
            DeckPersonal test = new DeckPersonal("", "");
            test.draw(3);
        });
        assertThrows(IllegalFilePathException.class, ()->{
            DeckPersonal test = new DeckPersonal("deckPersonal.json", "");
            test.draw(3);
        });
        assertThrows(IllegalFilePathException.class, ()->{
            DeckPersonal test = new DeckPersonal("", "pointsReference.json");
            test.draw(3);
        });
        assertThrows(NotEnoughCardsException.class, ()->{
            DeckPersonal test = new DeckPersonal("deckPersonalTest.json", "pointsReference.json");
            test.draw(100);
        });
        assertThrows(NegativeFieldException.class, ()->{
            DeckPersonal test = new DeckPersonal("deckPersonalTest.json", "pointsReference.json");
            test.draw(-1);
        });
        assertThrows(NullPointerException.class, ()->{
            DeckPersonal test = new DeckPersonal(null, "pointsReference.json");
        });
        assertThrows(NullPointerException.class, ()->{
            DeckPersonal test = new DeckPersonal("deckPersonalTest.json", null);
        });
    }

    /**
     * This tests if the draw method is working correctly
     * @throws IllegalFilePathException if the path for the configuration file is not found
     * @throws NotEnoughCardsException if there aren't as many cards as the ones asked for
     * @throws NegativeFieldException if the number of cards to draw is negative
     */
    @Test
    @DisplayName("Test the limits cases of draw method")
    void draw() throws NotEnoughCardsException, NegativeFieldException, IllegalFilePathException {
        DeckPersonal test = new DeckPersonal("deckPersonalTest.json", "pointsReference.json");

        ArrayList<PersonalGoalCard> extracted = test.draw(3);
        assertEquals(3, extracted.size());

        assertThrows(NegativeFieldException.class, () -> {
            ArrayList<PersonalGoalCard> forTest = test.draw(0);
        });

        extracted = test.draw(1);
        assertInstanceOf(PersonalGoalCard.class, extracted.get(0));
    }
}