package com.mcgreedy.optionothello.ai.options;

import com.mcgreedy.optionothello.ai.Option;
import com.mcgreedy.optionothello.engine.Board;
import com.mcgreedy.optionothello.engine.Move;
import com.mcgreedy.optionothello.engine.OthelloEvaluator;
import com.mcgreedy.optionothello.utils.Constants.PLAYER_COLOR;
import java.util.ArrayList;
import java.util.List;

public class EdgeSecuringOption implements Option {

  private static final long CORNERS = 0x8100000000000081L;
  private static final long EDGES = 0x7E8181818181817EL & ~CORNERS; // alle Ränder ohne Ecken

  @Override
  public boolean isBoardInInitiationSet(Board board, PLAYER_COLOR playerColor) {
    long empty = ~(board.getWhite() | board.getBlack());
    return (EDGES & empty) != 0; // freie Randfelder vorhanden
  }

  @Override
  public List<Board> initiationSet() { return new ArrayList<>(); }

  @Override
  public boolean shouldTerminate(Board board, PLAYER_COLOR playerColor) {
    long empty = ~(board.getWhite() | board.getBlack());
    return (EDGES & empty) == 0; // keine Randfelder mehr
  }

  @Override
  public Move getBestMove(Board board, List<Move> possibleMoves) {
    PLAYER_COLOR color = possibleMoves.get(0).getColor();
    Move bestMove = null;
    int bestValue = Integer.MIN_VALUE;

    for (Move move : possibleMoves) {
      long moveBit = 1L << move.getPosition();

      // Priorisiere Randzüge
      if ((moveBit & EDGES) != 0) {
        Board clone = board.clone();
        clone.updateBoard(move.getPosition(), color == PLAYER_COLOR.WHITE);
        int value = OthelloEvaluator.evaluate(clone, color);
        if (value > bestValue) {
          bestValue = value;
          bestMove = move;
        }
      }
    }

    // Fallback: bester Zug laut Evaluator
    if (bestMove == null) {
      for (Move move : possibleMoves) {
        Board clone = board.clone();
        clone.updateBoard(move.getPosition(), color == PLAYER_COLOR.WHITE);
        int value = OthelloEvaluator.evaluate(clone, color);
        if (value > bestValue) {
          bestValue = value;
          bestMove = move;
        }
      }
    }

    return bestMove;
  }

  @Override
  public String getName() { return "EdgeSecuringOption"; }

  @Override
  public String toString() { return "EdgeSecuringOption"; }
}

