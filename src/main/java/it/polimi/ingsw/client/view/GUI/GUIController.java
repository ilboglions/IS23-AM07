package it.polimi.ingsw.client.view.GUI;

import it.polimi.ingsw.client.connection.ConnectionHandler;
import javafx.stage.Stage;

/**
 * This is the abstract class for the GUIControllers
 */
public abstract class GUIController {
    private static ConnectionHandler clientController;
    protected Stage stage;



    private GUIView manager;

    /**
     * Sets the ConnectionHandler
     * @param clientController connection manager for this client
     */
    public void setConnectionHandler( ConnectionHandler clientController){
        GUIController.clientController = clientController;
    }

    /**
     * Sets the view manager for this client
     * @param manager GUIView instance reference
     */
    public void setManager(GUIView manager){
        this.manager = manager;
    }

    /**
     * Posts a notification on the stage
     * @param title title of the notification
     * @param desc description of the notification
     */
    public abstract void postNotification(String title, String desc);
    protected ConnectionHandler getClientController(){
        return GUIController.clientController;
    }

    protected GUIView getManager() {return manager;}

    /**
     * Set the current stage attribute
     * @param stage stage reference
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
