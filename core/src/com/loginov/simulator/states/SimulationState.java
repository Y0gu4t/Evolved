package com.loginov.simulator.states;

public enum SimulationState {
    DAY(16f),
    NIGHT(DAY.duration/2),
    ;

    private final float duration;
    SimulationState(float generatedTime) {
        this.duration = generatedTime;
    }

    public float getDuration() {
        return duration;
    }
}
