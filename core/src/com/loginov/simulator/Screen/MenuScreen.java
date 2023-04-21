package com.loginov.simulator.Screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import static com.loginov.simulator.Evolved.proxy;

public class MenuScreen extends InputApiAdapter {
    private TextButton startButton;
    private TextButton menuButton;
    private Skin buttonSkin;
    private Stage stage;
    private static TextureAtlas atlas;
    private OrthographicCamera apiCamera;
    private Viewport apiPort;
    private InputMultiplexer multiplexer;

    public MenuScreen(){
        super(proxy);
        apiCamera = new OrthographicCamera();
        apiPort = new ScreenViewport(apiCamera);

        Table buttonTable = new Table();
        buttonSkin = new Skin();
        atlas = new TextureAtlas("flat-earth/skin/flat-earth-ui.atlas");
        buttonSkin.addRegions(atlas);
        buttonSkin.load(Gdx.files.internal("flat-earth/skin/flat-earth-ui.json"));
        startButton = new TextButton("Start", buttonSkin, "default");
        menuButton = new TextButton("Simulator settings",buttonSkin,"default");
        buttonTable.add(startButton).width(Gdx.graphics.getWidth()/3).padBottom(startButton.getHeight()).row();
        buttonTable.add(menuButton).width(Gdx.graphics.getWidth()/3).padBottom(menuButton.getHeight()).row();
        buttonTable.setX(Gdx.graphics.getWidth()/2 - buttonTable.getWidth()/2);
        buttonTable.setY(Gdx.graphics.getHeight()-200);

        stage = new Stage(apiPort);
        stage.addActor(buttonTable);
        multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(stage);
        multiplexer.addProcessor(this);
        Gdx.input.setInputProcessor(multiplexer);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.8f,0.8f,0.8f,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        proxy.batch.setProjectionMatrix(apiCamera.combined);
        stage.act(delta);
        stage.draw();
        buttonPressed();
    }

    public void buttonPressed(){
        if(startButton.isPressed()){
            this.dispose();
            proxy.setScreen(new SimulatorScreen());
        }
        if(menuButton.isPressed()){
            this.dispose();
            proxy.setScreen(new SettingsScreen());
        }
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
        Gdx.input.setInputProcessor(null);
        multiplexer.clear();
        buttonSkin.dispose();
        stage.dispose();
        atlas.dispose();
    }

}
