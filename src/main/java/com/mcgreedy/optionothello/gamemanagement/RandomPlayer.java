package com.mcgreedy.optionothello.gamemanagement;

import com.mcgreedy.optionothello.engine.Move;
import com.mcgreedy.optionothello.utils.Constants;

public class RandomPlayer extends Player {

    public RandomPlayer(Constants.PLAYER_COLOR color, Constants.PLAYER_TYPE type) {
        super(color, type);
    }

    @Override
    public Move makeMove() {
        return null;
    }
}
