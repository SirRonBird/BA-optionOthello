package com.mcgreedy.optionothello.engine;

public class Board {

    long black;
    long white;

    static final long FULL = -1L; // every 64 Bits set to 1

    static final int[] DIRECTIONS = { 8, -8, 1, -1, 9, -9, 7, -7 };
    static final long LEFT_EDGE = 0x0101010101010101L;
    static final long RIGHT_EDGE = 0x8080808080808080L;

    static final long START_WHITE = 0x0000001008000000L;
    static final long START_BLACK = 0x0000000810000000L;


    public Board() {
        black = START_BLACK;
        white = START_WHITE;
    }


}
