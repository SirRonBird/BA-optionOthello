package com.mcgreedy.optionothello.gamemanagement;

import com.mcgreedy.optionothello.engine.Board;
import com.mcgreedy.optionothello.engine.Move;
import com.mcgreedy.optionothello.utils.Constants;

public abstract class Player {

    protected Constants.PLAYER_COLOR color;
    protected Constants.PLAYER_TYPE type;

    protected Gamemanager gamemanager;

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

    @Override
    public String toString() {
        return "Player{" +
                "color=" + color +
                ", type=" + type +
                '}';
    }
}
