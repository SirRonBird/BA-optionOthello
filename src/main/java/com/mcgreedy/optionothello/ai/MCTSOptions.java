package com.mcgreedy.optionothello.ai;

public class MCTSOptions {

    private final int simulationLimit;
    private final double explorationConstant;


    public MCTSOptions(int simulationLimit, double explorationConstant) {
        this.simulationLimit = simulationLimit;
        this.explorationConstant = explorationConstant;

    }

    public int getSimulationLimit() {
        return simulationLimit;
    }

    public double getExplorationConstant() {
        return explorationConstant;
    }

    @Override
    public String toString() {
        return "MCTSOptions{" +
                "simulationLimit=" + simulationLimit +
                ", explorationConstant=" + explorationConstant +
                '}';
    }
}
