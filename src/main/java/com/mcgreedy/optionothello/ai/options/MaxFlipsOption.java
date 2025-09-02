package com.mcgreedy.optionothello.ai.options;

import com.mcgreedy.optionothello.ai.Option;
import com.mcgreedy.optionothello.engine.Board;
import com.mcgreedy.optionothello.engine.Move;
import com.mcgreedy.optionothello.utils.Constants;
import com.mcgreedy.optionothello.utils.Constants.PLAYER_COLOR;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * MaxFlipsOption
 * --------------------------------------------------------------------
 * Ziel: Anzahl gedrehter Steine maximieren NUR im Endgame (>=50 Steine).
 * Early/Midgame werden viele Flips vermieden (minimiert), um Frontier
 * klein zu halten.
 */
public class MaxFlipsOption implements Option {

  private static final String NAME = "MaxFlipsOption";

  @Override public boolean isBoardInInitiationSet(Board b, Constants.PLAYER_COLOR c) { return true; }
  @Override public List<Board> initiationSet() { return Collections.emptyList(); }
  @Override public boolean shouldTerminate(Board b, Constants.PLAYER_COLOR c) { return false; }

  @Override
  public Move getBestMove(Board board, List<Move> possibleMoves) {
    int discs = board.getWhiteCount() + board.getBlackCount();
    boolean endgame = discs >= 50;

    Constants.PLAYER_COLOR me = possibleMoves.getFirst().getColor();

    return (endgame
        ? possibleMoves.stream().max(Comparator.comparingInt(m -> flipDelta(board, m, me)))
        : possibleMoves.stream().min(Comparator.comparingInt(m -> flipDelta(board, m, me)))
    ).orElse(possibleMoves.get(new Random().nextInt(possibleMoves.size())));
  }

  private int flipDelta(Board b, Move m, Constants.PLAYER_COLOR me) {
    int before = (me == Constants.PLAYER_COLOR.WHITE) ? b.getWhiteCount() : b.getBlackCount();
    Board c = b.clone();
    c.updateBoard(m.getPosition(), me == Constants.PLAYER_COLOR.WHITE);
    int after  = (me == Constants.PLAYER_COLOR.WHITE) ? c.getWhiteCount() : c.getBlackCount();
    return after - before;
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
