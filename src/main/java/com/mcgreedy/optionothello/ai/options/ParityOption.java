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
 * ParityOption
 * --------------------------------------------------------------------
 * Ziel: Parität im Endspiel – wer den letzten Zug hat, gewinnt oft.
 * Näherung: Bevorzuge Zustände mit ungerader Anzahl leerer Felder und
 * niedriger Gegner-Mobilität. Aktiv v.a. ab ~50 Steinen.
 */
public class ParityOption implements Option {

  private static final String NAME = "ParityOption";

  @Override
  public boolean isBoardInInitiationSet(Board board, Constants.PLAYER_COLOR playerColor) {
    return (board.getWhiteCount() + board.getBlackCount()) >= 50;
  }

  @Override public List<Board> initiationSet() { return Collections.emptyList(); }
  @Override public boolean shouldTerminate(Board board, Constants.PLAYER_COLOR playerColor) { return board.isGameOver(); }

  @Override
  public Move getBestMove(Board board, List<Move> possibleMoves) {
    Constants.PLAYER_COLOR me  = possibleMoves.getFirst().getColor();
    Constants.PLAYER_COLOR opp = (me == Constants.PLAYER_COLOR.WHITE) ? Constants.PLAYER_COLOR.BLACK : Constants.PLAYER_COLOR.WHITE;

    return possibleMoves.stream()
        .max(Comparator.comparingInt(m -> {
          Board c = board.clone();
          c.updateBoard(m.getPosition(), me == Constants.PLAYER_COLOR.WHITE);
          int empties = 64 - (c.getWhiteCount() + c.getBlackCount());
          int oppMob  = c.generateMovesAsList(opp == Constants.PLAYER_COLOR.WHITE, 0, Constants.PLAYER_TYPE.O_MCTS).size();
          int parity = (empties % 2 == 1) ? 2 : -2;
          return parity - oppMob; // einfache Paritäts-/Mobility-Heuristik
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
