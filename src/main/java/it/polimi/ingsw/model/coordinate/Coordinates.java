package it.polimi.ingsw.model.coordinate;

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
}
