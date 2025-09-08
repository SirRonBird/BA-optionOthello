package com.mcgreedy.optionothello.ai.options;

import com.mcgreedy.optionothello.ai.Option;
import com.mcgreedy.optionothello.engine.Board;
import com.mcgreedy.optionothello.engine.Move;
import com.mcgreedy.optionothello.utils.Constants;
import com.mcgreedy.optionothello.utils.Constants.PLAYER_COLOR;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * CenterControlOption
 * --------------------------------------------------------------------
 * Ziel: im frühen Spiel (bis ~20 Steine) möglichst zentrumsnahe Felder
 * besetzen. Bewertung über negative Manhattan-Distanz zum Brettzentrum.
 * Danach wird die Option automatisch schwächer (Termination).
 */
public class CenterControlOption implements Option {

  private static final Set<Integer> centerSquares = Set.of(
      18, 19, 20, 21,
      26, 27, 28, 29,
      34, 35, 36, 37,
      42, 43, 44, 45
  );

  private static final long CENTER_BITS = 66229406269440L;
  private static final Random rand  = new Random();

  private static final String NAME = "CenterControlOption";

  @Override
  public boolean isBoardInInitiationSet(Board board, Constants.PLAYER_COLOR playerColor) {
    return ((board.getWhite() | board.getBlack()) & CENTER_BITS) != CENTER_BITS;
  }

  @Override public List<Board> initiationSet() { return Collections.emptyList(); }
  @Override public boolean shouldTerminate(Board board, Constants.PLAYER_COLOR playerColor) {
    return (board.getWhiteCount() + board.getBlackCount()) >= 24;
  }

  @Override
  public Move getBestMove(Board board, List<Move> possibleMoves) {
    //get most discs in Center
    PLAYER_COLOR color = possibleMoves.getFirst().getColor();
    int myCenterDiscs = 0;
    int enemyCenterDiscs = 0;
    long whiteCenterDiscs = board.getWhite() & CENTER_BITS;
    long blackCenterDiscs = board.getBlack() & CENTER_BITS;
    if(color == PLAYER_COLOR.WHITE) {
      myCenterDiscs = Long.bitCount(whiteCenterDiscs);
      enemyCenterDiscs = Long.bitCount(blackCenterDiscs);
    } else {
      myCenterDiscs = Long.bitCount(blackCenterDiscs);
      enemyCenterDiscs = Long.bitCount(whiteCenterDiscs);
    }

    //filter possible so only moves in the center 16 fields are considered
    List<Move> centerMoves = possibleMoves.stream().filter(
        move -> centerSquares.contains(move.getPosition()
        )
    ).toList();

    if(centerMoves.isEmpty()) {
      return possibleMoves.get(rand.nextInt(possibleMoves.size()));
    }

    if(myCenterDiscs <= enemyCenterDiscs){
      // Kandidaten mit maximaler Center-Besetzung sammeln
      List<Move> bestCenterMoves = new ArrayList<>();
      int mostCenterDiscs = 0;
      for(Move move : centerMoves){
        Board clone = board.clone();
        clone.updateBoard(move.getPosition(), color == PLAYER_COLOR.WHITE);
        long wCenterDiscs = clone.getWhite() & CENTER_BITS;
        long bCenterDiscs = clone.getBlack() & CENTER_BITS;
        int centerDiscCount = (color == PLAYER_COLOR.WHITE)
            ? Long.bitCount(wCenterDiscs)
            : Long.bitCount(bCenterDiscs);
        if(centerDiscCount > mostCenterDiscs){
          mostCenterDiscs = centerDiscCount;
          bestCenterMoves.clear();
          bestCenterMoves.add(move);
        } else if(centerDiscCount == mostCenterDiscs){
          bestCenterMoves.add(move);
        }
      }
      // Unter den Zügen mit maximaler Center-Kontrolle wähle den leisesten
      Move quietMove = null;
      double bestScore = Double.POSITIVE_INFINITY;
      for(Move move : bestCenterMoves){
        Board clone = board.clone();
        clone.updateBoard(move.getPosition(), color == PLAYER_COLOR.WHITE);
        int frontierCount = countFrontierDiscs(clone, color);
        int maxWallLength = countMaxFrontierWall(clone, color);
        double score = frontierCount + 2.0 * maxWallLength;
        if(score < bestScore) {
          bestScore = score;
          quietMove = move;
        }
      }
      if(quietMove != null) {
        return quietMove;
      }
    } else {
      //get most quite move
      Move quiestMove = null;
      double bestScore = Double.POSITIVE_INFINITY;
      for(Move move : centerMoves){
        Board clone = board.clone();
        clone.updateBoard(move.getPosition(), color == PLAYER_COLOR.WHITE);
        int frontierCount = countFrontierDiscs(clone, color);
        int maxWallLength = countMaxFrontierWall(clone, color);

        double score = frontierCount + 2.0 * maxWallLength;

        if(score < bestScore) {
          bestScore = score;
          quiestMove = move;
        }
      }
      if(quiestMove != null) {
        return quiestMove;
      }
    }
    return centerMoves.get(rand.nextInt(possibleMoves.size()));
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
  public String toString() {
    return NAME;
  }
}
