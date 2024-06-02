package com.loginov.simulator;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.loginov.simulator.screen.MenuScreen;
import com.loginov.simulator.screen.SimulatorScreen;
import com.loginov.simulator.util.ResourceManager;


public class Evolved extends Game {
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    public static Evolved proxy;
    private MenuScreen menu;
    private ResourceManager resourceManager;
    private SimulatorScreen simulation;
    public Stage stageMenu;
    public Stage stageSimulation;


    @Override
    public void create() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        resourceManager = new ResourceManager();
        proxy = this;
        menu = new MenuScreen(this, resourceManager);
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
    }


    public SpriteBatch getBatch() {
        return batch;
    }

    public ShapeRenderer getShapeRenderer() { return shapeRenderer; }

    public MenuScreen getMenu() {
        return menu;
    }

    public SimulatorScreen getSimulation() {
        return simulation;
    }

}
