package it.polimi.ingsw.model.coordinate;

import java.util.Objects;

/**
 * the Coordinates class is a useful and immutable class used in many method
 */
public class Coordinates {

    /**
     * the attributes are the position of the coordinate in x and y axes
     */
    private final int x,y;

    /**
     * creates an immutable object representing the coordinate point
     * @param x - the x coordinate to be assigned
     * @param y - the y coordinate to be assigned
     */
    public Coordinates(int x, int y) {
        if(x < 0 || y < 0 || x > 8 || y > 8) {
            throw new IndexOutOfBoundsException("Given coordinates are out of range");
        }
        this.x = x;
        this.y = y;
    }

    /**
     *
     * @return the x coordinate
     */
    public int getX() {
        return x;
    }

    /**
     *
     * @return the y coordinate
     */
    public int getY() {
        return y;
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
        return x == that.x && y == that.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
