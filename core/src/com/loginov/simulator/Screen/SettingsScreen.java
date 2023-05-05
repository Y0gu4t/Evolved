package com.loginov.simulator.Screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.loginov.simulator.Evolved;
import com.loginov.simulator.util.ResourceManager;
import com.loginov.simulator.util.SimulationParams;


public class SettingsScreen extends BaseScreen {

    private BaseScreen previousScreen;
    private ScreenViewport apiPort;
    private Stage stage;
    private Table classTable;
    private Table settingsTable;
    private InputMultiplexer multiplexer;
    private TextField humanCount;
    private TextField humanAreas;
    private TextField foodCount;
    private TextField foodAdd;
    private TextField foodAreas;
    private float stateTime;


    public SettingsScreen(Evolved proxy, BaseScreen previousScreen, ResourceManager resourceManager) {
        super(proxy, resourceManager);
        this.previousScreen = previousScreen;
        apiCam = new OrthographicCamera();
        apiCam.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        apiPort = new ScreenViewport(apiCam);
        stage = new Stage(apiPort);
        classTable = createTable();
        settingsTable = createSettingsTable(classTable.getWidth());
        handleHumanClassTextArea();
        handleFoodClassTextArea();
        handleSimulationClassTextArea();
        handleBackButton();
        handleSaveButton();

        handleHumanSettings();
        handleFoodSettings();
        settingsTable.clear();
        stage.addActor(classTable);
        stage.addActor(settingsTable);
        multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(stage);
        Gdx.input.setInputProcessor(multiplexer);
    }

    @Override
    protected Table createTable() {
        Table table = new Table();
        table.setBounds(0,0, (float) Gdx.graphics.getWidth()/6, (float) Gdx.graphics.getHeight());
        return table;
    }

    private Table createSettingsTable(float x) {
        Table table = new Table();
        table.setBounds(x, 0, (float) Gdx.graphics.getWidth() * 5 / 6, (float) Gdx.graphics.getHeight());
        return table;
    }

    private void handleHumanClassTextArea() {
        createButton("Human", classTable.getWidth()/1.5f, 50, 0, 15, classTable);
        Actor humanActor = classTable.getCells().get(0).getActor();
        humanActor.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                handleHumanSettings();
            }
        });
    }

    private void handleFoodClassTextArea() {
        createButton("Food", classTable.getWidth()/1.5f, 50, 0, 15, classTable);
        Actor foodActor = classTable.getCells().get(1).getActor();
        foodActor.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                handleFoodSettings();
            }
        });
    }

    private void handleSimulationClassTextArea() {
        createButton("Simulation", classTable.getWidth()/1.5f, 50, 0, 15, classTable);
        Actor simulationActor = classTable.getCells().get(2).getActor();
    }

    private void handleHumanSettings() {
        settingsTable.clear();
        TextField humanCountInfoField = createTextField("Count", settingsTable.getWidth()/6, 50, 0, 15, true, false, settingsTable);
        humanCountInfoField.setAlignment(Align.left);
        humanCount = createTextField(String.valueOf(SimulationParams.getHumanCount()), settingsTable.getWidth()/4, 50, 5, 15, false, true, settingsTable);
        TextField humanAreasInfoField = createTextField("Areas", settingsTable.getWidth()/6, 50, 0, 15, true, false, settingsTable);
        humanAreasInfoField.setAlignment(Align.left);
        humanAreas = createTextField(String.valueOf(SimulationParams.getHumanAreas()), settingsTable.getWidth()/4, 50, 5, 15, false, true, settingsTable);
    }

    private void handleFoodSettings() {
        settingsTable.clear();
        TextField foodCountInfoField = createTextField("Count", settingsTable.getWidth()/6, 50, 0, 15, true, false, settingsTable);
        foodCountInfoField.setAlignment(Align.left);
        foodCount = createTextField(String.valueOf(SimulationParams.getFoodCount()), settingsTable.getWidth()/4, 50, 5, 15, false, true, settingsTable);
        TextField foodAddInfoField = createTextField("Add count", settingsTable.getWidth()/6, 50, 0, 15, true, false, settingsTable);
        foodAddInfoField.setAlignment(Align.left);
        foodAdd = createTextField(String.valueOf(SimulationParams.getFoodAdd()), settingsTable.getWidth()/4, 50, 5, 15, false, true, settingsTable);
        TextField foodAreasInfoField = createTextField("Areas", settingsTable.getWidth()/6, 50, 0, 15, true, false, settingsTable);
        foodAreasInfoField.setAlignment(Align.left);
        foodAreas = createTextField(String.valueOf(SimulationParams.getFoodAreas()), settingsTable.getWidth()/4, 50, 5, 15, false, true, settingsTable);
    }

    public void handleBackButton(){
        createButton("Back", classTable.getWidth()/1.5f, 50, 0, 50, classTable);
        int id = classTable.getCells().size;
        Actor backButton = classTable.getCells().get(id-1).getActor();
        backButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                proxy.setScreen(new MenuScreen(proxy, resourceManager));
            }
        });
    }

    public void handleSaveButton(){
        createButton("Save", classTable.getWidth()/1.5f, 50, 0, 15, classTable);
        int id = classTable.getCells().size;
        Actor backButton = classTable.getCells().get(id-1).getActor();
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
