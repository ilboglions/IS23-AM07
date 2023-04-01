package it.polimi.ingsw.model.cards;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;

import it.polimi.ingsw.model.cards.common.NequalsSquares;
import it.polimi.ingsw.model.exceptions.NegativeFieldException;
import it.polimi.ingsw.model.exceptions.PlayersNumberOutOfRange;
import it.polimi.ingsw.model.coordinate.Coordinates;
import it.polimi.ingsw.model.tiles.ItemTile;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class NequalsSquaresTest {
    @Test
    @DisplayName("Check exceptions")
    void checkException() {
        assertThrows(NegativeFieldException.class, () -> {
            NequalsSquares test = new NequalsSquares(2,"card Description",-1,2);
        });
        assertThrows(PlayersNumberOutOfRange.class, () -> {
            NequalsSquares test = new NequalsSquares(-1,"card Description",1,2);
        });

        assertThrows(PlayersNumberOutOfRange.class, () -> {
            NequalsSquares test = new NequalsSquares(5,"card Description",1,2);
        });

        assertThrows(NegativeFieldException.class, () -> {
            NequalsSquares test = new NequalsSquares(2,"card Description",1,-2);
        });
    }
    @Test
    @DisplayName("Check constraints")
    void checkConstraints() throws NegativeFieldException, PlayersNumberOutOfRange, FileNotFoundException {
            Gson gson = new Gson();

            JsonArray jsonCards = gson.fromJson(new FileReader("testConfFiles/bookshelf4NequalsSquares.json"), JsonArray.class);
            TypeToken<Map<Coordinates, ItemTile>> mapType = new TypeToken<>(){};

            NequalsSquares card = new NequalsSquares(2,"card Description",2,2);
            ArrayList<Boolean> expectedAnswer = new ArrayList<>();

            expectedAnswer.add(Boolean.TRUE);
            expectedAnswer.add(Boolean.TRUE);
            expectedAnswer.add(Boolean.FALSE);

          /*  jsonCards.forEach(
                    (el) -> {
                        Bookshelf bookshelf = new CardBookshelf(gson.fromJson( el.getAsJsonObject(),mapType));
                        assertEquals(expectedAnswer.iterator().next(),card.verifyConstraint( bookshelf ));

                    }
            );*/


    }

}
