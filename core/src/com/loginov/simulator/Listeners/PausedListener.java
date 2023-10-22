package com.loginov.simulator.Listeners;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.loginov.simulator.Enums.ApplicationState;
import com.loginov.simulator.Screen.SimulatorScreen;


public class PausedListener extends ClickListener {
    private ApplicationState screen;

    public PausedListener(ApplicationState screen){
        this.screen = screen;
    }

    @Override
    public void clicked(InputEvent event, float x, float y) {

    }

    public void setSimulationState(ApplicationState state){
    }
}
