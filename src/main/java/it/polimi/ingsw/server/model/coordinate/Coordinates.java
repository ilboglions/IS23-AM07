package it.polimi.ingsw.server.model.coordinate;

import it.polimi.ingsw.server.model.exceptions.InvalidCoordinatesException;

import javax.swing.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

import static it.polimi.ingsw.server.model.utilities.UtilityFunctions.isNumeric;

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
            throw new InvalidCoordinatesException("Given coordinates are out of range! row:"+row+" col:"+column);
        }
        this.row = row;
        this.column = column;
    }

    /**
     * creates an immutable object representing the coordinate point
     * @param coordinates a string in a form such as ( 3, 5 ) or 3,5 or (3,5)
     * @throws InvalidCoordinatesException if the format is not correct, or the coordinate is out of range
     */
    public Coordinates(String coordinates) throws InvalidCoordinatesException {
        coordinates = coordinates.replace(" ","").replace("(","").replace(")","");

        String[] coordArray = coordinates.split(",");

        if(coordArray.length < 2 ) throw  new InvalidCoordinatesException("coordinate format is invalid");

        String rowS = coordArray[0];
        String colS = coordArray[1];

        int row,column;
        if(isNumeric(rowS))
            row = Integer.parseInt(rowS);
        else
            throw new InvalidCoordinatesException("row is not a number");

        if(isNumeric(colS))
           column = Integer.parseInt(colS);
        else
            throw new InvalidCoordinatesException("column is not a number");

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
