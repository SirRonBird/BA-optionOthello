package com.mcgreedy.optionothello.engine;

import com.mcgreedy.optionothello.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class Game {

    public int blackPieces;
    public int whitePieces;

    public List<Move> moveHistory;
    public List<Board> boardHistory;

    public Board board;

    public Game() {
        blackPieces = 2;
        whitePieces = 2;
        moveHistory = new ArrayList<>();

        board = new Board();
        addBoardToHistory(board);
    }

    public void updateScore() {
        this.blackPieces = Long.bitCount(this.board.getBlack());
        this.whitePieces = Long.bitCount(this.board.getWhite());
    }

    public void placePiece(Move move) {
        moveHistory.add(move);
        if (move.position == -1) {
            addBoardToHistory(board);
        } else {
            board.updateBoard(move.position, move.color == Constants.PLAYER_COLOR.WHITE);
            addBoardToHistory(board);
        }
    }

    public long getBlackBoard() {
        return board.getBlack();
    }

    public long getWhiteBoard() {
        return board.getWhite();
    }

    public void addMoveToHistory(Move move) {
        this.moveHistory.add(move);
    }

    public void addBoardToHistory(Board board) {
        assert this.boardHistory != null;
        this.boardHistory.add(board);
    }


}
