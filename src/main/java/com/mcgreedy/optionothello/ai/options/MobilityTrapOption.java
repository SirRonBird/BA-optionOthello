package com.mcgreedy.optionothello.ai.options;

import com.mcgreedy.optionothello.ai.Option;
import com.mcgreedy.optionothello.engine.Board;
import com.mcgreedy.optionothello.engine.Move;
import com.mcgreedy.optionothello.engine.OthelloEvaluator;
import com.mcgreedy.optionothello.utils.Constants.PLAYER_COLOR;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MobilityTrapOption implements Option {

  @Override
  public boolean isBoardInInitiationSet(Board board, PLAYER_COLOR playerColor) {
    boolean isWhite = (playerColor == PLAYER_COLOR.WHITE);
    int oppMoves = Long.bitCount(board.generateAllPossibleMoves(!isWhite));
    return oppMoves > 3; // nur sinnvoll, wenn Gegner viele Züge hat
  }

  @Override
  public List<Board> initiationSet() { return new ArrayList<>(); }

  @Override
  public boolean shouldTerminate(Board board, PLAYER_COLOR playerColor) {
    boolean isWhite = (playerColor == PLAYER_COLOR.WHITE);
    int oppMoves = Long.bitCount(board.generateAllPossibleMoves(!isWhite));
    return oppMoves <= 3; // Falle "geschnappt"
  }

  @Override
  public Move getBestMove(Board board, List<Move> possibleMoves) {
    PLAYER_COLOR color = possibleMoves.get(0).getColor();
    Move bestMove = null;
    int bestValue = Integer.MIN_VALUE;

    for (Move move : possibleMoves) {
      Board clone = board.clone();
      clone.updateBoard(move.getPosition(), color == PLAYER_COLOR.WHITE);

      int myMovesAfter = Long.bitCount(clone.generateAllPossibleMoves(color == PLAYER_COLOR.WHITE));
      int oppMovesAfter = Long.bitCount(clone.generateAllPossibleMoves(color != PLAYER_COLOR.WHITE));

      // Spezieller Fokus auf Mobilität
      int value = (myMovesAfter - oppMovesAfter) * 10;

      // mit allgemeiner Bewertung kombinieren
      value += OthelloEvaluator.evaluate(clone, color);

      if (value > bestValue) {
        bestValue = value;
        bestMove = move;
      }
    }

    // Fallback: zufällig
    if (bestMove == null) {
      bestMove = possibleMoves.get(new Random().nextInt(possibleMoves.size()));
    }

    return bestMove;
  }

  @Override
  public String getName() { return "MobilityTrapOption"; }

  @Override
  public String toString() { return "MobilityTrapOption"; }
}
