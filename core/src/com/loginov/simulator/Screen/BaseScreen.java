package com.loginov.simulator.Screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.loginov.simulator.Evolved;
import com.loginov.simulator.util.ResourceManager;


public class BaseScreen implements Screen {
    protected final Evolved proxy;
    protected ResourceManager resourceManager;
    protected OrthographicCamera apiCam;
    // viewport that keeps aspect ratios of the game when resizing
    protected Viewport viewport;
    // stage of each screen
    protected Stage stage;

    public BaseScreen(Evolved proxy, ResourceManager resourceManager){
        this.proxy = proxy;
        this.resourceManager = resourceManager;
    }




    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {

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

    }

    public OrthographicCamera getApiCam(){
        return apiCam;
    }

    public Stage getStage(){
        return stage;
    }

    public Table createTable(){
        Table table = new Table();
        table.setBounds(0, 0, (float) Gdx.graphics.getWidth(), (float) Gdx.graphics.getHeight());
        return table;
    }

    public void createButton(String name, float width, float height, float x, float y, Table table){
        TextButton button = new TextButton(name, resourceManager.toolSkin, "default");
        table.add(button).width(width).height(height).padLeft(x).padTop(y);
        table.row();
    }

    public TextArea createTextArea(String name, float width, float height, float x, float y, boolean isDisabled, boolean newRow, Table table){
        TextArea textArea = new TextArea(name, resourceManager.toolSkin, "default");
        textArea.setDisabled(isDisabled);
        table.add(textArea).width(width).height(height).padLeft(x).padTop(y);
        if(newRow) table.row();
        return textArea;
    }

    public Slider createSlider( float width, float height, float x, float y, float min, float max, float stepSize, boolean isVertical, Table table){
        Slider slider = new Slider(min, max, stepSize, isVertical, resourceManager.toolSkin);
        table.add(slider).width(width).height(height).padLeft(x).padTop(y);
        table.row();
        return slider;
    }
}
