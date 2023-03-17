package it.polimi.ingsw.model.coordinate;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
public class CoordinatesTest {
    @Test
    @DisplayName("Check exception")
    void checkException() {
        assertThrows(IndexOutOfBoundsException.class, () -> {
            Coordinates test = new Coordinates(-1,-1);
        });
        assertThrows(IndexOutOfBoundsException.class, () -> {
            Coordinates test = new Coordinates(9,0);
        });
        assertThrows(IndexOutOfBoundsException.class, () -> {
            Coordinates test = new Coordinates(3,9);
        });
    }
    @Test
    @DisplayName("Generic Value")
    void checkGenericValue() {
        Coordinates test1 = new Coordinates(0,0);
        Coordinates test2 = new Coordinates(0,8);
        Coordinates test3 = new Coordinates(8,0);
        Coordinates test4 = new Coordinates(3,5);
        assertEquals(0,test1.getX());
        assertEquals(0,test1.getY());
        assertEquals(0,test2.getX());
        assertEquals(8,test2.getY());
        assertEquals(8,test3.getX());
        assertEquals(0,test3.getY());
        assertEquals(3,test4.getX());
        assertEquals(5,test4.getY());

    }

}
