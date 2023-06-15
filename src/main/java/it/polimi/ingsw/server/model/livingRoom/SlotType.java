package it.polimi.ingsw.server.model.livingRoom;

/**
 * Different types of slot
 * BASIC for normal slots
 * ATLEAST3 for slots that are active only in games with 3 or more players
 * ATLEAST4 for slots that are active only in games with 4 or more players
 * NOTCELL for slots that are never in use
 */
public enum SlotType {
    BASIC,
    ATLEAST3,
    ATLEAST4,
    NOTCELL
}
