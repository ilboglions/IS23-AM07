package it.polimi.ingsw.model.coordinate;

public class Coordinates {

    private final int x,y;

    public Coordinates(int x, int y) {
        if(x < 0 || y < 0 || x > 8 || y > 8) {
            throw new IndexOutOfBoundsException("Given coordinates are out of range");
        }
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
