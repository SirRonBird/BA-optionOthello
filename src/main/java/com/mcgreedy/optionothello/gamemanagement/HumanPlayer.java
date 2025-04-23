package com.mcgreedy.optionothello.gamemanagement;

import com.mcgreedy.optionothello.engine.Move;
import com.mcgreedy.optionothello.utils.Constants;

public class HumanPlayer extends Player {

    public HumanPlayer(Constants.PLAYER_COLOR color, Constants.PLAYER_TYPE type) {
        super(color, type);
    }

    @Override
    public Move makeMove() {
        return null;
    }

    @Override
    public String toString() {
        return "Human Player{" +
                "color=" + color +
                ", type=" + type +
                '}';
    }
}
