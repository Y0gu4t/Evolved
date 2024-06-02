package com.loginov.simulator.screen;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.loginov.simulator.Evolved;

public abstract class InputApiAdapter extends InputAdapter implements Screen {
    private final Evolved api;

    public InputApiAdapter(Evolved api){
        this.api = api;
    }
}
