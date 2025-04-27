package com.mcgreedy.optionothello.gamemanagement;

import com.mcgreedy.optionothello.engine.Move;
import com.mcgreedy.optionothello.ui.MainGUI;
import com.mcgreedy.optionothello.utils.Constants;

public class HumanPlayer extends Player {

    public HumanPlayer(Constants.PLAYER_COLOR color, Constants.PLAYER_TYPE type, Gamemanager gamemanager) {
        super(color, type, gamemanager);
    }

    @Override
    public void makeMove() {
        int position = MainGUI.lastClickedCell;

        Move move =  new Move(
                this.color,
                position,
                0,
                this.type
        );

        this.gamemanager.makeMove(move);
    }



    @Override
    public String toString() {
        return "Human Player{" +
                "color=" + color +
                ", type=" + type +
                '}';
    }
}
