package com.mcgreedy.optionothello.ai.options;

import com.mcgreedy.optionothello.ai.Option;
import com.mcgreedy.optionothello.engine.Board;
import com.mcgreedy.optionothello.engine.Move;
import com.mcgreedy.optionothello.utils.Constants;
import com.mcgreedy.optionothello.utils.Constants.PLAYER_COLOR;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * DiagonalControlOption
 * --------------------------------------------------------------------
 * Ziel: Haupt- und Gegendiagonale besetzen. Wenn die zugehörige Ecke
 * bereits uns gehört, wird das Feld auf dieser Diagonale stärker
 * gewichtet (stabilitätsnah).
 */
public class DiagonalControlOption implements Option {

  private static final Set<Integer> MAIN_DIAG = new HashSet<>();
  private static final Set<Integer> ANTI_DIAG = new HashSet<>();
  static {
    for (int i = 0; i < 8; i++) {
      MAIN_DIAG.add(i * 9);        // 0,9,18,...,63
      ANTI_DIAG.add((i + 1) * 7);  // 7,14,21,...,56
    }
  }
  private static final int A1 = 0, H8 = 63, H1 = 7, A8 = 56;

  private static final String NAME = "DiagonalControlOption";

  @Override public boolean isBoardInInitiationSet(Board b, Constants.PLAYER_COLOR c) { return true; }
  @Override public List<Board> initiationSet() { return Collections.emptyList(); }
  @Override public boolean shouldTerminate(Board b, Constants.PLAYER_COLOR c) { return false; }

  @Override
  public Move getBestMove(Board board, List<Move> possibleMoves) {
    Constants.PLAYER_COLOR me = possibleMoves.get(0).getColor();
    long myBits = (me == Constants.PLAYER_COLOR.WHITE) ? board.getWhite() : board.getBlack();

    boolean a1 = ((myBits >>> A1) & 1L) != 0;
    boolean h8 = ((myBits >>> H8) & 1L) != 0;
    boolean h1 = ((myBits >>> H1) & 1L) != 0;
    boolean a8 = ((myBits >>> A8) & 1L) != 0;

    return possibleMoves.stream()
        .max(Comparator.comparingInt(m -> {
          int p = m.getPosition();
          int base = (MAIN_DIAG.contains(p) || ANTI_DIAG.contains(p)) ? 2 : 0;
          if (MAIN_DIAG.contains(p) && (a1 || h8)) base += 3;
          if (ANTI_DIAG.contains(p) && (h1 || a8)) base += 3;
          return base;
        }))
        .orElse(possibleMoves.get(new Random().nextInt(possibleMoves.size())));
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
