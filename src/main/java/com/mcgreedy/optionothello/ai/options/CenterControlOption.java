package com.mcgreedy.optionothello.ai.options;

import com.mcgreedy.optionothello.ai.Option;
import com.mcgreedy.optionothello.engine.Board;
import com.mcgreedy.optionothello.engine.Move;
import com.mcgreedy.optionothello.utils.Constants;
import com.mcgreedy.optionothello.utils.Constants.PLAYER_COLOR;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

/**
 * CenterControlOption
 * --------------------------------------------------------------------
 * Ziel: im frühen Spiel (bis ~20 Steine) möglichst zentrumsnahe Felder
 * besetzen. Bewertung über negative Manhattan-Distanz zum Brettzentrum.
 * Danach wird die Option automatisch schwächer (Termination).
 */
public class CenterControlOption implements Option {

  private static final Set<Integer> centerSquares = Set.of(27, 28, 35, 36);

  private static final String NAME = "CenterControlOption";

  @Override
  public boolean isBoardInInitiationSet(Board board, Constants.PLAYER_COLOR playerColor) {
    return (board.getWhiteCount() + board.getBlackCount()) < 24;
  }

  @Override public List<Board> initiationSet() { return Collections.emptyList(); }
  @Override public boolean shouldTerminate(Board board, Constants.PLAYER_COLOR playerColor) {
    return (board.getWhiteCount() + board.getBlackCount()) >= 24;
  }

  @Override
  public Move getBestMove(Board board, List<Move> possibleMoves) {
    return possibleMoves.stream()
        .max(Comparator.comparingInt(m -> -manhattanToCenter(m.getPosition())))
        .orElse(possibleMoves.get(0));
  }

  private int manhattanToCenter(int pos) {
    int r = pos / 8, c = pos % 8;
    // Zentrum ~ (3,4)/(4,3)/(3,3)/(4,4). Nutze dist zu (3.5,3.5) gerundet.
    return Math.min(Math.abs(r - 3), Math.abs(r - 4)) + Math.min(Math.abs(c - 3), Math.abs(c - 4));
  }


  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public String toString() {
    return NAME;
  }
}
