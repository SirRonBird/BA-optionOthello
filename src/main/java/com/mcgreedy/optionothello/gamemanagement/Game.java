package com.mcgreedy.optionothello.gamemanagement;

import com.mcgreedy.optionothello.engine.Board;
import com.mcgreedy.optionothello.engine.Move;
import com.mcgreedy.optionothello.utils.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class Game {

    Board board;
    int blackPieces;
    int whitePieces;

    List<Move> moveHistory;
    List<Board> boardHistory;

    int winner = -1;

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

        if (move.getPosition() == -1) {
            Board newBoard = new Board(board.getBlack(), board.getWhite());
            addBoardToHistory(newBoard);
        } else {
            board.updateBoard(move.getPosition(), move.getColor() == Constants.PLAYER_COLOR.WHITE);
            Board newBoard = new Board(board.getBlack(), board.getWhite());
            addBoardToHistory(newBoard);
        }
    }

    public void setWinner(int winner) {
        this.winner = winner;
    }

    public int getWinner() {
        return winner;
    }

    public long getBlackBoard() {
        return board.getBlack();
    }

    public long getWhiteBoard() {
        return board.getWhite();
    }

    public void addBoardToHistory(Board board) {
        assert this.boardHistory != null;
        LOGGER.debug("Adding board to history: {}", board);
        this.boardHistory.add(board);
    }

    public List<Move> getMoveHistory() {
        return moveHistory;
    }

    public List<Board> getBoardHistory() {
        return boardHistory;
    }

    public Board getBoard() {
        return board;
    }
}
