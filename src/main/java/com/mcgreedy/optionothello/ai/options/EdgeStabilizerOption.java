package com.mcgreedy.optionothello.ai.options;

import com.mcgreedy.optionothello.ai.Option;
import com.mcgreedy.optionothello.engine.Board;
import com.mcgreedy.optionothello.engine.Move;
import com.mcgreedy.optionothello.utils.Constants.PLAYER_COLOR;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class EdgeStabilizerOption implements Option {

  private static final long CORNERS = 0x8100000000000081L;
  private static final long EDGES = 0x42C3C3C3C3C34242L & ~CORNERS;

  @Override
  public boolean isBoardInInitiationSet(Board board, PLAYER_COLOR playerColor) {
    long empty = ~(board.getWhite() | board.getBlack());
    return (EDGES & empty) != 0;
  }

  @Override
  public List<Board> initiationSet() { return new ArrayList<>(); }

  @Override
  public boolean shouldTerminate(Board board, PLAYER_COLOR playerColor) {
    long empty = ~(board.getWhite() | board.getBlack());
    return (EDGES & empty) == 0;
  }

  @Override
  public Move getBestMove(Board board, List<Move> possibleMoves) {
    Move bestMove = null;
    int bestValue = Integer.MIN_VALUE;

    for (Move move : possibleMoves) {
      long moveBit = 1L << move.getPosition();
      if ((moveBit & EDGES) != 0) {
        int value = evaluateMove(board, move, move.getColor());
        if (value > bestValue) {
          bestValue = value;
          bestMove = move;
        }
      }
    }

    if (bestMove == null) {
      bestMove = possibleMoves.stream()
          .max(Comparator.comparingInt(m -> evaluateMove(board, m, m.getColor())))
          .orElse(possibleMoves.get(new Random().nextInt(possibleMoves.size())));
    }

    return bestMove;
  }

  @Override
  public String getName() { return "StableEdgeController"; }

  private int evaluateMove(Board board, Move move, PLAYER_COLOR color) {
    long moveBit = 1L << move.getPosition();
    return (moveBit & EDGES) != 0 ? 3 : 0;
  }
}


