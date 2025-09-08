package com.mcgreedy.optionothello.ai.options;

import com.mcgreedy.optionothello.ai.Option;
import com.mcgreedy.optionothello.engine.Board;
import com.mcgreedy.optionothello.engine.Move;
import com.mcgreedy.optionothello.utils.Constants;
import com.mcgreedy.optionothello.utils.Constants.PLAYER_COLOR;
import java.util.List;
import java.util.Random;

public class QuietMoveOption implements Option {

  private static final String NAME = "QuietMoveOption";

  private static final int[] DIRECTIONS = Board.DIRECTIONS;
  private static final Random rand  = new Random();

  @Override
  public boolean isBoardInInitiationSet(Board board, PLAYER_COLOR playerColor) {
    return true;
  }

  @Override
  public List<Board> initiationSet() {
    return List.of();
  }

  @Override
  public boolean shouldTerminate(Board board, PLAYER_COLOR playerColor) {
    //No moves left
    return true;
    //return board.generateAllPossibleMoves(playerColor == PLAYER_COLOR.WHITE) == 0L || board.isGameOver();
  }

  @Override
  public Move getBestMove(Board board, List<Move> possibleMoves) {
    LOGGER.info(NAME);
    PLAYER_COLOR me = possibleMoves.getFirst().getColor();
    Move best = null;
    double bestScore = Double.POSITIVE_INFINITY;
    
    for (Move move : possibleMoves) {
      //simulate move
      Board clone = board.clone();
      clone.updateBoard(move.getPosition(), me == PLAYER_COLOR.WHITE);
      // count frontierdiscs
      int frontierCount = countFrontierDiscs(clone, me);
      int maxWallLength = countMaxFrontierWall(clone, me);

      double score = frontierCount + 2.0 * maxWallLength;

      if (score < bestScore) {
        bestScore = score;
        best = move;
      }
    }
    
    if(best == null) {
      return possibleMoves.get(rand.nextInt(possibleMoves.size()));
    }
    return best;
  }

  private int countFrontierDiscs(Board board, PLAYER_COLOR playerColor) {
    long playerPieces = playerColor == PLAYER_COLOR.WHITE
        ? board.getWhite()
        : board.getBlack();
    long emptySquares = ~(board.getWhite() | board.getBlack());
    int frontierStoneCount = 0;

    // Für jede Position auf dem Brett prüfen
    for (int position = 0; position < 64; position++) {
      long positionBit = 1L << position;
      // Nur eigene Steine betrachten
      if ((playerPieces & positionBit) == 0) {
        continue;
      }
      // Prüfe alle Nachbarrichtungen auf leere Felder
      for (int direction : Board.DIRECTIONS) {
        int neighborPosition = position + direction;
        if (neighborPosition < 0 || neighborPosition >= 64) {
          continue;
        }
        long neighborBit = 1L << neighborPosition;
        // Wenn angrenzendes Feld leer ist, zählt der Stein als Frontier
        if ((emptySquares & neighborBit) != 0) {
          frontierStoneCount++;
          //break;
        }
      }
    }
    return frontierStoneCount;
  }

  /**
   * Zählt die maximale Länge einer Frontier-„Wand“ in Reihen und Spalten.
   * Eine Frontier-Stein ist ein eigener Stein mit mindestens einem leeren Nachbarn.
   */
  public int countMaxFrontierWall(Board board, PLAYER_COLOR playerColor) {
    long playerPieces = playerColor == PLAYER_COLOR.WHITE
        ? board.getWhite()
        : board.getBlack();
    long emptySquares = ~(board.getWhite() | board.getBlack());
    int maxWallLength = 0;

    // Prüfe alle 8 Reihen
    for (int row = 0; row < 8; row++) {
      int currentLength = 0;
      for (int col = 0; col < 8; col++) {
        int pos = row * 8 + col;
        if (isFrontierStone(board, playerPieces, emptySquares, pos)) {
          currentLength++;
          maxWallLength = Math.max(maxWallLength, currentLength);
        } else {
          currentLength = 0;
        }
      }
    }

    // Prüfe alle 8 Spalten
    for (int col = 0; col < 8; col++) {
      int currentLength = 0;
      for (int row = 0; row < 8; row++) {
        int pos = row * 8 + col;
        if (isFrontierStone(board, playerPieces, emptySquares, pos)) {
          currentLength++;
          maxWallLength = Math.max(maxWallLength, currentLength);
        } else {
          currentLength = 0;
        }
      }
    }

    return maxWallLength;
  }

  /** Prüft, ob an Position pos ein Frontier-Stein liegt. */
  private boolean isFrontierStone(Board board, long playerPieces, long emptySquares, int pos) {
    long bit = 1L << pos;
    if ((playerPieces & bit) == 0) {
      return false;
    }
    // Ein Frontier-Stein hat mindestens einen leeren Nachbarn
    for (int dir : Board.DIRECTIONS) {
      int neighbor = pos + dir;
      if (neighbor < 0 || neighbor >= 64) continue;
      if ((emptySquares & (1L << neighbor)) != 0) {
        return true;
      }
    }
    return false;
  }


  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public String toString(){
    return NAME;
  }
}
