package com.mcgreedy.optionothello.ai.options;

import com.mcgreedy.optionothello.ai.Option;
import com.mcgreedy.optionothello.engine.Board;
import com.mcgreedy.optionothello.engine.Move;
import com.mcgreedy.optionothello.utils.Constants.PLAYER_COLOR;
import com.mcgreedy.optionothello.utils.Constants.PLAYER_TYPE;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EndgameSolverOption implements Option {

  private static final int MAX_DEPTH = 4;

  @Override
  public boolean isBoardInInitiationSet(Board board, PLAYER_COLOR playerColor) {
    return Long.bitCount(board.getWhite() | board.getBlack()) >= 52; // 12 Züge übrig
  }

  @Override
  public List<Board> initiationSet() { return new ArrayList<>(); }

  @Override
  public boolean shouldTerminate(Board board, PLAYER_COLOR playerColor) {
    return Long.bitCount(board.getWhite() | board.getBlack()) < 52;
  }

  @Override
  public Move getBestMove(Board board, List<Move> possibleMoves) {
    PLAYER_COLOR color = possibleMoves.get(0).getColor();
    Move bestMove = null;
    int bestValue = Integer.MIN_VALUE;

    for (Move move : possibleMoves) {
      Board clone = board.clone();
      clone.updateBoard(move.getPosition(), color == PLAYER_COLOR.WHITE);
      int value = -minimax(clone, MAX_DEPTH - 1, color == PLAYER_COLOR.WHITE ? PLAYER_COLOR.BLACK : PLAYER_COLOR.WHITE);
      if (value > bestValue) {
        bestValue = value;
        bestMove = move;
      }
    }
    return bestMove != null ? bestMove : possibleMoves.get(new Random().nextInt(possibleMoves.size()));
  }

  private int minimax(Board board, int depth, PLAYER_COLOR player) {
    if (depth == 0 || board.isGameOver()) {
      return evaluate(board, player);
    }
    List<Move> moves = board.generateMovesAsList(player == PLAYER_COLOR.WHITE, -2, PLAYER_TYPE.O_MCTS);
    if (moves.isEmpty()) {
      return -minimax(board, depth - 1, player == PLAYER_COLOR.WHITE ? PLAYER_COLOR.BLACK : PLAYER_COLOR.WHITE);
    }
    int best = Integer.MIN_VALUE;
    for (Move move : moves) {
      Board clone = board.clone();
      clone.updateBoard(move.getPosition(), player == PLAYER_COLOR.WHITE);
      int score = -minimax(clone, depth - 1, player == PLAYER_COLOR.WHITE ? PLAYER_COLOR.BLACK : PLAYER_COLOR.WHITE);
      best = Math.max(best, score);
    }
    return best;
  }



  private int evaluate(Board board, PLAYER_COLOR player) {
    int whiteCount = Long.bitCount(board.getWhite());
    int blackCount = Long.bitCount(board.getBlack());
    return (player == PLAYER_COLOR.WHITE) ? (whiteCount - blackCount) : (blackCount - whiteCount);
  }

  @Override
  public String getName() { return "EndgameSolver"; }

  @Override
  public String toString() { return "EndgameSolver"; }
}

