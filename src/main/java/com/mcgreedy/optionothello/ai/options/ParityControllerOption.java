package com.mcgreedy.optionothello.ai.options;

import com.mcgreedy.optionothello.ai.Option;
import com.mcgreedy.optionothello.engine.Board;
import com.mcgreedy.optionothello.engine.Move;
import com.mcgreedy.optionothello.utils.Constants.PLAYER_COLOR;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ParityControllerOption implements Option {

  @Override
  public boolean isBoardInInitiationSet(Board board, PLAYER_COLOR playerColor) {
    return Long.bitCount(board.getWhite() | board.getBlack()) >= 49; // letzte 15 Züge
  }

  @Override
  public List<Board> initiationSet() { return new ArrayList<>(); }

  @Override
  public boolean shouldTerminate(Board board, PLAYER_COLOR playerColor) {
    return Long.bitCount(board.getWhite() | board.getBlack()) < 49;
  }

  @Override
  public Move getBestMove(Board board, List<Move> possibleMoves) {
    Move bestMove = null;
    int bestValue = Integer.MIN_VALUE;

    for (Move move : possibleMoves) {
      int value = evaluateMove(move);
      if (value > bestValue) {
        bestValue = value;
        bestMove = move;
      }
    }

    if (bestMove == null) {
      bestMove = possibleMoves.get(new Random().nextInt(possibleMoves.size()));
    }

    return bestMove;
  }

  @Override
  public String getName() { return "EndgameParityController"; }

  private int evaluateMove(Move move) {
    // Bevorzugt gerade Parität, sonst zufällig
    return (move.getPosition() % 2) == 0 ? 2 : 0;
  }
}


