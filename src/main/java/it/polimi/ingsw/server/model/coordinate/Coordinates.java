package it.polimi.ingsw.server.model.coordinate;

import it.polimi.ingsw.server.model.exceptions.InvalidCoordinatesException;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * the Coordinates class is a useful and immutable class used in many method
 */
public class Coordinates implements Serializable {
    @Serial
    private static final long serialVersionUID = -3104092465697470225L;
    /**
     * the attributes are the position of the coordinate in x and y axes
     */
    private final int row,column;

    /**
     * creates an immutable object representing the coordinate point
     * @param row - the x coordinate to be assigned
     * @param column - the y coordinate to be assigned
     * @throws InvalidCoordinatesException if the row or column param is less than 0 or grater than 8
     */
    public Coordinates(int row, int column) throws InvalidCoordinatesException {
        if(row < 0 || column < 0 || row > 8 || column > 8){
            throw new InvalidCoordinatesException("Given coordinates are out of range");
        }
        this.row = row;
        this.column = column;
    }

    /**
     *
     * @return the x coordinate
     */
    public int getColumn() {
        return column;
    }

    /**
     *
     * @return the y coordinate
     */
    public int getRow() {
        return row;
    }

    /**
     * override of the classical equals method
     * @param o the object to be compared with
     * @return true, if elements are the same class and contains the same attributes
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coordinates that = (Coordinates) o;
        return row == that.row && column == that.column;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, column);
    }
}
