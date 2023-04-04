package it.polimi.ingsw.model.distributable;

import it.polimi.ingsw.model.cards.personal.PersonalGoalCard;
import it.polimi.ingsw.model.exceptions.NotEnoughCardsException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class DeckPersonalTest {

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
        assertThrows(NullPointerException.class, ()->{
            DeckPersonal test = new DeckPersonal(null, "src/test/java/it/polimi/ingsw/model/distributable/testConfFiles/pointsReferenceTest.json");
        });
        assertThrows(NullPointerException.class, ()->{
            DeckPersonal test = new DeckPersonal("src/test/java/it/polimi/ingsw/model/distributable/testConfFiles/deckPersonalTest.json", null);
        });
    }

    @Test
    @DisplayName("Test the limits cases of draw method")
    void draw() throws FileNotFoundException, NotEnoughCardsException {
        DeckPersonal test = new DeckPersonal("src/test/java/it/polimi/ingsw/model/distributable/testConfFiles/deckPersonalTest.json", "src/test/java/it/polimi/ingsw/model/distributable/testConfFiles/pointsReferenceTest.json");

        ArrayList<PersonalGoalCard> extracted = test.draw(3);
        assertEquals(3, extracted.size());

        extracted = test.draw(0);
        assertEquals(0, extracted.size());

        extracted = test.draw(1);
        assertInstanceOf(PersonalGoalCard.class, extracted.get(0));
    }
}