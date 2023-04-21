package com.loginov.simulator;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.loginov.simulator.Screen.MenuScreen;
import com.loginov.simulator.Screen.SimulatorScreen;


public class Evolved extends Game {
    public SpriteBatch batch;
    public static Evolved proxy;
    public static Screen menu, simulation;
    public Stage stageMenu;
    public Stage stageSimulation;


    @Override
    public void create() {
        batch = new SpriteBatch();
        proxy = this;
        menu = new MenuScreen();
        simulation = new SimulatorScreen();
        stageMenu = new Stage(new ScreenViewport());
        stageSimulation = new Stage(new ScreenViewport());

        Gdx.input.setInputProcessor(stageMenu);
        setScreen(new MenuScreen());
    }


    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        super.dispose();
        menu.dispose();
        simulation.dispose();
    }


}
