package it.polimi.ingsw.server.model.cards.personal;

import it.polimi.ingsw.server.model.bookshelf.CardBookshelf;
import it.polimi.ingsw.server.model.coordinate.Coordinates;
import it.polimi.ingsw.server.model.exceptions.InvalidCoordinatesException;
import it.polimi.ingsw.server.model.tiles.ItemTile;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * PersonalGoalCard represent the card assigned to a specific player. They represent a pattern that should be respected for earning more points
 */
public class PersonalGoalCard{
    /**
     * this attribute assign an immutable bookshelf to the card, in order to represent the pattern to follow.
     */
    private final CardBookshelf bookshelf;
    /**
     * the pointsReference attribute map the number of matching elements and the points assigned
     */
    private final Map<Integer,Integer> pointsReference;

    /**
     * the personal card is an immutable class, so the constructor is used to assign all the necessary values
     * @param pattern is the pattern to be load in the card bookshelf assigned to the card
     * @param pointsReference is the parameter to be assigned to the point reference attribute
     */
    public PersonalGoalCard(Map<Coordinates, ItemTile> pattern, Map<Integer,Integer> pointsReference){
        Objects.requireNonNull(pattern, "You passed a null instead of a pattern Map");
        Objects.requireNonNull(pointsReference, "You passed a null instead of a pointsReference Map");

        if(pattern.isEmpty() || pointsReference.isEmpty())
            throw new IllegalArgumentException("You passed an empty parameter!");

        bookshelf = new CardBookshelf(pattern);
        this.pointsReference = new HashMap<>(pointsReference);
    }

    /**
     * The getBookshelf method returns the card bookshelf that represent the pattern of the card
     * @return a copy of the immutable bookshelf assigned to the card
     */
    public CardBookshelf getBookshelf() {

        Map<Coordinates, ItemTile> tempPattern = new HashMap<>();
        for( int r = 0; r < bookshelf.getRows(); r++){
            for( int c = 0; c < bookshelf.getColumns(); c++) {
                Coordinates tempCoord;
                try {
                    tempCoord = new Coordinates(r, c);
                } catch (InvalidCoordinatesException e) {
                    throw new RuntimeException(e);
                }
                this.bookshelf.getItemTile(tempCoord).ifPresent(el -> tempPattern.put(tempCoord, el));
            }
        }
        return new CardBookshelf(tempPattern);
    }

    /**
     * a simple getter method that returns the point reference of the card
     * @return the points reference used to evaluate the points acquired
     */
    public Map<Integer, Integer> getPointsReference() {
        return new HashMap<>(pointsReference);
    }
}
