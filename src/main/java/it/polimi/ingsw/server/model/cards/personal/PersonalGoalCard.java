package it.polimi.ingsw.server.model.cards.personal;

import it.polimi.ingsw.remoteInterfaces.RemotePersonalGoalCard;
import it.polimi.ingsw.server.model.bookshelf.CardBookshelf;
import it.polimi.ingsw.server.model.coordinate.Coordinates;
import it.polimi.ingsw.server.model.exceptions.InvalidCoordinatesException;
import it.polimi.ingsw.server.model.tiles.ItemTile;

import java.io.Serial;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * PersonalGoalCard represent the card assigned to a specific player. They represent a pattern that should be respected for earning more points
 */
public class PersonalGoalCard extends UnicastRemoteObject implements RemotePersonalGoalCard {
    @Serial
    private static final long serialVersionUID = 158808678662870084L;
    /**
     * this attribute assign an immutable bookshelf to the card, in order to represent the pattern to follow.
     */
    private final CardBookshelf bookshelf;
    /**
     * the pointsReference attribute map the number of matching elements and the points assigned
     */
    private final Map<Integer,Integer> pointsReference;
    /**
     * the card's identifier
     */
    private final int id;

    /**
     * the personal card is an immutable class, so the constructor is used to assign all the necessary values
     * @param pattern is the pattern to be load in the card bookshelf assigned to the card
     * @param pointsReference is the parameter to be assigned to the point reference attribute
     * @param id the identifier of the card
     * @throws RemoteException RMI Exception
     */
    public PersonalGoalCard(Map<Coordinates, ItemTile> pattern, Map<Integer,Integer> pointsReference, int id) throws RemoteException {
        super();
        Objects.requireNonNull(pattern, "You passed a null instead of a pattern Map");
        Objects.requireNonNull(pointsReference, "You passed a null instead of a pointsReference Map");

        if(pattern.isEmpty() || pointsReference.isEmpty())
            throw new IllegalArgumentException("You passed an empty parameter!");
        this.id = id;
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
                    this.bookshelf.getItemTile(tempCoord).ifPresent(el -> tempPattern.put(tempCoord, el));
                } catch (InvalidCoordinatesException ignored) { //THIS EXCEPTION IS NEVER THROWN
                }
            }
        }
        return new CardBookshelf(tempPattern);
    }

    /**
     *
     * @return the pattern associated to this card
     * @throws RemoteException RMI Exception
     */
    public Map<Coordinates, ItemTile> getCardPattern() throws RemoteException{
        Map<Coordinates, ItemTile> itemTileMap = new HashMap<>();
        for( int r = 0; r < bookshelf.getRows(); r++){
            for( int c = 0; c < bookshelf.getColumns(); c++) {
                Coordinates tempCoord;
                try {
                    tempCoord = new Coordinates(r, c);
                } catch (InvalidCoordinatesException e) {
                    throw new RuntimeException(e);
                }
                this.bookshelf.getItemTile(tempCoord).ifPresent(el -> itemTileMap.put(tempCoord, el));
            }
        }
        return itemTileMap;
    }

    /**
     *
     * @return the serial ID of this card
     * @throws RemoteException RMI Exception
     */
    @Override
    public int getID() throws RemoteException {
        return this.id;
    }

    /**
     * a simple getter method that returns the point reference of the card
     * @return the points reference used to evaluate the points acquired
     * @throws RemoteException RMI Exception
     */
    public Map<Integer, Integer> getPointsReference() throws RemoteException {
        return new HashMap<>(pointsReference);
    }
}
