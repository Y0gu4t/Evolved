package com.loginov.simulator.Screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.loginov.simulator.Evolved;

public class TestScreen extends InputApiAdapter {
    private OrthographicCamera apiCamera;
    private Viewport apiPort;
    private Evolved api;
    private Skin buttonSkin;
    private TextureAtlas atlas;
    private TextButton startButton;
    private Stage stage;
    private InputMultiplexer multiplexer;

    public TestScreen(Evolved api) {
        super(api);
        this.api = api;
        apiCamera = new OrthographicCamera();
        apiPort = new ScreenViewport(apiCamera);
        buttonSkin = new Skin();
        atlas = new TextureAtlas("flat-earth/skin/flat-earth-ui.atlas");
        buttonSkin.addRegions(atlas);
        buttonSkin.load(Gdx.files.internal("flat-earth/skin/flat-earth-ui.json"));
        startButton = new TextButton("Start", buttonSkin, "default");
        startButton.setBounds(100, 100, 200, 50);
        Button b = new Button();
        stage = new Stage(apiPort);
        stage.addActor(startButton);
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
        api.batch.setProjectionMatrix(apiCamera.combined);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        apiPort.update(width,height);
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
        buttonSkin.dispose();
        stage.dispose();
        atlas.dispose();
    }
}
