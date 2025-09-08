package com.mcgreedy.optionothello.gamemanagement;

import com.mcgreedy.optionothello.engine.Board;
import com.mcgreedy.optionothello.engine.Move;
import com.mcgreedy.optionothello.ui.MainGUI;
import com.mcgreedy.optionothello.utils.Constants;

public class HumanPlayer extends Player {

    public HumanPlayer(Constants.PLAYER_COLOR color, Constants.PLAYER_TYPE type, Gamemanager gamemanager) {
        super(color, type, gamemanager);
    }

    @Override
    public Move getMove(Board board) {
        int position = MainGUI.getLastClickedCell();

        Move move = new Move(
                this.color,
                position,
                0,
                this.type
        );
        gamemanager.makeMove(move);
        return move;
    }

    @Override
    public void resetMAST() {
        // nothing to do here
    }


    @Override
    public String toString() {
        return "Human Player{" +
                "color=" + color +
                ", type=" + type +
                '}';
    }

    public void skipMove() {
        Move skipMove = new Move(this.color, -1, -1, this.type);
        gamemanager.passMove(skipMove);
    }
}
