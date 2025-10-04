package com.mcgreedy.optionothello.engine;


import com.mcgreedy.optionothello.utils.Constants;
import com.mcgreedy.optionothello.utils.Constants.PLAYER_COLOR;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Board {

    long black;
    long white;

    boolean isMask;
    long mask;
    String name;

    private static final long FULL = -1L; // every 64 Bits set to 1
    private static final long LEFT_EDGE = 0x0101010101010101L;
    private static final long RIGHT_EDGE = 0x8080808080808080L;
    private static final long START_WHITE = 0x0000001008000000L;
    private static final long START_BLACK = 0x0000000810000000L;

    //For testing
    /*static final long START_WHITE = 8232037175148945520L;
    static final long START_BLACK = 0x81416d4ded6f1400L;*/

    public static final long WHITE_START_BOARD = START_WHITE;
    public static final long BLACK_START_BOARD = START_BLACK;

    private static final Logger LOGGER = LogManager.getLogger(Board.class);

    /* Start board */
    public Board() {
        black = START_BLACK;
        white = START_WHITE;
        this.isMask = false;
    }

    /* Special board */
    public Board(long black, long white) {
        this.black = black;
        this.white = white;
        this.isMask = false;
    }

    /* Mask board */
    public Board(String name, boolean isMask) {
        this.name = name;
        this.isMask = isMask;
        this.mask = 0L;
    }

    public void updateBoard(int position, boolean isWhite) {
        if(position == -1){
            return;
        }
        long move = 1L << position;
        long player = isWhite ? white : black;
        long opponent = isWhite ? black : white;

        long flipped = 0L;

        for (int dir : Constants.DIRECTIONS) {
            long mask = 0L;
            long current = shift(move, dir);

            while ((current != 0) && ((current & opponent) != 0)) {
                mask |= current;
                current = shift(current, dir);
            }

            if ((current & player) != 0) {
                flipped |= mask;
            }
        }

        if (isWhite) {
            white |= move | flipped;
            black &= ~flipped;
        } else {
            black |= move | flipped;
            white &= ~flipped;
        }
    }

    public long generateAllPossibleMoves(boolean forWhite) {
        long player = forWhite ? white : black;
        long opponent = forWhite ? black : white;

        long empty = ~(player | opponent);
        long possibleMoves = 0L;

        for (int dir : Constants.DIRECTIONS) {
            long mask = shift(player, dir) & opponent;

            for (int i = 0; i < 5; i++) {
                mask |= shift(mask, dir) & opponent;
            }

            long candidates = shift(mask, dir) & empty;

            possibleMoves |= candidates;
        }

        return possibleMoves;
    }

    public List<Move> generateMovesAsList(boolean forWhite, int searchDepth, Constants.PLAYER_TYPE playerType) {
        long bitboard = generateAllPossibleMoves(forWhite);
        List<Move> moves = new ArrayList<>();
        LOGGER.debug("Possible moves: {}", bitboard);
        Constants.PLAYER_COLOR color = forWhite ? Constants.PLAYER_COLOR.WHITE : Constants.PLAYER_COLOR.BLACK;

        for (int i = 0; i < Constants.CELL_COUNT; i++) {
            if (((bitboard >>> i) & 1L) != 0) {
                moves.add(new Move(color, i, searchDepth, playerType));
            }
        }
        Collections.shuffle(moves, new Random());

        return moves;
    }

    private long shift(long bits, int direction) {
        return switch (direction) {
            case 1 -> (bits << 1) & ~LEFT_EDGE;      // Osten (nach rechts)
            case -1 -> (bits >>> 1) & ~RIGHT_EDGE;   // Westen (nach links)
            case 8 -> bits << 8;                     // Süden (nach unten)
            case -8 -> bits >>> 8;                   // Norden (nach oben)
            case 9 -> (bits << 9) & ~LEFT_EDGE;      // Südost
            case -9 -> (bits >>> 9) & ~RIGHT_EDGE;   // Nordwest
            case 7 -> (bits << 7) & ~RIGHT_EDGE;     // Südwest
            case -7 -> (bits >>> 7) & ~LEFT_EDGE;    // Nordost
            default -> throw new IllegalArgumentException("Ungültige Richtung: " + direction);
        };
    }

    public boolean isGameOver() {
        return isBoardFull() || generateAllPossibleMoves(true) == 0L && generateAllPossibleMoves(false) == 0L;
    }

    public Constants.PLAYER_COLOR getWinner() {
        int blackCount = Long.bitCount(black);
        int whiteCount = Long.bitCount(white);

        if (blackCount > whiteCount) {
            return Constants.PLAYER_COLOR.BLACK;
        } else if (whiteCount > blackCount) {
            return Constants.PLAYER_COLOR.WHITE;
        } else {
            return null; // oder z.B. Constants.PLAYER_COLOR.NONE
        }
    }

    public boolean isBoardFull() {
        return (black | white) == FULL;
    }

    public int getWhiteCount(){ return Long.bitCount(white); }

    public int getBlackCount(){ return Long.bitCount(black); }

    public int getValue(boolean forWhite) {
        if(forWhite){
            return getWhiteCount() - getBlackCount();
        } else {
            return getBlackCount() - getWhiteCount();
        }
    }

    public boolean boardIsHittingMask(Board mask, PLAYER_COLOR color) {
        if(mask.isMask){
            long boardLong = color == PLAYER_COLOR.BLACK ? this.getBlack() : this.getWhite();
            return (boardLong & mask.mask) != 0;
        }
        return false;
    }

    public long getBlack() {
        return black;
    }

    public long getWhite() {
        return white;
    }

    public long getMask() {
        return mask;
    }

    public void setMask(long mask) {
        this.mask = mask;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        if(this.isMask){
            return "Mask-Board{"+
                "mask="+ mask+ '}';
        }else {
            return "Board{" +
                "black=" + black +
                ", white=" + white +
                '}';
        }
    }

    @Override
    public Board clone() {
        if(this.isMask){
            Board clone = new Board(this.name,true);
            clone.mask = this.mask;
            return clone;
        } else {
            return new Board(this.black, this.white);
        }
    }
}
