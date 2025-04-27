package com.mcgreedy.optionothello.engine;

import com.mcgreedy.optionothello.utils.Constants;

public class Move {

    Constants.PLAYER_COLOR color;
    int position;
    int searchDepth;

    Constants.PLAYER_TYPE playerType;

    //Option option;

    public Move(Constants.PLAYER_COLOR color, int position, int searchDepth, Constants.PLAYER_TYPE playerType) {
        this.color = color;
        this.position = position;
        this.searchDepth = searchDepth;
        this.playerType = playerType;
    }

    public int getPosition() {
        return position;
    }

    public int getSearchDepth() {
        return searchDepth;
    }

    public Constants.PLAYER_TYPE getPlayerType() {
        return playerType;
    }

    public Constants.PLAYER_COLOR getColor() {
        return color;
    }

    @Override
    public String toString() {
        return "Move{" +
                "color=" + color +
                ", position=" + position +
                ", searchDepth=" + searchDepth +
                ", playerType=" + playerType +
                '}';
    }
}
