package com.mcgreedy.optionothello.engine;

import com.mcgreedy.optionothello.utils.Constants.PLAYER_COLOR;

public class OthelloEvaluator {

  private static final long CORNERS = 0x8100000000000081L;
  private static final long X_SQUARES = 0x4200000000000042L;
  private static final long C_SQUARES = 0x2400000000000024L;

  public static int evaluate(Board board, PLAYER_COLOR player) {
    boolean isWhite = (player == PLAYER_COLOR.WHITE);
    long myDiscs = isWhite ? board.getWhite() : board.getBlack();
    long oppDiscs = isWhite ? board.getBlack() : board.getWhite();

    int score = 0;

    // Ecken
    score += Long.bitCount(myDiscs & CORNERS) * 100;
    score -= Long.bitCount(oppDiscs & CORNERS) * 100;

    // X- und C-Squares
    score -= Long.bitCount(myDiscs & (X_SQUARES | C_SQUARES)) * 50;
    score += Long.bitCount(oppDiscs & (X_SQUARES | C_SQUARES)) * 50;

    // Mobilität
    int myMoves = Long.bitCount(board.generateAllPossibleMoves(isWhite));
    int oppMoves = Long.bitCount(board.generateAllPossibleMoves(!isWhite));
    score += (myMoves - oppMoves) * 5;

    // Disc Difference nur im Endspiel
    int totalDiscs = Long.bitCount(myDiscs | oppDiscs);
    if (totalDiscs >= 50) {
      score += (Long.bitCount(myDiscs) - Long.bitCount(oppDiscs));
    }

    return score;
  }
}

