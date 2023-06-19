package it.polimi.ingsw.server.model.livingRoom;

import it.polimi.ingsw.server.model.tiles.ItemTile;

import java.util.Optional;

/**
 * Every slot represents a cell of the board
 */
public class Slot {
    private final SlotType slotType;
    private Optional<ItemTile> itemTile;

    /**
     * Constructor of a slot specifying type and content of it
     * @param slotType type of the slot
     * @param itemTile content of the slot
     */
    public Slot(SlotType slotType, Optional<ItemTile> itemTile) {
        this.slotType = slotType;
        this.itemTile = itemTile;
    }

    /**
     *
     * @return the type of the slot
     */
    public SlotType getSlotType() {
        return slotType;
    }

    /**
     * @return an optional of the content of the slot
     */
    public Optional<ItemTile> getItemTile() {
        return itemTile;
    }

    /**
     * Fill the slot with an ItemTile
     * @param itemTile new content of the slot
     */
    public void setItemTile(Optional<ItemTile> itemTile) {
        this.itemTile = itemTile;
    }
}
