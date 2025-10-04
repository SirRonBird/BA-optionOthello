package com.mcgreedy.optionothello.ai;

public record MCTSSettings(
    double explorationConstant,
    boolean useMast,
    boolean useRave,
    double tau,
    double k
) {


    @Override
    public String toString() {
        return "MCTSSettings{" +
            "explorationConstant=" + explorationConstant +
            ", useMast=" + useMast +
            ", useRave=" + useRave +
            '}';
    }
}
