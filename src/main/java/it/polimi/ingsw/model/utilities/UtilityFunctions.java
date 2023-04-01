package it.polimi.ingsw.model.utilities;

import it.polimi.ingsw.model.bookshelf.Bookshelf;
import it.polimi.ingsw.model.coordinate.Coordinates;
import it.polimi.ingsw.model.tiles.ItemTile;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class UtilityFunctions {
    /**
     * findAdjacentElements is a recursive function used to find all same elements that are adjacent.
     * @param bookshelf the bookshelf to search in
     * @param row the row-coordinate to check
     * @param col the col-coordinate to check
     * @param itemTile the tile used for reference
     * @param visited a set containing all the Coordinates already visited
     * @return a list of coordinates that are adjacent
     */
    public static List<Coordinates> findAdjacentElements(Bookshelf bookshelf, int row, int col, ItemTile itemTile, Set<Coordinates> visited) {

        List<Coordinates> group = new ArrayList<>();
        if (row < 0 || row >= bookshelf.getRows() || col < 0 || col >= bookshelf.getColumns() || visited.contains(new Coordinates(row,col)) || bookshelf.getItemTile(new Coordinates(row,col)).isEmpty()
                || (bookshelf.getItemTile(new Coordinates(row,col)).isPresent() && !bookshelf.getItemTile(new Coordinates(row,col)).get().equals(itemTile))) {
            return new ArrayList<>();
        }

        group.add(new Coordinates(row, col));
        visited.add(new Coordinates(row,col));

        group.addAll(findAdjacentElements(bookshelf, row + 1, col, itemTile,visited));
        group.addAll(findAdjacentElements(bookshelf, row, col + 1, itemTile, visited));
        group.addAll(findAdjacentElements(bookshelf, row - 1, col, itemTile,visited));
        group.addAll(findAdjacentElements(bookshelf, row, col - 1, itemTile, visited));

        return  group.stream().distinct().collect(Collectors.toList());
    }
}
