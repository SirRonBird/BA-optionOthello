package com.mcgreedy.optionothello.engine;

import com.mcgreedy.optionothello.ai.Option;
import com.mcgreedy.optionothello.utils.Constants;

import java.util.Objects;

public class Move {

    int position;

    Constants.PLAYER_COLOR color;
    Constants.PLAYER_TYPE playerType;
    MoveStatistics statistics;

    public Move() {
        // default constructor needed for Jackson
    }

    public Move(Constants.PLAYER_COLOR color, int position, int searchDepth, Constants.PLAYER_TYPE playerType) {
        this.color = color;
        this.position = position;
        this.playerType = playerType;
        this.statistics = new MoveStatistics();
        this.statistics.setSearchDepth(searchDepth);

    }

    public void setStatistics(MoveStatistics statistics) {
        this.statistics = statistics;
    }

    public void setSearchDepth(int searchDepth) {
        this.statistics.setSearchDepth(searchDepth);
    }

    public void setOption(Option option) {
        this.statistics.setOption(option);
    }

    public int getPosition() {
        return position;
    }

    public Option getOption() {
        return this.statistics.getOption();
    }

    public int getSearchDepth() {
        return this.statistics.getSearchDepth();
    }

    public Constants.PLAYER_TYPE getPlayerType() {
        return playerType;
    }

    public Constants.PLAYER_COLOR getColor() {
        return color;
    }

    public MoveStatistics getStatistics() {
        return statistics;
    }

    @Override
    public String toString() {
        return "Move{" +
                "color=" + color +
                ", position=" + position +
                ", searchDepth=" + this.statistics.getSearchDepth() +
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
