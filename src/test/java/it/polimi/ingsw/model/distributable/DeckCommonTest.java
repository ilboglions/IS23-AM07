package it.polimi.ingsw.model.distributable;

import it.polimi.ingsw.model.cards.common.CommonGoalCard;
import it.polimi.ingsw.model.exceptions.NegativeFieldException;
import it.polimi.ingsw.model.exceptions.NotEnoughCardsException;
import it.polimi.ingsw.model.exceptions.PlayersNumberOutOfRange;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Class used to test DeckCommon class
 */
class DeckCommonTest {

    /**
     * This tests if the exception are firing correctly for player number out of range, negative number of cards to draw, null values or unknown file path
     */
    @Test
    @DisplayName("Test all the exceptions")
    void testExceptions() {
        assertThrows(FileNotFoundException.class, ()->{
            DeckCommon test = new DeckCommon(3, "");
            test.draw(5);
        });
        assertThrows(PlayersNumberOutOfRange.class, ()->{
            DeckCommon test = new DeckCommon(-1, "src/test/java/it/polimi/ingsw/model/distributable/testConfFiles/deckCommonTest.json");
        });
        assertThrows(PlayersNumberOutOfRange.class, ()->{
            DeckCommon test = new DeckCommon(10, "src/test/java/it/polimi/ingsw/model/distributable/testConfFiles/deckCommonTest.json");
        });
        assertThrows(NegativeFieldException.class, ()->{
            DeckCommon test = new DeckCommon(4, "src/test/java/it/polimi/ingsw/model/distributable/testConfFiles/deckCommonTest.json");
            test.draw(-1);
        });
        assertThrows(NotEnoughCardsException.class, ()->{
            DeckCommon test = new DeckCommon(4, "src/test/java/it/polimi/ingsw/model/distributable/testConfFiles/deckCommonTest.json");
            test.draw(100);
        });
        assertThrows(IllegalArgumentException.class, ()->{
           DeckCommon test = new DeckCommon(4, "src/test/java/it/polimi/ingsw/model/distributable/testConfFiles/deckCommonTestIllegal.json");
           test.draw(1);
        });
        assertThrows(NullPointerException.class, ()->{
           DeckCommon test = new DeckCommon(3, null);
        });
    }

    /**
     * This tests if the draw method is working appropriately
     * @throws PlayersNumberOutOfRange if the number of player is less than 2 or grater than 4
     * @throws NegativeFieldException if the number of cards to draw is negative
     * @throws FileNotFoundException if the path for the configuration file is not found
     * @throws NotEnoughCardsException if there aren't as many cards as the ones asked for
     */
    @Test
    @DisplayName("Test the limits cases of draw method")
    void draw() throws PlayersNumberOutOfRange, NegativeFieldException, FileNotFoundException, NotEnoughCardsException {
        DeckCommon test = new DeckCommon(3, "src/test/java/it/polimi/ingsw/model/distributable/testConfFiles/deckCommonTest.json");
        ArrayList<CommonGoalCard> selected = test.draw(13);

        assertEquals(13, selected.size());
        assertInstanceOf(CommonGoalCard.class, selected.get(0));

        selected = test.draw(0);
        assertEquals(0, selected.size());
    }
}