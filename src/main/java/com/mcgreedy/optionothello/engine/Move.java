package com.mcgreedy.optionothello.engine;

import com.mcgreedy.optionothello.utils.Constants;

import java.util.Objects;

public class Move {

    private Constants.PLAYER_COLOR color;
    private int position;
    private int searchDepth;

    private Constants.PLAYER_TYPE playerType;
    //Option option;


    public Move() {
        // default constructor needed for Jackson
    }

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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Move move = (Move) obj;
        return position == move.position && color == move.color; // oder erweitern, je nach Design
    }

    @Override
    public int hashCode() {
        return Objects.hash(position, color);
    }

}
