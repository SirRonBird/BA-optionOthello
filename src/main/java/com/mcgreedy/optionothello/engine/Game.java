package com.mcgreedy.optionothello.engine;

import com.mcgreedy.optionothello.utils.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class Game {

    public int blackPieces;
    public int whitePieces;

    public List<Move> moveHistory;
    public List<Board> boardHistory;

    public Board board;

    private static final Logger LOGGER = LogManager.getLogger(Game.class);

    public Game() {
        blackPieces = 2;
        whitePieces = 2;
        moveHistory = new ArrayList<>();
        boardHistory = new ArrayList<>();
        board = new Board();
    }

    public void updateScore() {
        this.blackPieces = Long.bitCount(this.board.getBlack());
        this.whitePieces = Long.bitCount(this.board.getWhite());
    }

    public void updateBoard(Move move) {
        moveHistory.add(move);

        if (move.position == -1) {
            Board newBoard = new Board(board.getBlack(), board.getWhite());
            addBoardToHistory(newBoard);
        } else {
            board.updateBoard(move.position, move.color == Constants.PLAYER_COLOR.WHITE);
            Board newBoard = new Board(board.getBlack(), board.getWhite());
            addBoardToHistory(newBoard);
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
        LOGGER.debug("Adding board to history: {}", board.toString());
        this.boardHistory.add(board);
    }


}
