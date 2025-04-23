package com.mcgreedy.optionothello.engine;

import com.mcgreedy.optionothello.ui.MainGUI;

import java.util.ArrayList;
import java.util.List;

public class Game {

    int blackPieces;
    int whitePieces;

    List<Move> moves;

    Board board;

    public Game() {
        blackPieces = 2;
        whitePieces = 2;
        moves = new ArrayList<Move>();

        board = new Board();

        MainGUI.updatedBoardGrid(board.black, board.white);
    }
}
