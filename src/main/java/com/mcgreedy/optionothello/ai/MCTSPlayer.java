package com.mcgreedy.optionothello.ai;

import com.mcgreedy.optionothello.engine.Board;
import com.mcgreedy.optionothello.engine.Move;
import com.mcgreedy.optionothello.gamemanagement.Gamemanager;
import com.mcgreedy.optionothello.gamemanagement.Player;
import com.mcgreedy.optionothello.utils.Constants.PLAYER_COLOR;
import com.mcgreedy.optionothello.utils.Constants.PLAYER_TYPE;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class MCTSPlayer extends Player {

    private final MCTSOptions settings;
    private static int SIMULATION_LIMIT = 1000;
    private static double explorationConstant = 0;

    private final Map<Integer, int[]> globalRAVE = new HashMap<>();
    private final Map<Move, MASTStats> mastStats = new HashMap<>();
    private final Random random = new Random();

    private static final Logger LOGGER = LogManager.getLogger(MCTSPlayer.class);

    public MCTSPlayer(PLAYER_COLOR color, PLAYER_TYPE type, Gamemanager gamemanager, MCTSOptions mctsSettings) {
        super(color, type, gamemanager);
        this.settings = mctsSettings;
        explorationConstant = settings.explorationConstant();
        SIMULATION_LIMIT = settings.simulationLimit();
        LOGGER.info("Created MCTSPlayer with settings:" + settings);
    }

    @Override
    public Move getMove(Board board) {
        Node root = new Node(null, null, board, color, 0);
        for (int i = 0; i < SIMULATION_LIMIT; i++) {
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
        if (best == null || best.move == null) {
            return new Move(this.color, -1, 0, this.type);
        }
        Move originalMove = best.move;
        return new Move(originalMove.getColor(), originalMove.getPosition(), best.maxSimulationDepth, originalMove.getPlayerType());
    }

    private Move selectMASTMove(List<Move> moves) {
        double epsilon = 0.1;
        if (random.nextDouble() < epsilon) {
            return moves.get(random.nextInt(moves.size()));
        }
        return moves.stream()
                .max(Comparator.comparingDouble(m -> {
                    MASTStats stats = mastStats.get(m);
                    return (stats != null && stats.visits >= 5) ? stats.getWinRate() : 0.5;
                })).orElseGet(() -> moves.get(random.nextInt(moves.size())));
    }

    private PLAYER_COLOR toggleColor(PLAYER_COLOR color) {
        return (color == PLAYER_COLOR.WHITE) ? PLAYER_COLOR.BLACK : PLAYER_COLOR.WHITE;
    }

    private static class Node {
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
                this.untriedMoves = board.generateMovesAsList(player == PLAYER_COLOR.WHITE, depth, PLAYER_TYPE.MCTS);
                if (this.untriedMoves.isEmpty()) {
                    Move passMove = new Move(player, -1, depth, PLAYER_TYPE.MCTS);
                    this.untriedMoves.add(passMove);
                }
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
                        double winRate = (double) child.wins / (child.visits + 1e-6);
                        return winRate + explorationConstant * Math.sqrt(Math.log(visits + 1) / (child.visits + 1e-6));
                    }).thenComparingDouble(n -> random.nextDouble())
            ).orElseThrow();
        }

        Node bestChild() {
            return children.stream().max(Comparator.comparingInt(c -> c.visits)).orElse(null);
        }
    }

    private static class MASTStats {
        int visits = 0;
        int wins = 0;

        void update(boolean win) {
            visits++;
            if (win) wins++;
        }

        double getWinRate() {
            return (double) wins / (visits + 1e-6);
        }
    }
}

