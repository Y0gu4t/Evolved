package com.loginov.simulator.Screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.loginov.simulator.Evolved;
import com.loginov.simulator.util.ResourceManager;
import com.loginov.simulator.util.SimulationParams;

public class SettingsScreen extends BaseScreen {
    private BaseScreen previousScreen;
    private ScreenViewport apiPort;
    private Stage stage;
    private Table settingsTable;
    private InputMultiplexer multiplexer;
    private TextArea humanCount;
    private TextArea humanAreas;
    private TextArea foodCount;
    private TextArea foodAdd;
    private TextArea foodAreas;
    private float stateTime;


    public SettingsScreen(Evolved proxy, BaseScreen previousScreen, ResourceManager resourceManager) {
        super(proxy, resourceManager);
        this.previousScreen = previousScreen;
        apiCam = new OrthographicCamera();
        apiPort = new ScreenViewport(apiCam);
        stage = new Stage(apiPort);
        settingsTable = createTable();
        handleHumanCountTextArea();
        handleHumanAreasTextArea();
        handleFoodCountTextArea();
        handleFoodAddTextArea();
        handleFoodAreasTextArea();
        handleBackButton();
        handleSaveButton();
        stage.addActor(settingsTable);
        multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(stage);
        Gdx.input.setInputProcessor(multiplexer);
    }

    public void handleHumanCountTextArea(){
        createTextArea("Human count", settingsTable.getWidth()/10, 25, 0, 25, true, false, settingsTable);
        humanCount = createTextArea(String.valueOf(SimulationParams.getHumanCount()), settingsTable.getWidth()/6, 25, 5, 25, false, true, settingsTable);
    }

    public void handleHumanAreasTextArea(){
        createTextArea("Human areas", settingsTable.getWidth()/10, 25, 0, 25, true, false, settingsTable);
        humanAreas = createTextArea(String.valueOf(SimulationParams.getHumanAreas()), settingsTable.getWidth()/6, 25, 5, 25, false, true, settingsTable);
    }

    public void handleFoodCountTextArea(){
        createTextArea("Food count", settingsTable.getWidth()/10, 25, 0, 25, true, false, settingsTable);
        foodCount = createTextArea(String.valueOf(SimulationParams.getFoodCount()), settingsTable.getWidth()/6, 25, 5, 25, false, true, settingsTable);
    }

    public void handleFoodAddTextArea(){
        createTextArea("Food add", settingsTable.getWidth()/10, 25, 0, 25, true, false, settingsTable);
        foodAdd = createTextArea(String.valueOf(SimulationParams.getFoodAdd()), settingsTable.getWidth()/6, 25, 5, 25, false, true, settingsTable);
    }

    public void handleFoodAreasTextArea(){
        createTextArea("Food areas", settingsTable.getWidth()/10, 25, 0, 25, true, false, settingsTable);
        foodAreas = createTextArea(String.valueOf(SimulationParams.getFoodAreas()), settingsTable.getWidth()/6, 25, 5, 25, false, true, settingsTable);
    }

    public void handleBackButton(){
        createButton("Back", settingsTable.getWidth()/10, 50, 0, 50, settingsTable);
        int id = settingsTable.getCells().size;
        Actor backButton = settingsTable.getCells().get(id-1).getActor();
        backButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                proxy.setScreen(new MenuScreen(proxy, resourceManager));
            }
        });
    }

    public void handleSaveButton(){
        createButton("Save", settingsTable.getWidth()/10, 50, 0, 50, settingsTable);
        int id = settingsTable.getCells().size;
        Actor backButton = settingsTable.getCells().get(id-1).getActor();
        backButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                SimulationParams.setHumanCount(Integer.parseInt(humanCount.getText()));
                SimulationParams.setHumanAreas(Integer.parseInt(humanAreas.getText()));
                SimulationParams.setFoodCount(Integer.parseInt(foodCount.getText()));
                SimulationParams.setFoodAdd(Integer.parseInt(foodAdd.getText()));
                SimulationParams.setFoodAreas(Integer.parseInt(foodAreas.getText()));
            }
        });
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        stateTime += Gdx.graphics.getDeltaTime();
        Gdx.gl.glClearColor(0.8f,0.8f,0.8f,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        proxy.getBatch().setProjectionMatrix(apiCam.combined);
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
