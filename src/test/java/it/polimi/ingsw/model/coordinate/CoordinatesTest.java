package it.polimi.ingsw.model.coordinate;

import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw.model.exceptions.InvalidCoordinatesException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
public class CoordinatesTest {
    @Test
    @DisplayName("Check exception")
    void checkException() {
        assertThrows(InvalidCoordinatesException.class, () -> {
            Coordinates test = new Coordinates(-1,-1);
        });
        assertThrows(InvalidCoordinatesException.class, () -> {
            Coordinates test = new Coordinates(9,0);
        });
        assertThrows(InvalidCoordinatesException.class, () -> {
            Coordinates test = new Coordinates(3,9);
        });
    }
    @Test
    @DisplayName("Generic Value")
    void checkGenericValue() throws InvalidCoordinatesException {
        Coordinates test1 = new Coordinates(0,0);
        Coordinates test2 = new Coordinates(0,8);
        Coordinates test3 = new Coordinates(8,0);
        Coordinates test4 = new Coordinates(3,5);
        assertEquals(0,test1.getRow());
        assertEquals(0,test1.getColumn());
        assertEquals(0,test2.getRow());
        assertEquals(8,test2.getColumn());
        assertEquals(8,test3.getRow());
        assertEquals(0,test3.getColumn());
        assertEquals(3,test4.getRow());
        assertEquals(5,test4.getColumn());
    }

    @Test
    @DisplayName("Test equals method")
    void testEquals() throws InvalidCoordinatesException {
        Coordinates test = new Coordinates(0,0);

        assertEquals(test, new Coordinates(0, 0));
        assertNotEquals(test, new Coordinates(1, 3));
    }

}
