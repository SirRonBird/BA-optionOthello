package com.mcgreedy.optionothello.engine;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {

    /**
     * This class tests the logic of board generation and move generation
     * for the game of othello. Tests included are for the Board class and its
     * method generateAllPossibleMoves.
     */

    @Test
    void testGenerateAllPossibleMovesForNewGame() {
        Board board = new Board();

        long expectedBlack = 0x102004080000L;
        long expectedWhite = 0x80420100000L;
        long actualForBlack = board.generateAllPossibleMoves(false);
        long actualForWhite = board.generateAllPossibleMoves(true);

        assertEquals(expectedBlack, actualForBlack);
        assertEquals(expectedWhite, actualForWhite);
    }


    @Test
    void testGenerateAllPossibleMoves() {
        Board board = new Board();
        board.black = 0x201008080000L;
        board.white = 0xc10200000L;

        long expectedBlack = 0xa2220100000L;
        long expectedWhite = 0x102004100c00L;
        long actualForBlack = board.generateAllPossibleMoves(false);
        long actualForWhite = board.generateAllPossibleMoves(true);

        assertEquals(expectedBlack, actualForBlack);
        assertEquals(expectedWhite, actualForWhite);
    }

    @Test
    void testGenerateAllPossibleMovesEdges(){
        Board board = new Board();
        board.black = 0x905a260000265210L;
        board.white = 0x4820183c1c182400L;

        long expectedBlack = 0x240040026240086cL;
        long actualForBlack = board.generateAllPossibleMoves(false);

        assertEquals(expectedBlack, actualForBlack);
    }

    @Test
    void testLastMove(){
        Board board = new Board();
        board.black = 0xf7fffffffffffff7L;
        board.white = 0x800000000000000L;

        long expectedBlack = 0x0L;
        long expectedWhite = 0x8L;

        long actualForBlack = board.generateAllPossibleMoves(false);
        long actualForWhite = board.generateAllPossibleMoves(true);

        assertEquals(expectedBlack, actualForBlack);
        assertEquals(expectedWhite, actualForWhite);


    }

    @Test
    void testNoMovesPossibleBlack(){
        Board board = new Board();
        board.black = 0x7f261c0818182800L;
        board.white = 0x80d9e377e7e7d7ffL;

        long expectedBlack = 0x8000000000L;
        long actualForBlack = board.generateAllPossibleMoves(false);

        assertEquals(expectedBlack, actualForBlack);

    }

    @Test
    void testNoMovesPossibleWhite(){
        Board board = new Board();
        board.black = 0x7f394143735b5f7fL;
        board.white = 0x80c6bebc8c848080L;

        long expectedWhite = 0x202000L;
        long actualForWhite = board.generateAllPossibleMoves(true);

        assertEquals(expectedWhite, actualForWhite);

    }

    @Test
    void testNoMovesPossible(){
        Board board = new Board();
        board.black = 0x1L;
        board.white = 0x0L;

        long expectedBlack = 0x0L;
        long actualForBlack = board.generateAllPossibleMoves(false);

        assertEquals(expectedBlack, actualForBlack);
    }
}