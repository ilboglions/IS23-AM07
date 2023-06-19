package it.polimi.ingsw.client.connection;

/**
 * Enum of the supported connection types
 */
public enum ConnectionType {
    RMI(),
    TCP();

    /**
     *
     * @return a string representing the ConnectionType
     */
    @Override
    public String toString() {
        return super.toString();
    }
}
