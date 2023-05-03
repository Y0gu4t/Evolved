package com.loginov.simulator;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.loginov.simulator.Screen.MenuScreen;
import com.loginov.simulator.Screen.SimulatorScreen;
import com.loginov.simulator.util.ResourceManager;


public class Evolved extends Game {
    private SpriteBatch batch;
    public static Evolved proxy;
    private MenuScreen menu;
    private ResourceManager resourceManager;
    private SimulatorScreen simulation;
    public Stage stageMenu;
    public Stage stageSimulation;


    @Override
    public void create() {
        batch = new SpriteBatch();
        resourceManager = new ResourceManager();
        proxy = this;
        menu = new MenuScreen(this, resourceManager);
        /*simulation = new SimulatorScreen();
        stageMenu = new Stage(new ScreenViewport());
        stageSimulation = new Stage(new ScreenViewport());*/

        //Gdx.input.setInputProcessor(stageMenu);
        setScreen(menu);
    }


    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        super.dispose();
        batch.dispose();
        menu.dispose();
        simulation.dispose();
    }


    public SpriteBatch getBatch() {
        return batch;
    }

    public MenuScreen getMenu() {
        return menu;
    }

    public SimulatorScreen getSimulation() {
        return simulation;
    }

}
