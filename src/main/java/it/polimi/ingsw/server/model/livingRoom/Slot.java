package it.polimi.ingsw.server.model.livingRoom;

import it.polimi.ingsw.server.model.tiles.ItemTile;

import java.util.Optional;

public class Slot {
    private final SlotType slotType;
    private Optional<ItemTile> itemTile;

    public Slot(SlotType slotType, Optional<ItemTile> itemTile) {
        this.slotType = slotType;
        this.itemTile = itemTile;
    }

    public SlotType getSlotType() {
        return slotType;
    }

    public Optional<ItemTile> getItemTile() {
        return itemTile;
    }

    public void setItemTile(Optional<ItemTile> itemTile) {
        this.itemTile = itemTile;
    }
}
