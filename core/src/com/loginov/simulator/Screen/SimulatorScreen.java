package com.loginov.simulator.Screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.loginov.simulator.Actor.Human;
import com.loginov.simulator.Enums.ApplicationState;
import com.loginov.simulator.Enums.HumanState;
import com.loginov.simulator.Enums.SimulationState;
import com.loginov.simulator.Evolved;
import com.loginov.simulator.util.FoodGenerator;
import com.loginov.simulator.util.HumanGenerator;
import com.loginov.simulator.util.ResourceManager;
import com.loginov.simulator.util.SimulationParams;

import java.util.ArrayList;

public class SimulatorScreen extends BaseScreen {
    private BaseScreen previousScreen;
    private InputMultiplexer multiplexer;
    private ApplicationState applicationState;
    private SimulationState simulationState;
    private Viewport apiPort;
    private Table infoTable;
    private Group group;
    private FoodGenerator foodGenerator;
    private HumanGenerator humanGenerator;
    private  ShapeRenderer shapeRenderer;
    private float simulatorTime = 0f;
    private int daysPast = 0;
    public float generateTime = 0f;
    public static float simulationSpeed = 0.0f;

    public SimulatorScreen(Evolved proxy, BaseScreen previousScreen, ResourceManager resourceManager){
        super(proxy, resourceManager);
        this.previousScreen = previousScreen;
        // set running state
        setApplicationState(ApplicationState.RUNNING);
        simulationState = SimulationState.DAY;
        // set camera
        apiCam = new OrthographicCamera();
        apiCam.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        apiPort = new ScreenViewport(apiCam);
        // set input and stage
        multiplexer = new InputMultiplexer();
        stage = new Stage(apiPort);
        //
        infoTable = new Table();
        group = new Group();
        shapeRenderer = new ShapeRenderer();

        // put infoTable on the screen
        handleInfoTable();
        // create buttons
        handleTextFieldSimulationInfo();
        handlePauseButton();
        handleBackButton();
        handleSpeedSlider();
        // set group params
        handleGroup();
        // add actors in stage
        stage.addActor(infoTable);
        stage.addActor(group);
        // generate simulation objects
        humanGenerator = new HumanGenerator(group);
        foodGenerator = new FoodGenerator(group);
        foodGenerator.generate(SimulationParams.getFoodCount(), resourceManager);
        humanGenerator.generate(SimulationParams.getHumanCount(), resourceManager);
        // set input
        multiplexer.addProcessor(stage);
        Gdx.input.setInputProcessor(multiplexer);
    }

    /**
     * update human's satiety
     * @// FIXME: 27.04.2023 need to rework or replace
     */
    private void satietyUpdate(float deltaSatiety){

        ArrayList<Human> toDelete = new ArrayList<>();

        for(Human human : humanGenerator.getHumans()){
            human.setSatiety(deltaSatiety * human.getMetabolism());
            if(human.getSatiety()<=0 || human.getAge() >= Human.MAX_AGES_OF_LIFE){
                toDelete.add(human);
            }
        }

        for(Human human : toDelete){
            humanGenerator.remove(human);
        }

    }

    // update when simulation paused
    public void updatePaused(float delta){
    }

    /**
     * update simulation variables
     * @// FIXME: 27.04.2023 need to rework
     */
    public void updateRunning(float delta){
        simulatorTime += delta * simulationSpeed;
        generateTime += delta * simulationSpeed;
        if (generateTime >= simulationState.getDuration()) {
            switch (simulationState) {
                case NIGHT:
                    daysPast++;
                    for (Human h : humanGenerator.getHumans()) {
                        h.updateAge();
                        h.updateAgesAfterChildbirth();
                        h.setState(HumanState.FIND_FOOD);
                    }
                    break;
                case DAY:
                    break;
            }
            foodGenerator.generate(SimulationParams.getFoodAdd(), resourceManager);
            satietyUpdate(SimulationParams.getDeltaSatiety());
            changeSimulationState();
        }

        for (Human h: humanGenerator.getHumans()) {
            h.operate(foodGenerator, humanGenerator, simulationState);
        }
        humanGenerator.addChildren();
    }

    private void changeSimulationState(){
        generateTime = 0;
        switch (simulationState){
            case DAY:
                simulationState = SimulationState.NIGHT;
                break;
            case NIGHT:
                simulationState = SimulationState.DAY;
                break;
        }
    }

    /**
     * @// FIXME: 27.04.2023 add threads, maybe need to replace
     */
    public void draw(float delta){
        Gdx.gl.glClearColor(1f, 1f, 1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        proxy.getBatch().setProjectionMatrix(apiCam.combined);

        // show simulation areas
        debugAreas(humanGenerator.getAreas());
       // debugAreas(foodGenerator.getAreas());

        proxy.getBatch().begin();

        for (int i = 0; i < humanGenerator.getHumans().size(); i++) {
            humanGenerator.getHumans().get(i).draw(proxy.getBatch());
        }
        for (int i = 0; i < foodGenerator.getFood().size(); i++) {
            foodGenerator.getFood().get(i).draw(proxy.getBatch());
        }

        proxy.getBatch().end();

        stage.act(delta);
        stage.draw();
    }

    /**
     * update simulation
     * @// FIXME: 27.04.2023 need to rework
     */
    public void update(float delta){
        switch(applicationState){
            case RUNNING:
                updateRunning(delta);
                break;
            case PAUSED:
                updatePaused(delta);
                break;
        }
    }

    /**
     * set new simulation's state
     */
    public void setApplicationState(ApplicationState state){
        switch (state) {
            case RUNNING:
                applicationState = ApplicationState.RUNNING;
                break;
            case PAUSED:
                if(applicationState == ApplicationState.PAUSED){
                    applicationState = ApplicationState.RUNNING;
                }
                else if(applicationState == ApplicationState.RUNNING){
                    applicationState = ApplicationState.PAUSED;
                }
                break;
        }
    }

    /**
     * set table's params
     */
    private void handleInfoTable(){
        infoTable.setWidth(stage.getWidth()/8);
        infoTable.setHeight(stage.getHeight());
        infoTable.padLeft(10).padTop(10);
        infoTable.align(Align.topLeft);
    }

    /**
     * create simulation's information field and add info updater
     */
    private void handleTextFieldSimulationInfo(){
        final TextArea textArea = createTextArea("", infoTable.getWidth(), infoTable.getHeight()/4, 0, 50, true, true, infoTable);
        Actor thisTextArea = infoTable.getCells().get(0).getActor();
        thisTextArea.addAction(new Action() {
            @SuppressWarnings("DefaultLocale")
            @Override
            public boolean act(float delta) {
                float avgSatiety = 0f;
                float avgAge = 0f;
                float avgMetabolism = 0f;
                for(Human h : humanGenerator.getHumans()){
                    avgSatiety += h.getSatiety();
                    avgAge += h.getAge();
                    avgMetabolism += h.getMetabolism();
                }
                avgSatiety /= humanGenerator.getHumans().size();
                avgAge /= humanGenerator.getHumans().size();
                avgMetabolism /= humanGenerator.getHumans().size();

                textArea.setText(String.format("Simulation time: %d", (int) simulatorTime) +
                                String.format("\nTimes of day: %s", simulationState) +
                                String.format("\nDays past: %d", daysPast) +
                                String.format("\nPeople in simulation: %d", humanGenerator.getHumans().size()) +
                                String.format("\nFood in simulation: %d", foodGenerator.getFood().size()) +
                                String.format("\nAverage satiety: %.2f", avgSatiety) +
                                String.format("\nAverage age: %.2f", avgAge) +
                                String.format("\nAverage meta: %.2f", avgMetabolism));

                // false - update info on screen, true - not update
                return false;
            }
        });
    }

    /**
     *  create back to menu button and set new screen listener
     */
    private void handleBackButton(){
        createButton("Back", infoTable.getWidth(), 50, 0, 50, infoTable);
        Actor thisButton = infoTable.getCells().get(2).getActor();
        thisButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                proxy.setScreen(new MenuScreen(proxy, resourceManager));
            }
        });
    }

    /**
     *  create pause button and simulation pause listener
     */
    private void handlePauseButton(){
        createButton("Pause", infoTable.getWidth(), 50, 0 , 50, infoTable);
        Actor thisButton = infoTable.getCells().get(1).getActor();
        thisButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                setApplicationState(ApplicationState.PAUSED);
            }
        });
    }

    private void handleSpeedSlider(){
        final Slider speedSlider = createSlider(infoTable.getWidth(), 10, 0, 50, 0.0f, 5.0f, 0.5f, false, infoTable);
        speedSlider.setValue(simulationSpeed);
        Actor thisSlider = infoTable.getCells().get(3).getActor();
        thisSlider.addAction(new Action() {
            @Override
            public boolean act(float delta) {
                simulationSpeed = speedSlider.getValue();
                return false;
            }
        });
    }

    private void handleGroup(){
        group.setBounds(infoTable.getWidth()+ 20, 20, stage.getWidth() - infoTable.getWidth() - 30, stage.getHeight() - 30);
        group.setDebug(false);
    }

    private void debugAreas(ArrayList<Circle> areas){
        proxy.getShapeRenderer().setProjectionMatrix(apiCam.combined);
        proxy.getShapeRenderer().begin(ShapeRenderer.ShapeType.Filled);

        proxy.getShapeRenderer().setColor(Color.CYAN);
        for (Circle area: areas) {
            proxy.getShapeRenderer().circle(area.x, area.y, area.radius);
        }

        proxy.getShapeRenderer().end();
    }

    @Override
    public void show() {

    }


    @Override
    public void render(float delta) {
        update(delta);
        draw(delta);
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
        simulationSpeed = 1f;
        stage.dispose();
    }

}
