package com.loginov.simulator.Screen;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.loginov.simulator.Evolved;

public abstract class InputApiAdapter extends InputAdapter implements Screen {
    private Evolved api;

    public InputApiAdapter(Evolved api){
        this.api = api;
    }
}
