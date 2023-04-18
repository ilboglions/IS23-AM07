package it.polimi.ingsw.model.livingRoom;

import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw.server.model.exceptions.PlayersNumberOutOfRange;
import it.polimi.ingsw.server.model.livingRoom.LivingRoomBoard;
import it.polimi.ingsw.server.model.livingRoom.Slot;
import it.polimi.ingsw.server.model.livingRoom.SlotType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import com.google.gson.Gson;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class LivingRoomBoardTest {

    /**
     * This test verifies the correctness of the constructor method, exception cases
     * and Board filling
     * @throws FileNotFoundException
     * @throws PlayersNumberOutOfRange
     */
    @Test
    @DisplayName("LivingRoomBoardTester")
    void ConstructorTest() throws FileNotFoundException, PlayersNumberOutOfRange {
        assertThrows(PlayersNumberOutOfRange.class, () ->{ new LivingRoomBoard(0);});
        assertThrows(PlayersNumberOutOfRange.class, () ->{ new LivingRoomBoard(5);});
        LivingRoomBoard testboard = new LivingRoomBoard(4);
        String confFilePath = "src/main/java/it/polimi/ingsw/model/livingRoom/confFiles/4orMorePlayersPattern.json";
        Gson gson = new Gson();
        LivingRoomBoard.JsonLivingBoardCell[][] jsonCells = gson.fromJson(new FileReader(confFilePath), LivingRoomBoard.JsonLivingBoardCell[][].class);
        SlotType slotType;
        Slot[][] slotmatrix = testboard.getAllSlots();
        for(int i=0; i<9; i++) {
            for(int j=0; j<9; j++) {
                slotType = jsonCells[i][j].slotType;
                assertEquals(slotType, slotmatrix[i][j].getSlotType());
            }
        }
        testboard = new LivingRoomBoard(3);
        confFilePath = "src/main/java/it/polimi/ingsw/model/livingRoom/confFiles/3PlayersPattern.json";
        gson = new Gson();
        jsonCells = gson.fromJson(new FileReader(confFilePath), LivingRoomBoard.JsonLivingBoardCell[][].class);
        slotmatrix = testboard.getAllSlots();
        for(int i=0; i<9; i++) {
            for(int j=0; j<9; j++) {
                slotType = jsonCells[i][j].slotType;
                assertEquals(slotType, slotmatrix[i][j].getSlotType());
            }
        }
        testboard = new LivingRoomBoard(2);
        confFilePath = "src/main/java/it/polimi/ingsw/model/livingRoom/confFiles/2PlayersPattern.json";
        gson = new Gson();
        jsonCells = gson.fromJson(new FileReader(confFilePath), LivingRoomBoard.JsonLivingBoardCell[][].class);
        slotmatrix = testboard.getAllSlots();
        for(int i=0; i<9; i++) {
            for(int j=0; j<9; j++) {
                slotType = jsonCells[i][j].slotType;
                assertEquals(slotType, slotmatrix[i][j].getSlotType());
            }
        }
    }











}
