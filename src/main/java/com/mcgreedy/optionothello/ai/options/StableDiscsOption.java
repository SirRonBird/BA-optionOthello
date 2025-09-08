package com.mcgreedy.optionothello.ai.options;

import com.mcgreedy.optionothello.ai.Option;
import com.mcgreedy.optionothello.engine.Board;
import com.mcgreedy.optionothello.engine.Move;
import com.mcgreedy.optionothello.utils.Constants;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * StableDiscsOption
 * --------------------------------------------------------------------
 * Ziel: stabile Steine maximieren. Näherungsweise wird Stabilität über
 * besetzte Ecken + zusammenhängende gleicher Farbe entlang der Kanten
 * (von Ecken aus) geschätzt. Wählt den Zug mit größter Stabilitätszunahme.
 */
public class StableDiscsOption implements Option {

  private static final String NAME = "StableDiscsOption";

  private static final Board CORNER_MASK = new Board(
      "Corners", true
  );

  @Override public boolean isBoardInInitiationSet(Board b, Constants.PLAYER_COLOR c) {
    CORNER_MASK.mask = 0x8100000000000081L;
    // Only start if at least one corner is taken.
    return b.boardIsHittingMask(CORNER_MASK,c);
  }
  @Override public List<Board> initiationSet() { return Collections.emptyList(); }
  @Override public boolean shouldTerminate(Board b, Constants.PLAYER_COLOR c) { return b.isGameOver(); }

  @Override
  public Move getBestMove(Board board, List<Move> possibleMoves) {
    Constants.PLAYER_COLOR me = possibleMoves.getFirst().getColor();
    return possibleMoves.stream()
        .max(Comparator.comparingInt(m -> {
          int before = estimateStableEdgeCount(board, me);
          Board c = board.clone();
          c.updateBoard(m.getPosition(), me == Constants.PLAYER_COLOR.WHITE);
          int after = estimateStableEdgeCount(c, me);
          return after - before;
        }))
        .orElse(possibleMoves.get(new Random().nextInt(possibleMoves.size())));
  }

  private int estimateStableEdgeCount(Board b, Constants.PLAYER_COLOR me) {
    long bits = (me == Constants.PLAYER_COLOR.WHITE) ? b.getWhite() : b.getBlack();
    int sum = 0;
    if (((bits) & 1L)  != 0) { sum += run(bits, 0, +1); sum += run(bits, 0, +8); }
    if (((bits >>> 7) & 1L)  != 0) { sum += run(bits, 7, -1); sum += run(bits, 7, +8); }
    if (((bits >>> 56) & 1L) != 0) { sum += run(bits,56, +1); sum += run(bits,56, -8); }
    if (((bits >>> 63) & 1L) != 0) { sum += run(bits,63, -1); sum += run(bits,63, -8); }
    return sum;
  }
  private int run(long bits, int i, int step) {
    int cnt = 0;
    while (i >= 0 && i < 64) {
      if (((bits >>> i) & 1L) == 0) break;
      cnt++;
      int r = i / 8;
      int nr = (i + step) / 8;
      if ((step == 1 || step == -1) && nr != r) break;

      i += step;
    }
    return cnt;
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
