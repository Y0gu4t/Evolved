package com.loginov.simulator.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.loginov.simulator.Evolved;
import com.loginov.simulator.util.ResourceManager;
import com.loginov.simulator.util.SimulationParams;

public class SettingsScreen extends BaseScreen {
    private BaseScreen previousScreen;
    private final ScreenViewport apiPort;
    private final Stage stage;
    private final Table settingsTable;
    private final float textFieldHeight = 60f;
    private final float textAreaHeight = 40f;
    private final float textSpace = 40f;
    private TextArea collectorCountClan1;
    private TextArea collectorCountClan2;
    private TextArea warriorCountClan1;
    private TextArea warriorCountClan2;
    private TextArea thiefCountClan1;
    private TextArea thiefCountClan2;
    private TextArea foodCount;
    private TextArea foodAdd;


    public SettingsScreen(Evolved proxy, BaseScreen previousScreen, ResourceManager resourceManager) {
        super(proxy, resourceManager);
        this.previousScreen = previousScreen;
        apiCam = new OrthographicCamera();
        apiPort = new ScreenViewport(apiCam);
        stage = new Stage(apiPort);
        settingsTable = createTable();
        handleClanArea();
        handleFoodCountTextArea();
        handleFoodAddTextArea();
        handleBackButton();
        handleSaveButton();
        stage.addActor(settingsTable);
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(stage);
        Gdx.input.setInputProcessor(multiplexer);
    }

    public void handleClanArea() {
        TextField textField = createTextField("", settingsTable.getWidth() / 8, textFieldHeight,
                settingsTable.getWidth() / 10, textSpace, false, settingsTable);
        textField.setVisible(false);
        createTextField("Clan 1", settingsTable.getWidth() / 8, textFieldHeight,
                settingsTable.getWidth() / 10, textSpace, false, settingsTable);
        createTextField("Clan 2", settingsTable.getWidth() / 8, textFieldHeight,
                settingsTable.getWidth() / 10, textSpace, true, settingsTable);

        createTextField("Collectors", settingsTable.getWidth() / 8, textFieldHeight,
                settingsTable.getWidth() / 10, textSpace, false, settingsTable);
        collectorCountClan1 = createTextArea(String.valueOf(SimulationParams.getClanList().get(0).getCollectorCount()), settingsTable.getWidth() / 8, textAreaHeight,
                settingsTable.getWidth() / 10, textSpace, false, settingsTable);
        collectorCountClan2 = createTextArea(String.valueOf(SimulationParams.getClanList().get(1).getCollectorCount()), settingsTable.getWidth() / 8, textAreaHeight,
                settingsTable.getWidth() / 10, textSpace, true, settingsTable);

        createTextField("Thieves", settingsTable.getWidth() / 8, textFieldHeight,
                settingsTable.getWidth() / 10, textSpace, false, settingsTable);
        thiefCountClan1 = createTextArea(String.valueOf(SimulationParams.getClanList().get(0).getThiefCount()), settingsTable.getWidth() / 8, textAreaHeight,
                settingsTable.getWidth() / 10, textSpace, false, settingsTable);
        thiefCountClan2 = createTextArea(String.valueOf(SimulationParams.getClanList().get(1).getThiefCount()), settingsTable.getWidth() / 8, textAreaHeight,
                settingsTable.getWidth() / 10, textSpace, true, settingsTable);

        createTextField("Warriors", settingsTable.getWidth() / 8, textFieldHeight,
                settingsTable.getWidth() / 10, textSpace, false, settingsTable);
        warriorCountClan1 = createTextArea(String.valueOf(SimulationParams.getClanList().get(0).getWarriorCount()), settingsTable.getWidth() / 8, textAreaHeight,
                settingsTable.getWidth() / 10, textSpace, false, settingsTable);
        warriorCountClan2 = createTextArea(String.valueOf(SimulationParams.getClanList().get(1).getWarriorCount()), settingsTable.getWidth() / 8, textAreaHeight,
                settingsTable.getWidth() / 10, textSpace, true, settingsTable);
    }

    public void handleFoodCountTextArea() {
        createTextField("Food count", settingsTable.getWidth() / 8, textFieldHeight,
                settingsTable.getWidth() / 10, textSpace, false, settingsTable);
        foodCount = createTextArea(String.valueOf(SimulationParams.getFoodCount()), settingsTable.getWidth() / 8, textAreaHeight,
                settingsTable.getWidth() / 10, textSpace, true, settingsTable);
    }

    public void handleFoodAddTextArea() {
        createTextField("Food add", settingsTable.getWidth() / 8, textFieldHeight,
                settingsTable.getWidth() / 10, textSpace, false, settingsTable);
        foodAdd = createTextArea(String.valueOf(SimulationParams.getFoodAdd()), settingsTable.getWidth() / 8, textAreaHeight,
                settingsTable.getWidth() / 10, textSpace, true, settingsTable);
    }

    public void handleBackButton() {
        Actor backButton = createButton("Back", settingsTable.getWidth() / 8, textFieldHeight, 0, textSpace, settingsTable);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                proxy.setScreen(new MenuScreen(proxy, resourceManager));
            }
        });
    }

    public void handleSaveButton() {
        Actor saveButton = createButton("Save", settingsTable.getWidth() / 8, textFieldHeight, 0, textSpace, settingsTable);
        saveButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                try {
                    SimulationParams.getClanList().get(0).setCollectorCount(Integer.parseInt(collectorCountClan1.getText()));
                } catch (NumberFormatException e) {
                    System.out.println("Clan 1, collector: " + e);
                }
                try {
                    SimulationParams.getClanList().get(0).setThiefCount(Integer.parseInt(thiefCountClan1.getText()));
                } catch (NumberFormatException e) {
                    System.out.println("Clan 1, thief: " + e);
                }
                try {
                    SimulationParams.getClanList().get(0).setWarriorCount(Integer.parseInt(warriorCountClan1.getText()));
                } catch (NumberFormatException e) {
                    System.out.println("Clan 1, warrior: " + e);
                }

                try {
                    SimulationParams.getClanList().get(1).setCollectorCount(Integer.parseInt(collectorCountClan2.getText()));
                } catch (NumberFormatException e) {
                    System.out.println("Clan 2, collector: " + e);
                }
                try {
                    SimulationParams.getClanList().get(1).setThiefCount(Integer.parseInt(thiefCountClan2.getText()));
                } catch (NumberFormatException e) {
                    System.out.println("Clan 2, thief: " + e);
                }
                try {
                    SimulationParams.getClanList().get(1).setWarriorCount(Integer.parseInt(warriorCountClan2.getText()));
                } catch (NumberFormatException e) {
                    System.out.println("Clan 2, warrior: " + e);
                }

                try {
                    SimulationParams.setFoodCount(Integer.parseInt(foodCount.getText()));
                } catch (NumberFormatException e) {
                    System.out.println("Food count: " + e);
                }
                try {
                    SimulationParams.setFoodAdd(Integer.parseInt(foodAdd.getText()));
                } catch (NumberFormatException e) {
                    System.out.println("Food add: " + e);
                }
            }
        });
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(250 / 255f, 235 / 255f, 215 / 255f, 1);
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
    public void dispose() {
        super.dispose();
        Gdx.input.setInputProcessor(null);
        stage.dispose();
    }
}
