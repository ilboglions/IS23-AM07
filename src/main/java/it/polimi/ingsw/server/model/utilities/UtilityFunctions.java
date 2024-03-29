package it.polimi.ingsw.server.model.utilities;

import it.polimi.ingsw.server.model.bookshelf.Bookshelf;
import it.polimi.ingsw.server.model.coordinate.Coordinates;
import it.polimi.ingsw.server.model.exceptions.InvalidCoordinatesException;
import it.polimi.ingsw.server.model.tiles.ItemTile;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Class that contains some values and methods used by multiple classes
 */
public class UtilityFunctions {
    /**
     * defines the max players number
     */
    public static final int MAX_PLAYERS = 4;

    /**
     * findAdjacentElements is a recursive function used to find all same elements that are adjacent.
     * @param bookshelf the bookshelf to search in
     * @param row the row-coordinate to check
     * @param col the col-coordinate to check
     * @param itemTile the tile used for reference
     * @param visited a set containing all the Coordinates already visited
     * @return a list of coordinates that are adjacent
     */
    public static List<Coordinates> findAdjacentElements(Bookshelf bookshelf, int row, int col, ItemTile itemTile, Set<Coordinates> visited){

        List<Coordinates> group = new ArrayList<>();
        try {
            if (row < 0 || row >= bookshelf.getRows() || col < 0 || col >= bookshelf.getColumns() || visited.contains(new Coordinates(row, col)) || bookshelf.getItemTile(new Coordinates(row, col)).isEmpty()
                    || (bookshelf.getItemTile(new Coordinates(row, col)).isPresent() && !bookshelf.getItemTile(new Coordinates(row, col)).get().equals(itemTile))) {
                return new ArrayList<>();
            }

            group.add(new Coordinates(row, col));
            visited.add(new Coordinates(row, col));

            group.addAll(findAdjacentElements(bookshelf, row + 1, col, itemTile, visited));
            group.addAll(findAdjacentElements(bookshelf, row, col + 1, itemTile, visited));
            group.addAll(findAdjacentElements(bookshelf, row - 1, col, itemTile, visited));
            group.addAll(findAdjacentElements(bookshelf, row, col - 1, itemTile, visited));
        }
        catch (InvalidCoordinatesException ignored){} //EXCEPTION IGNORED BECAUSE IT WILL NEVER BE THROWN
        return  group.stream().distinct().collect(Collectors.toList());
    }

    /**
     * This method is used to evaluate whether a string is numeric (can be converted to a number)
     * @param str string to be evaluated
     * @return if the string represents a number, false otherwise
     */
    public static boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
