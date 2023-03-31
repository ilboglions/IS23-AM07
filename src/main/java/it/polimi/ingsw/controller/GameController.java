package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.exceptions.InvalidPlayerException;
import it.polimi.ingsw.model.exceptions.NotAllPlayersHaveJoinedException;
import it.polimi.ingsw.model.game.GameModelInterface;
import it.polimi.ingsw.model.tokens.ScoringToken;

import java.util.ArrayList;

public class GameController {
    private final GameModelInterface gameModel;
    private final GameView gameview;


    public GameController(GameModelInterface gameModel, GameView gameview) {
        this.gameModel = gameModel;
        this.gameview = gameview;
    }

    public boolean getIsStarted(){
        return gameModel.getIsStarted();
    }

    public boolean start(){
        try{
            gameModel.start();
        } catch (NotAllPlayersHaveJoinedException e) {
            return false;
        }
        return true;

    }

    public int getPlayerPoints( String player) throws InvalidPlayerException{

        return  gameModel.getPlayerPoints(player);

    }

    public ArrayList<ScoringToken> getPlayerTokens(String player) throws InvalidPlayerException{
        return gameModel.getPlayerTokens(player);
    }

    public String getPlayerInTurn(){
        return  gameModel.getPlayerInTurn();
    }




}
