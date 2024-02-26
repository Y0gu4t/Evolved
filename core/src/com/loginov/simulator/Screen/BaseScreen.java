package com.loginov.simulator.Screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.loginov.simulator.Evolved;
import com.loginov.simulator.util.ResourceManager;

import javax.swing.GroupLayout;


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

    public Actor createButton(String text, float width, float height, float x, float y, Table table){
        TextButton button = new TextButton(text, resourceManager.toolSkin, "default");
        table.add(button).width(width).height(height).padLeft(x).padTop(y);
        table.row();
        return  button;
    }

    public TextArea createTextArea(String text, float width, float height, float x, float y, boolean newRow, Table table){
        TextArea textArea = new TextArea(text, resourceManager.toolSkin, "default");
        table.add(textArea).width(width).height(height).padRight(x).padBottom(y);
        if(newRow) table.row();
        return textArea;
    }

    public TextArea createTextArea(String text, float width, float height, float x, float y, boolean newRow, Table table, String styleName){
        TextArea textArea = new TextArea(text, resourceManager.toolSkin, styleName);
        table.add(textArea).width(width).height(height).padRight(x).padBottom(y);
        if(newRow) table.row();
        return textArea;
    }

    public TextField createTextField(String text, float width, float height, float x, float y, boolean newRow, Table table){
        TextField textField = new TextField(text, resourceManager.toolSkin, "default");
        textField.setDisabled(true);
        textField.setAlignment(Align.center);
        table.add(textField).width(width).height(height).padRight(x).padBottom(y);
        if(newRow) table.row();
        return textField;
    }

    public Slider createSlider( float width, float height, float x, float y, float min, float max, float stepSize, boolean isVertical, Table table){
        Slider slider = new Slider(min, max, stepSize, isVertical, resourceManager.toolSkin);
        table.add(slider).width(width).height(height).padLeft(x).padTop(y);
        table.row();
        return slider;
    }
}
