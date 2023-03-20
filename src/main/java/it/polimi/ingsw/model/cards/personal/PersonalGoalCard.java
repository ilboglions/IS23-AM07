package it.polimi.ingsw.model.cards.personal;

import it.polimi.ingsw.model.bookshelf.CardBookshelf;
import it.polimi.ingsw.model.coordinate.Coordinates;
import it.polimi.ingsw.model.tiles.ItemTile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PersonalGoalCard{
    private final CardBookshelf bookshelf;
    private final Map<Integer,Integer> pointsReference;

    public PersonalGoalCard(Map<Coordinates, ItemTile> pattern, Map<Integer,Integer> pointsReference){
        bookshelf = new CardBookshelf(pattern);
        this.pointsReference = new HashMap<>(pointsReference);
    }

    public CardBookshelf getBookshelf() {

        Map<Coordinates, ItemTile> tempPattern = new HashMap<>();
        Coordinates tempCoord;
        for( int r = 0; r < bookshelf.getRows(); r++){
            for( int c = 0; c < bookshelf.getColumns(); c++){
                tempCoord = new Coordinates(r,c);
                Coordinates finalTempCoord = tempCoord;
                this.bookshelf.getItemTile(tempCoord).ifPresent(el -> tempPattern.put(finalTempCoord, el));
            }
        }
        return new CardBookshelf(tempPattern);
    }

    public Map<Integer, Integer> getPointsReference() {
        return new HashMap<Integer,Integer>(pointsReference);
    }
}
