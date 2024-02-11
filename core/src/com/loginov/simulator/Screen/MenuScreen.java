package com.loginov.simulator.Screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.loginov.simulator.Evolved;
import com.loginov.simulator.util.ResourceManager;

public class MenuScreen extends BaseScreen {
    private Table menuTable;
    private OrthographicCamera apiCamera;
    private Viewport apiPort;
    private float stateTime;

    public MenuScreen(Evolved proxy, ResourceManager resourceManager){
        super(proxy, resourceManager);
        menuTable = createTable();
        stage = new Stage();
        apiCamera = new OrthographicCamera();
        apiPort = new ScreenViewport(apiCamera);
        handleStartButton();
        handleSettingsButton();
    }


    /**
     * create start button with listener
     */
    private void handleStartButton(){
        createButton("Start", menuTable.getWidth()/3, 50, 0 , menuTable.getHeight()/10, menuTable);
        Actor thisButton = menuTable.getCells().get(0).getActor();
        thisButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                proxy.setScreen(new SimulatorScreen(proxy, (BaseScreen) proxy.getScreen(), resourceManager));
            }
        });
    }

    /**
     * create setting button with listener
     */
    private void handleSettingsButton(){
        createButton("Settings", menuTable.getWidth()/3, 50, 0 , menuTable.getHeight()/10, menuTable);
        Actor thisButton = menuTable.getCells().get(1).getActor();
        thisButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                proxy.setScreen(new SettingsScreen(proxy, (BaseScreen) proxy.getScreen(), resourceManager));
            }
        });
    }

    @Override
    public void show() {
        stage.addActor(menuTable);
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        stateTime += Gdx.graphics.getDeltaTime();
        Gdx.gl.glClearColor(250/255f, 235/255f, 215/255f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        proxy.getBatch().setProjectionMatrix(apiCamera.combined);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        apiPort.update(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        super.dispose();
        Gdx.input.setInputProcessor(null);
        stage.dispose();
    }

}
