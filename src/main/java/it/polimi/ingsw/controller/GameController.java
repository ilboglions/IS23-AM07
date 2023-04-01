package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.coordinate.Coordinates;
import it.polimi.ingsw.model.exceptions.*;
import it.polimi.ingsw.model.game.GameModelInterface;

import java.util.ArrayList;

/**
 * the game controller ensures the communication through client controller and server model
 */
public class GameController {
    private final GameModelInterface gameModel;

    /**
     * creates the gameController
     * @param gameModel the model reffered to the game
     */
    public GameController(GameModelInterface gameModel) {
        this.gameModel = gameModel;
    }

    /**
     * first method to call, it is used to confirm that a certain set of item tiles can be taken by the player in the board (but without actually taking it)
     * @param player the player that perform the action
     * @param coords the list of coordinates where tiles should have been taken
     * @return true, if the action is permitted
     */
    public boolean getItemTiles(String player, ArrayList<Coordinates> coords)  {


        try {
            if(!player.equals(gameModel.getPlayerInTurn())) return false;
        } catch (GameEndedException e) {
            return false;
        }

        try{
            return  gameModel.getItemTiles(coords);

        } catch (EmptySlotException e){
            return false;
        }

    }

    /**
     * the second method to be called by the player, it performs the insertion of the tile in the bookshelf of the player, removing them from the board.
     * it also checks if the livingroom should be refilled and, in that case invoke the refill action. It also passes the turn to the next player
     * @param player the player that is performing the action
     * @param source the coordinates to be taken
     * @param column the column where the coordinates will be inserted
     * @return true, if the action has been correctly performed, false otherwise
     */
    public boolean moveTiles( String player, ArrayList<Coordinates> source, int column){
        try {
            if(!player.equals(gameModel.getPlayerInTurn())) return false;
        } catch (GameEndedException e) {
            return false;
        }

        try {
            gameModel.moveTiles(source,column);
            if (gameModel.checkRefill()){
                gameModel.refillLivingRoom();
            }

            gameModel.updatePlayerPoints(player);

            gameModel.checkBookshelfComplete();

            gameModel.setPlayerTurn();


            return true;
        } catch (InvalidCoordinatesException e) {
            return false;
        } catch (EmptySlotException e) {
            return false;
        } catch (NotEnoughSpaceException e) {
            return false;
        } catch (InvalidPlayerException e){
            return false;
        }
    }

    protected GameModelInterface getGameControlled(){
        return gameModel;
    }




}
