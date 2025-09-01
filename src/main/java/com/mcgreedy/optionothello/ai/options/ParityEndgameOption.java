package com.mcgreedy.optionothello.ai.options;

import com.mcgreedy.optionothello.ai.Option;
import com.mcgreedy.optionothello.engine.Board;
import com.mcgreedy.optionothello.engine.Move;
import com.mcgreedy.optionothello.engine.OthelloEvaluator;
import com.mcgreedy.optionothello.utils.Constants.PLAYER_COLOR;
import com.mcgreedy.optionothello.utils.Constants.PLAYER_TYPE;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class ParityEndgameOption implements Option {

  private static final int SEARCH_DEPTH = 6; // Endspiel-Tiefe (kannst du hochstellen)

  @Override
  public boolean isBoardInInitiationSet(Board board, PLAYER_COLOR playerColor) {
    int emptyCount = 64 - Long.bitCount(board.getWhite() | board.getBlack());
    return emptyCount <= 14; // aktivieren in den letzten 14 Zügen
  }

  @Override
  public List<Board> initiationSet() { return new ArrayList<>(); }

  @Override
  public boolean shouldTerminate(Board board, PLAYER_COLOR playerColor) {
    int emptyCount = 64 - Long.bitCount(board.getWhite() | board.getBlack());
    return emptyCount > 14; // wenn wieder zu früh im Spiel → beenden
  }

  @Override
  public Move getBestMove(Board board, List<Move> possibleMoves) {
    PLAYER_COLOR color = possibleMoves.get(0).getColor();

    Move bestMove = null;
    int bestValue = Integer.MIN_VALUE;

    for (Move move : possibleMoves) {
      Board clone = board.clone();
      clone.updateBoard(move.getPosition(), color == PLAYER_COLOR.WHITE);

      int value = -negamax(clone, SEARCH_DEPTH - 1, Integer.MIN_VALUE, Integer.MAX_VALUE,
          color != PLAYER_COLOR.WHITE ? PLAYER_COLOR.WHITE : PLAYER_COLOR.BLACK);

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
  public String getName() { return "ParityEndgameOption"; }

  @Override
  public String toString() { return "ParityEndgameOption"; }

  /**
   * Negamax-Suche mit Alpha-Beta-Pruning
   */
  private int negamax(Board board, int depth, int alpha, int beta, PLAYER_COLOR playerColor) {
    boolean isWhite = (playerColor == PLAYER_COLOR.WHITE);
    long moves = board.generateAllPossibleMoves(isWhite);

    if (depth == 0 || moves == 0) {
      return OthelloEvaluator.evaluate(board, playerColor);
    }

    int max = Integer.MIN_VALUE;

    List<Move> moveList = board.generateMovesAsList(playerColor == PLAYER_COLOR.WHITE,depth,
        PLAYER_TYPE.O_MCTS); // Hilfsmethode: alle Moves aus Bitboard
    for (Move move : moveList) {
      Board clone = board.clone();
      clone.updateBoard(move.getPosition(), isWhite);

      int score = -negamax(clone, depth - 1, -beta, -alpha,
          (isWhite ? PLAYER_COLOR.BLACK : PLAYER_COLOR.WHITE));

      max = Math.max(max, score);
      alpha = Math.max(alpha, score);

      if (alpha >= beta) break; // Alpha-Beta-Cutoff
    }

    return max;
  }
}


