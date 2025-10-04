package com.mcgreedy.optionothello.ai;

import com.mcgreedy.optionothello.engine.Board;
import com.mcgreedy.optionothello.engine.Move;
import com.mcgreedy.optionothello.gamemanagement.Gamemanager;
import com.mcgreedy.optionothello.utils.Constants;

public abstract class Player {

    protected Constants.PLAYER_COLOR color;
    protected Constants.PLAYER_TYPE type;

    protected Gamemanager gamemanager;
    protected long searchTimeLimit = 0;
    protected int simulationLimit = 500;

    protected Player(Constants.PLAYER_COLOR color, Constants.PLAYER_TYPE type, Gamemanager gamemanager) {
        this.color = color;
        this.type = type;
        this.gamemanager = gamemanager;
    }

    public Constants.PLAYER_COLOR getColor() {
        return color;
    }

    public Constants.PLAYER_TYPE getType() {
        return type;
    }

    public abstract Move getMove(Board board);

    public void setSearchTimeLimit(long searchTimeLimit) {
        this.searchTimeLimit = searchTimeLimit;
    }

    public void setSimulationLimit(int simulationLimit) {
        this.simulationLimit = simulationLimit;
    }

    public abstract void resetMAST();

    @Override
    public String toString() {
        return "Player{" +
                "color=" + color +
                ", type=" + type +
                '}';
    }


}
