package com.mcgreedy.optionothello.ai;

public record MCTSOptions(int simulationLimit, double explorationConstant, boolean useRave,
                          boolean useMast) {

    @Override
    public String toString() {
        return "MCTSOptions{" +
            "simulationLimit=" + simulationLimit +
            ", explorationConstant=" + explorationConstant +
            '}';
    }
}
