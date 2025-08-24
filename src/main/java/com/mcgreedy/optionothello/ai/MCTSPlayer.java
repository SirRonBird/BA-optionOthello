package com.mcgreedy.optionothello.ai;

import com.mcgreedy.optionothello.engine.Board;
import com.mcgreedy.optionothello.engine.Move;
import com.mcgreedy.optionothello.engine.MoveStatistics;
import com.mcgreedy.optionothello.gamemanagement.Gamemanager;
import com.mcgreedy.optionothello.gamemanagement.Player;
import com.mcgreedy.optionothello.utils.Constants.PLAYER_COLOR;
import com.mcgreedy.optionothello.utils.Constants.PLAYER_TYPE;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class MCTSPlayer extends Player {

    private final MCTSSettings settings;

    private final Random random = new Random();

    private static final Logger LOGGER = LogManager.getLogger(MCTSPlayer.class);

    private int nodeCount = 0;

    public MCTSPlayer(PLAYER_COLOR color, PLAYER_TYPE type, Gamemanager gamemanager, MCTSSettings mctsSettings) {
        super(color, type, gamemanager);
        this.settings = mctsSettings;
        LOGGER.info("Created MCTSPlayer with settings: {}" , settings);
    }

    @Override
    public Move getMove(Board board) {
        Node root = new Node(null, null, board, color, 0);
        nodeCount =0;

        long duration = 1000 + 1000 * searchTimeLimit;
        long startTime = System.currentTimeMillis();

        //for (int i = 0; i < SIMULATION_LIMIT; i++) {
        while (System.currentTimeMillis() - startTime < duration) {
            Node node = root;
            Board boardClone = board.clone();
            List<Move> simulationPath = new ArrayList<>();

            // Selection
            while (node.untriedMoves.isEmpty() && node.hasChildren()) {
                node = node.selectChild();
                boardClone.updateBoard(node.move.getPosition(), node.move.getColor() == PLAYER_COLOR.WHITE);
                simulationPath.add(node.move);
            }

            // Expansion
            if (!node.untriedMoves.isEmpty()) {
                Move move = node.untriedMoves.removeFirst();
                boardClone.updateBoard(move.getPosition(), move.getColor() == PLAYER_COLOR.WHITE);
                Move moveWithDepth = new Move(move.getColor(), move.getPosition(), node.depth + 1, move.getPlayerType());
                Node child = new Node(node, moveWithDepth, boardClone.clone(), toggleColor(node.player), node.depth + 1);
                nodeCount++;
                node.children.add(child);
                node = child;
                simulationPath.add(moveWithDepth);
            }

            // Simulation
            PLAYER_COLOR simColor = node.player;
            int simulationDepth = node.depth + 1;
            while (!boardClone.isGameOver()) {
                List<Move> moves = boardClone.generateMovesAsList(simColor == PLAYER_COLOR.WHITE, simulationDepth, PLAYER_TYPE.MCTS);
                if (moves.isEmpty()) {
                    simColor = toggleColor(simColor);
                    continue;
                }

                Move move = moves.get(random.nextInt(moves.size()));


                boardClone.updateBoard(move.getPosition(), move.getColor() == PLAYER_COLOR.WHITE);
                simulationPath.add(move);
                simColor = toggleColor(simColor);
                simulationDepth++;
            }

            // Backpropagation
            PLAYER_COLOR winner = boardClone.getWinner();
            for (Node n = node; n != null; n = n.parent) {
                n.visits++;
                if (winner == this.color) n.wins++;
                n.maxSimulationDepth = Math.max(n.maxSimulationDepth, simulationDepth);
            }
        }

        Node best = root.bestChild();
        //LOGGER.info("Maxdepth: {}", findMaxDepth(best));
        if (best == null || best.move == null) {
            Move passMove = new Move(this.color, -1, 0, this.type);
            passMove.setStatistics(new MoveStatistics());
            return passMove;
        }
        Move move = best.move;
        long searchTime = System.currentTimeMillis() - startTime;

        MoveStatistics statistics = new MoveStatistics(
            findMaxDepth(root),
            null,
            countNodes(root),
            searchTime
        );
        move.setStatistics(statistics);
        return move;
    }

    public static int countNodes(Node root) {
        if (root == null) return 0;

        int count = 0;
        Queue<Node> queue = new LinkedList<>();
        queue.add(root);

        while (!queue.isEmpty()){
            Node current = queue.poll();
            count++;
            queue.addAll(current.children);
        }

        return count;
    }

    public static int findMaxDepth(Node root) {
        if (root == null) return Integer.MIN_VALUE;

        int max = root.depth;

        for (Node child : root.children) {
            int childMax = findMaxDepth(child);
            if (childMax > max) {
                max = childMax;
            }
        }

        return max;
    }

    private PLAYER_COLOR toggleColor(PLAYER_COLOR color) {
        return (color == PLAYER_COLOR.WHITE) ? PLAYER_COLOR.BLACK : PLAYER_COLOR.WHITE;
    }

    private class Node {
        Node parent;
        List<Node> children = new ArrayList<>();
        List<Move> untriedMoves;

        Move move;
        Board board;
        PLAYER_COLOR player;
        int wins = 0;
        int visits = 0;
        int depth;
        int maxSimulationDepth = 0;

        private final Random random = new Random();

        Node(Node parent, Move move, Board board, PLAYER_COLOR player, int depth) {
            this.parent = parent;
            this.move = move;
            this.board = board;
            this.player = player;
            this.depth = depth;

            if (!board.isGameOver()) {
                long startMoveCreation = System.currentTimeMillis();
                this.untriedMoves = board.generateMovesAsList(player == PLAYER_COLOR.WHITE, depth, PLAYER_TYPE.MCTS);
                if (this.untriedMoves.isEmpty()) {
                    Move passMove = new Move(player, -1, depth, PLAYER_TYPE.MCTS);
                    this.untriedMoves.add(passMove);
                }
                long endMoveCreation = System.currentTimeMillis();
                long moveCreation = endMoveCreation - startMoveCreation;
                //LOGGER.info("It took {} ms to create all possible Moves for MCTS", moveCreation);
            } else {
                this.untriedMoves = new ArrayList<>();
            }
        }

        boolean hasChildren() {
            return !this.children.isEmpty();
        }

        Node selectChild() {
            return children.stream().max(Comparator.<Node>comparingDouble(
                    child -> {
                        double winRate =  child.wins / (child.visits + 1e-6);
                        return winRate + settings.explorationConstant() * Math.sqrt((2*Math.log(visits)) / child.visits);
                    }).thenComparingDouble(n -> random.nextDouble())
            ).orElseThrow();
        }

        Node bestChild() {
            return children.stream().max(Comparator.comparingInt(c -> c.visits)).orElse(null);
        }
        @Override
        public String toString(){
            return "[ Node (" + maxSimulationDepth + "):" + children +"]";
        }
    }



}

