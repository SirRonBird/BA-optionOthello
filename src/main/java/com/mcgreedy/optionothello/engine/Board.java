package com.mcgreedy.optionothello.engine;


public class Board {

    long black;
    long white;

    static final long FULL = -1L; // every 64 Bits set to 1

    static final int[] DIRECTIONS = { -8, 8, 1, -1, -7, 7, -9, 9 };
    static final long LEFT_EDGE = 0x0101010101010101L;
    static final long RIGHT_EDGE = 0x8080808080808080L;
    //static final long TOP_EDGE = 0xffL;
    //static final long BOTTOM_EDGE = 0xff00000000000000L;

    static final long START_WHITE = 0x0000001008000000L;
    static final long START_BLACK = 0x0000000810000000L;

    //static final long START_WHITE = 0x80d9e377e7e7d7ffL;
    //static final long START_BLACK = 0x7f261c0818182800L;

    public Board() {
        black = START_BLACK;
        white = START_WHITE;
    }

    public void updateBoard(int position, boolean isWhite) {
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

    public boolean isBoardFull(){
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
}
