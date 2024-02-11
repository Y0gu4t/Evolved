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
    private final float textFieldHeight = 50f;
    private final float textAreaHeight = 30f;
    private final float textSpace = 25f;
    private TextArea humanCount;
    private TextArea collectorCount;
    private TextArea warriorCount;
    private TextArea thiefCount;
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
        //handleHumanCountTextArea();
        handleCollectorCountTextArea();
        handleWarriorCountTextArea();
        handleThiefCountTextArea();
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
        createTextField("Human count", settingsTable.getWidth()/10, textFieldHeight,
                textSpace, textSpace, true, settingsTable);
        humanCount = createTextArea(String.valueOf(SimulationParams.getHumanCount()), settingsTable.getWidth()/6, 40,
                textSpace, textSpace, false, settingsTable);
    }

    public void handleCollectorCountTextArea(){
        createTextField("Collector count", settingsTable.getWidth()/10, textFieldHeight,
                textSpace, textSpace, false, settingsTable);
        collectorCount = createTextArea(String.valueOf(SimulationParams.getCollectorCount()), settingsTable.getWidth()/10, textAreaHeight,
                textSpace, textSpace, true, settingsTable);
    }

    public void handleWarriorCountTextArea(){
        createTextField("Warrior count", settingsTable.getWidth()/10, textFieldHeight,
                textSpace, textSpace, false, settingsTable);
        warriorCount = createTextArea(String.valueOf(SimulationParams.getWarriorCount()), settingsTable.getWidth()/10, textAreaHeight,
                textSpace, textSpace, true, settingsTable);
    }

    public void handleThiefCountTextArea(){
        createTextField("Thief count", settingsTable.getWidth()/10, textFieldHeight,
                textSpace, textSpace, false, settingsTable);
        thiefCount = createTextArea(String.valueOf(SimulationParams.getThiefCount()), settingsTable.getWidth()/10, textAreaHeight,
                textSpace, textSpace, true, settingsTable);
    }

    public void handleHumanAreasTextArea(){
        createTextField("Human areas", settingsTable.getWidth()/10, textFieldHeight,
                textSpace, textSpace, false, settingsTable);
        humanAreas = createTextArea(String.valueOf(SimulationParams.getHumanAreas()), settingsTable.getWidth()/10, textAreaHeight,
                textSpace, textSpace, true, settingsTable);
    }

    public void handleFoodCountTextArea(){
        createTextField("Food count", settingsTable.getWidth()/10, textFieldHeight,
                textSpace, textSpace, false, settingsTable);
        foodCount = createTextArea(String.valueOf(SimulationParams.getFoodCount()), settingsTable.getWidth()/10, textAreaHeight,
                textSpace, textSpace, true, settingsTable);
    }

    public void handleFoodAddTextArea(){
        createTextField("Food add", settingsTable.getWidth()/10, textFieldHeight,
                textSpace, textSpace, false, settingsTable);
        foodAdd = createTextArea(String.valueOf(SimulationParams.getFoodAdd()), settingsTable.getWidth()/10, textAreaHeight,
                textSpace, textSpace, true, settingsTable);
    }

    public void handleFoodAreasTextArea(){
        createTextField("Food areas", settingsTable.getWidth()/10, textFieldHeight,
                textSpace, textSpace, false, settingsTable);
        foodAreas = createTextArea(String.valueOf(SimulationParams.getFoodAreas()), settingsTable.getWidth()/10, textAreaHeight,
                textSpace, textSpace, true, settingsTable);
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
                //SimulationParams.setHumanCount(Integer.parseInt(humanCount.getText()));
                SimulationParams.setCollectorCount(Integer.parseInt(collectorCount.getText()));
                SimulationParams.setWarriorCount(Integer.parseInt(warriorCount.getText()));
                SimulationParams.setThiefCount(Integer.parseInt(thiefCount.getText()));
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
        Gdx.gl.glClearColor(250/255f, 235/255f, 215/255f, 1);
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
