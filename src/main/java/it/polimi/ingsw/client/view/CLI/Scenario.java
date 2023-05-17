package it.polimi.ingsw.client.view.CLI;

public enum Scenario {
    LOBBY(100,15, 8, 15, 5, 65),
    GAME( 170,70, 35, 120,10,45);

    private int rows;
    private int cols;

    private int startRNotifications;
    private int startCNotifications;

    private int lengthRNotifications;
    private int lengthCNotifications;
    Scenario(int cols, int rows, int startRNotifications, int startCNotifications, int lengthRNotifications, int lengthCNotifications){
        this.cols = cols;
        this.rows = rows;
        this.startRNotifications = startRNotifications;
        this.startCNotifications = startCNotifications;
        this.lengthRNotifications = lengthRNotifications;
        this.lengthCNotifications = lengthCNotifications;
    }

    public int getRows(){
        return this.rows;
    }
    public int getCols(){
        return this.cols;
    }

    public int getStartCNotifications() {
        return startCNotifications;
    }

    public int getStartRNotifications() {
        return startRNotifications;
    }

    public int getLengthRNotifications() {
        return lengthRNotifications;
    }

    public int getLengthCNotifications() {
        return this.lengthCNotifications;
    }
}
