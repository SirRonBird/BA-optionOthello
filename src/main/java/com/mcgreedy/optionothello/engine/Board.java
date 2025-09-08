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
    public long mask;
    public String name;

    static final long FULL = -1L; // every 64 Bits set to 1

    public static final int[] DIRECTIONS = {-8, 8, 1, -1, -7, 7, -9, 9};
    static final long LEFT_EDGE = 0x0101010101010101L;
    static final long RIGHT_EDGE = 0x8080808080808080L;
    //static final long TOP_EDGE = 0xffL;
    //static final long BOTTOM_EDGE = 0xff00000000000000L;

    static final long START_WHITE = 0x0000001008000000L;
    static final long START_BLACK = 0x0000000810000000L;

    /*static final long START_WHITE = 17246978048L;
    static final long START_BLACK = 17695668175360L*/;

    public long startWhite = START_WHITE;
    public long startBlack = START_BLACK;

    private static final Logger LOGGER = LogManager.getLogger(Board.class);

    public Board() {
        black = START_BLACK;
        white = START_WHITE;
        this.isMask = false;
    }

    public Board(long black, long white) {
        this.black = black;
        this.white = white;
        this.isMask = false;
    }

    public Board(String name, boolean isMask) {
        this.name = name;
        this.isMask = isMask;
        this.mask = 0L;
    }

    public void updateMask(int position) {
        this.mask ^= (1L << position);
    }

    public void clearMask(){
        if(this.isMask){
            this.mask = 0L;
        }
    }

    public void updateBoard(int position, boolean isWhite) {
        if(position == -1){
            return;
        }
        long move = 1L << position;
        long player = isWhite ? white : black;
        long opponent = isWhite ? black : white;

        long flipped = 0L;

        for (int dir : DIRECTIONS) {
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

    public boolean isBoardFull() {
        return (black | white) == FULL;
    }

    public long generateAllPossibleMoves(boolean forWhite) {
        long player = forWhite ? white : black;
        long opponent = forWhite ? black : white;

        long empty = ~(player | opponent);
        long possibleMoves = 0L;

        for (int dir : DIRECTIONS) {
            long mask = shift(player, dir) & opponent;

            for (int i = 0; i < 5; i++) {
                mask |= shift(mask, dir) & opponent;
            }

            long candidates = shift(mask, dir) & empty;

            possibleMoves |= candidates;
        }

        return possibleMoves;
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

    public long getBlack() {
        return black;
    }

    public long getWhite() {
        return white;
    }

    public boolean boardIsHittingMask(Board mask, PLAYER_COLOR color) {
        if(mask.isMask){
            long boardLong = color == PLAYER_COLOR.BLACK ? this.getBlack() : this.getWhite();
            return (boardLong & mask.mask) != 0;
        }
        return false;
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

    public boolean isGameOver() {
        return isBoardFull() || generateAllPossibleMoves(true) == 0L && generateAllPossibleMoves(false) == 0L;
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

    public int getWhiteCount(){ return Long.bitCount(white); }
    public int getBlackCount(){ return Long.bitCount(black); }

    public int getValue(boolean forWhite) {
        if(forWhite){
            return getWhiteCount() - getBlackCount();
        } else {
            return getBlackCount() - getWhiteCount();
        }
    }
}
