package com.loginov.simulator.Listeners;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.loginov.simulator.Screen.SimulatorScreen;

public class PausedListener extends ClickListener {
    private SimulatorScreen screen;

    public PausedListener(SimulatorScreen screen){
        this.screen = screen;
    }

    @Override
    public void clicked(InputEvent event, float x, float y) {
        switch(screen.getState()){
            case SimulatorScreen.SIMULATION_RUNNING:
                screen.setState(SimulatorScreen.SIMULATION_PAUSED);
                break;
            case SimulatorScreen.SIMULATION_PAUSED:
                screen.setState(SimulatorScreen.SIMULATION_RUNNING);
                break;
        }
    }
}
