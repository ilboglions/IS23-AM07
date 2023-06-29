package it.polimi.ingsw.server.model.bookshelf;

import it.polimi.ingsw.server.model.exceptions.InvalidCoordinatesException;
import it.polimi.ingsw.server.model.exceptions.NotEnoughSpaceException;
import it.polimi.ingsw.server.model.coordinate.Coordinates;
import it.polimi.ingsw.server.model.listeners.BookshelfListener;
import it.polimi.ingsw.server.model.player.Player;
import it.polimi.ingsw.remoteInterfaces.BookshelfSubscriber;
import it.polimi.ingsw.server.model.tiles.ItemTile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * the player bookshelf extends the bookshelf class, it is a mutable class, used for stores players tiles
 */
public class PlayerBookshelf extends Bookshelf{

    private final Player player;
    private static final int MAXTILES = 3;

    private final BookshelfListener bookshelfListener;


    /**
     * Methods that subscribe a subscriber to the listener of the player's bookshelf
     * @param subscriber the subscriber that needs to be subscribed to the listener
     */
    public void subscribeToListener(BookshelfSubscriber subscriber){
        bookshelfListener.addSubscriber(subscriber);
    }

    /**
     * Constructor method to create an istance of the PlayerBookshelf
     * @param player the player that owns the bookshelf
     */
    public PlayerBookshelf(Player player){
        super();
        this.player = player;
        this.bookshelfListener = new BookshelfListener();
    }

    /**
     * the method make it possible to insert items in a column
     * @param column the columns where items will be inserted
     * @param orderedTiles the tiles, in order of insertion, the first one will go to the bottom and above all the others
     * @throws NotEnoughSpaceException if the column is full, no item will be added
     */
    public void insertItemTile(int column, ArrayList<ItemTile> orderedTiles) throws NotEnoughSpaceException {
        Objects.requireNonNull(orderedTiles);
        if(orderedTiles.size() > MAXTILES)
            throw new IllegalArgumentException("The maximum number of tiles to insert inside the bookshelf is " + MAXTILES + "you passed " + orderedTiles.size());

        if(column < 0 || column >= this.columns)
            throw new IllegalArgumentException("Column number needs to be > 0 and < " + this.columns);

        if(checkFreeSpace(column) >= orderedTiles.size()) {
            int firstFreeIndex = 0;

            while(this.tiles[firstFreeIndex][column] != null) {
                firstFreeIndex++;
            }

            for (ItemTile orderedTile : orderedTiles) {
                this.tiles[firstFreeIndex][column] = orderedTile;
                firstFreeIndex++;
            }

            bookshelfListener.onBookshelfChange(player.getUsername(), orderedTiles, column, this.getItemTileMap());
        }
        else
            throw new NotEnoughSpaceException("There isn't enough space in this column!");
    }

    /**
     * is a useful method that make it possible to check the free space in a column
     * @param column the column to be checked
     * @return the free space in that column
     */
    public int checkFreeSpace(int column) {
        int count = 0;

        for(int i=0; i<this.getRows(); i++) {
            if(this.tiles[i][column] == null)
                count++;
        }

        return count;
    }

    /**
     * This method checks if the bookshelf is complete, in other words it checks if in every index of the matrix there is an ItemTile
     * @return if the bookshelf is complete or not
     */
    public boolean checkComplete() {
        Coordinates c;

        for(int j=0; j < this.columns; j++) {
            try {
                c = new Coordinates(this.rows-1,j);
            } catch (InvalidCoordinatesException e) {
                return false;
            }
            if(this.getItemTile(c).isEmpty())
                return false;
        }

        return true;
    }

    /**
     * Method used to trigger the listener manually
     * @param userToBeUpdated the username of the user that needs the update
     */
    public void triggerListener(String userToBeUpdated){
        Objects.requireNonNull(userToBeUpdated);

        this.bookshelfListener.triggerListener(userToBeUpdated, this.getItemTileMap(), this.player.getUsername());
    }

    /**
     * This method remove a subscriber from the bookshelf listener
     * @param username the username of the subscriber to remove
     */
    public void unsubscribeFromListener(String username) {
        this.bookshelfListener.removeSubscriber(username);
    }

    /**
     * Getter for the subscribers list
     * @return the list of the subscriber to the bookshelf listener
     */
    public Set<BookshelfSubscriber> getSubs() {
            return new HashSet<>(this.bookshelfListener.getSubscribers());
    }

}
