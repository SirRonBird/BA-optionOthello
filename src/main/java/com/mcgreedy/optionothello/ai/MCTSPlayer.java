package com.mcgreedy.optionothello.ai;

import com.mcgreedy.optionothello.engine.Board;
import com.mcgreedy.optionothello.engine.Move;
import com.mcgreedy.optionothello.engine.MoveStatistics;
import com.mcgreedy.optionothello.gamemanagement.Gamemanager;
import com.mcgreedy.optionothello.gamemanagement.Player;
import com.mcgreedy.optionothello.utils.Constants.PLAYER_COLOR;
import com.mcgreedy.optionothello.utils.Constants.PLAYER_TYPE;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class MCTSPlayer extends Player {

    private final MCTSSettings settings;
    private final Random rand = new Random();
    private static final Logger LOGGER = LogManager.getLogger(MCTSPlayer.class);
    private int nodeCount = 0;

    //MAST
    private Map<Move, Integer> mastVisits = new HashMap<>();
    private Map<Move, Double> mastValues = new HashMap<>();

    public MCTSPlayer(PLAYER_COLOR color, PLAYER_TYPE type, Gamemanager gamemanager, MCTSSettings settings) {
        super(color, type, gamemanager);
        this.settings = settings;
        LOGGER.info("Created MCTSPlayer with settings: {}", settings);
    }

    @Override
    public Move getMove(Board board) {
        Node root = new Node(null, null, board, color, 0);
        nodeCount = 0;
        long duration = 1000 + 1000 * searchTimeLimit;
        long startTime = System.currentTimeMillis();

        int simulations = 0;

        while (
            (searchTimeLimit >= 0 && System.currentTimeMillis() - startTime < duration) ||
                (simulationLimit > 0 && simulations <= simulationLimit)
        ) {
            Node node = root;
            Board boardClone = board.clone();
            List<Move> rolloutMoves = new ArrayList<>();

            // Selection
            while (!node.untriedMovesAvailable() && node.hasChildren()) {
                node = node.selectChild();
                boardClone.updateBoard(node.move.getPosition(), node.move.getColor() == PLAYER_COLOR.WHITE);
                rolloutMoves.add(node.move);
            }

            // Expansion
            if (node.untriedMovesAvailable()) {
                Move move = node.untriedMoves.removeFirst();
                boardClone.updateBoard(move.getPosition(), move.getColor() == PLAYER_COLOR.WHITE);
                Move moveWithDepth = new Move(move.getColor(), move.getPosition(), node.depth + 1, move.getPlayerType());
                Node child = new Node(node, moveWithDepth, boardClone.clone(), toggleColor(node.player), node.depth + 1);
                node.children.add(child);
                nodeCount++;
                node = child;
                rolloutMoves.add(moveWithDepth);
            }

            // Simulation
            PLAYER_COLOR simColor = node.player;
            int simDepth = node.depth + 1;
            while (!boardClone.isGameOver()) {
                List<Move> moves = boardClone.generateMovesAsList(simColor == PLAYER_COLOR.WHITE, simDepth, PLAYER_TYPE.MCTS);
                if (moves.isEmpty()) {
                    simColor = toggleColor(simColor);
                    continue;
                }
                Move move;
                if(settings.useMast()){
                    move = selectMoveWithMAST(moves);
                } else {
                    move = moves.get(rand.nextInt(moves.size()));
                }
                boardClone.updateBoard(move.getPosition(), move.getColor() == PLAYER_COLOR.WHITE);
                rolloutMoves.add(move);
                simColor = toggleColor(simColor);
                simDepth++;
            }

            // Backpropagation
            PLAYER_COLOR winner = boardClone.getWinner();

            for (Node n = node; n != null; n = n.parent) {
                n.visits++;

                // ±1 Reward
                if (winner == this.color) {
                    n.wins += 1;
                } else {
                    n.wins -= 1;
                }

                n.maxSimulationDepth = Math.max(n.maxSimulationDepth, simDepth);

                if (settings.useRave()) {
                    for (Move m : rolloutMoves) {
                        n.raveVisits.put(m, n.raveVisits.getOrDefault(m, 0) + 1);

                        if (winner == this.color) {
                            n.raveValues.put(m, n.raveValues.getOrDefault(m, 0.0) + 1.0);
                        } else {
                            n.raveValues.put(m, n.raveValues.getOrDefault(m, 0.0) - 1.0);
                        }
                    }
                }
            }

            // Update MAST values if enabled
            if (settings.useMast()) {
                for (Move m : rolloutMoves) {
                    mastVisits.put(m, mastVisits.getOrDefault(m, 0) + 1);
                    double reward = winner == this.color ? 1.0 : -1.0;
                    mastValues.put(m, mastValues.getOrDefault(m, 0.0) + reward);
                }
            }

        simulations++;
        }

        Node best = root.bestChild();
        if (best == null || best.move == null) {
            Move passMove = new Move(this.color, -1, 0, this.type);
            passMove.setStatistics(new MoveStatistics());
            return passMove;
        }

        Move move = best.move;
        long searchTime = System.currentTimeMillis() - startTime;
        MoveStatistics stats = new MoveStatistics(findMaxDepth(root), null, countNodes(root), searchTime);
        move.setStatistics(stats);

        /*try {
            exportTreeToDot(root);}
        catch (IOException e) {
            LOGGER.error("Failed to save tree as Dot: {}", e.getMessage());
        }*/

        return move;
    }

    private Move selectMoveWithMAST(List<Move> moves) {
        double tau = 10; // Temperatur
        double sum = 0.0;
        double[] scores = new double[moves.size()];

        for (int i = 0; i < moves.size(); i++) {
            Move m = moves.get(i);
            double q = mastValues.getOrDefault(m, 0.0) / (mastVisits.getOrDefault(m, 1));
            scores[i] = Math.exp(q / tau);
            sum += scores[i];
        }

        double r = Math.random() * sum;
        for (int i = 0; i < moves.size(); i++) {
            r -= scores[i];
            if (r <= 0) return moves.get(i);
        }

        return moves.get(moves.size() - 1);
    }

    @Override
    public void resetMAST() {
        if(settings.useMast()){
            mastValues.clear();
            mastVisits.clear();
        }
    }

    private PLAYER_COLOR toggleColor(PLAYER_COLOR color) {
        return (color == PLAYER_COLOR.WHITE) ? PLAYER_COLOR.BLACK : PLAYER_COLOR.WHITE;
    }

    public static int countNodes(Node root) {
        if (root == null) return 0;
        int count = 0;
        Queue<Node> queue = new LinkedList<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            Node current = queue.poll();
            count++;
            queue.addAll(current.children);
        }
        return count;
    }

    public static int findMaxDepth(Node root) {
        if (root == null) return Integer.MIN_VALUE;
        int max = root.depth;
        for (Node child : root.children) max = Math.max(max, findMaxDepth(child));
        return max;
    }

    private class Node {
        Node parent;
        List<Node> children = new ArrayList<>();
        LinkedList<Move> untriedMoves;
        Move move;
        Board board;
        PLAYER_COLOR player;
        int wins = 0;
        int visits = 0;
        int depth;
        int maxSimulationDepth = 0;

        // RAVE
        Map<Move, Integer> raveVisits = new HashMap<>();
        Map<Move, Double> raveValues = new HashMap<>();

        Node(Node parent, Move move, Board board, PLAYER_COLOR player, int depth) {
            this.parent = parent;
            this.move = move;
            this.board = board;
            this.player = player;
            this.depth = depth;

            if (!board.isGameOver()) {
                this.untriedMoves = new LinkedList<>(board.generateMovesAsList(player == PLAYER_COLOR.WHITE, depth, PLAYER_TYPE.MCTS));
                if (this.untriedMoves.isEmpty()) this.untriedMoves.add(new Move(player, -1, depth, PLAYER_TYPE.MCTS));
            } else this.untriedMoves = new LinkedList<>();
        }

        boolean untriedMovesAvailable() {
            return !untriedMoves.isEmpty();
        }

        boolean hasChildren() {
            return !children.isEmpty();
        }

        Node selectChild() {
            return children.stream().max(Comparator.comparingDouble(this::uctRaveValue)).orElseThrow();
        }

        Node bestChild() {
            LOGGER.info("Children: {}", children);
            return children.stream().max(Comparator.comparingDouble(c ->(double) c.wins/c.visits)).orElse(null);
        }

        private double uctRaveValue(Node child) {
            double qValue = child.wins / (child.visits + 1e-6);

            double exploration = settings.explorationConstant() *
                Math.sqrt(2 * Math.log(this.visits + 1) / (child.visits + 1e-6));

            if (settings.useRave() && child.move != null) {
                int raveN = this.raveVisits.getOrDefault(child.move, 0);
                double raveW = this.raveValues.getOrDefault(child.move, 0.0);
                double amaf = (raveN > 0) ? raveW / raveN : 0.0;

                double k = 1000;
                double beta = Math.sqrt(k / (3.0 * this.visits + k));

                double mixedValue = (1 - beta) * qValue + beta * amaf;

                return mixedValue + exploration;
            }

            return qValue + exploration;
        }

        @Override
        public String toString() {
            return "{ Node (" + move.getPosition() + ") " + wins + ":" + visits + "}\n";
        }
    }

    public static void exportTreeToDot(Node root) throws IOException {
        String projectDir = System.getProperty("user.dir");
        File directory = new File(projectDir, "savegames/treevis");

        if (!directory.exists()) {
            directory.mkdirs();
        }

        int depth = findMaxDepth(root);
        int nodes = countNodes(root);
        long timestamp = System.currentTimeMillis();

        File file = new File(directory,
            String.format("mcts_tree_d%d_n%d_%d.dot", depth, nodes, timestamp));

        try (PrintWriter out = new PrintWriter(new FileWriter(file))) {
            out.println("digraph G {");
            Queue<Node> queue = new LinkedList<>();
            queue.add(root);

            while (!queue.isEmpty()) {
                Node current = queue.poll();
                String currentId = "n" + System.identityHashCode(current);

                // Knoten selbst (nur Punkt)
                out.println("  " + currentId + " [label=\"\", shape=circle, width=0.2, style=filled, fillcolor=black];");

                for (Node child : current.children) {
                    String childId = "n" + System.identityHashCode(child);
                    out.println("  " + currentId + " -> " + childId + ";");
                    queue.add(child);
                }
            }

            out.println("}");
        }
    }
}



