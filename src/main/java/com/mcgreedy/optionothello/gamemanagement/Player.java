package com.mcgreedy.optionothello.gamemanagement;

import com.mcgreedy.optionothello.utils.Constants;
import com.mcgreedy.optionothello.engine.Move;

public abstract class Player {

    protected Constants.PLAYER_COLOR color;
    protected Constants.PLAYER_TYPE type;

    public Player(Constants.PLAYER_COLOR color, Constants.PLAYER_TYPE type) {
        this.color = color;
        this.type = type;
    }

    //TODO
    public abstract Move makeMove();

    public boolean isWhite() {
        return color == Constants.PLAYER_COLOR.WHITE;
    }

    @Override
    public String toString() {
        return "Player{" +
                "color=" + color +
                ", type=" + type +
                '}';
    }
}
