package com.loginov.simulator.Screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
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
import com.loginov.simulator.Actor.Food;
import com.loginov.simulator.Actor.Human;
import com.loginov.simulator.Evolved;
import com.loginov.simulator.util.ResourceManager;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Random;

public class SimulatorScreen extends BaseScreen {

    public enum SimulationState{
        SIMULATION_RUNNING,
        SIMULATION_PAUSED
    }

    private BaseScreen previousScreen;
    private InputMultiplexer multiplexer;
    private SimulationState simulationState;
    private Viewport apiPort;
    private final Random random;
    private Table infoTable;
    private Group group;
    private ArrayList<Human> humans;
    private ArrayList<Food> foods;
    public int humanCount = 20;
    public int foodCount = 30;
    public int foodAdd = 10;
    private float simulatorTime = 0f;
    private static final float METABOLISM = (float) (Math.random() + 0.5f);
    private ArrayList<Rectangle2D.Float> humanGenerationAreas;
    private ArrayList<Rectangle2D.Float> foodGenerationAreas;
    public float generatePeriod = 3f;
    public float generateTime = 0f;
    public float deltaSatiety = -10f;
    public static float simulationSpeed = 1f;

    public SimulatorScreen(Evolved proxy, BaseScreen previousScreen, ResourceManager resourceManager){
        super(proxy, resourceManager);
        this.previousScreen = previousScreen;
        // set running state
        setSimulationState(SimulationState.SIMULATION_RUNNING);
        // set camera
        apiCam = new OrthographicCamera();
        apiCam.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        apiPort = new ScreenViewport(apiCam);
        /*
               need to replace
         */
        random = new Random();
        // set input and stage
        multiplexer = new InputMultiplexer();
        stage = new Stage(apiPort);
        //
        infoTable = new Table();
        group = new Group();
        /*
               need to replace
          */
        // create object list
        humans = new ArrayList<>();
        foods = new ArrayList<>();
        humanGenerationAreas = new ArrayList<>();
        foodGenerationAreas = new ArrayList<>();
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
        addFoodGenerationAreas();
        generateHumans(humanCount);
        generateFood(foodCount);
        // set input
        multiplexer.addProcessor(stage);
        Gdx.input.setInputProcessor(multiplexer);
    }

    /**
     * generate humans in simulation field
     * @// FIXME: 27.04.2023 need to replace (GeneratorManager), need to rework
     */
    private void generateHumans(int humanToGenerate){
        addHumanGenerationAreas();
        for(int i=0; i < humanToGenerate; i++){
            int rand = random.nextInt(humanGenerationAreas.size());
            Rectangle2D area = humanGenerationAreas.get(rand);
            float x = (float)(area.getX() + random.nextFloat()*(area.getWidth() - Human.getHumanWidth()) + Human.getHumanWidth()/2);
            float y = (float)(area.getY() + random.nextFloat()*(area.getHeight() - Human.getHumanHeight()) + Human.getHumanHeight()/2);
            humans.add(new Human(resourceManager.humanTexture, x, y, METABOLISM));
        }
    }

    /**
     * generate food in simulation field
     * @// FIXME: 27.04.2023 need to replace (GeneratorManager), need to rework
     */
    private void generateFood(int foodToGenerate){
        for(int i=0; i < foodToGenerate; i++){
            int rand = random.nextInt(foodGenerationAreas.size());
            Rectangle2D area = foodGenerationAreas.get(rand);
            float x = (float) (area.getX() + random.nextFloat()*(area.getWidth() - Food.getFoodWidth()) + Food.getFoodWidth()/2);
            float y = (float) (area.getY() + random.nextFloat()*(area.getHeight() - Food.getFoodHeight()) + Food.getFoodHeight()/2);
            foods.add(new Food(resourceManager.foodTexture, x, y));
        }
    }

    /**
     * add human's home
     * @// FIXME: 27.04.2023 need to replace (GeneratorManager)
     */
    private void addHumanGenerationAreas(){
        Rectangle2D.Float r1 = new Rectangle2D.Float(group.getX() + 10, group.getY() + 10, group.getWidth()/16, group.getWidth()/8);
        Rectangle2D.Float r2 = new Rectangle2D.Float(group.getX() + 10, group.getHeight() - group.getWidth()/16, group.getWidth()/8, group.getWidth()/8);
        Rectangle2D.Float r3 = new Rectangle2D.Float(group.getWidth(), group.getY() + 10, group.getWidth()/8, group.getWidth()/8);
        Rectangle2D.Float r4 = new Rectangle2D.Float(group.getWidth(), group.getHeight() - group.getWidth()/8 - 10, group.getWidth()/8, group.getWidth()/8);
        humanGenerationAreas.add(r1);
        humanGenerationAreas.add(r2);
        humanGenerationAreas.add(r3);
        humanGenerationAreas.add(r4);
    }

    /**
     * add food's areas
     * @// FIXME: 27.04.2023 need to replace (GeneratorManager)
     */
    private void addFoodGenerationAreas(){
        System.out.println("Group: " + group.getHeight());
        Rectangle2D.Float r1 = new Rectangle2D.Float(group.getWidth()/2 + group.getWidth()/16, group.getHeight()/2 - group.getWidth()/16, group.getWidth()/8, group.getWidth()/8);
        Rectangle2D.Float r2 = new Rectangle2D.Float(group.getX() + 10, group.getHeight()/2 - group.getWidth()/16, group.getWidth()/8, group.getWidth()/8);
        Rectangle2D.Float r3 = new Rectangle2D.Float(group.getWidth()/2 + group.getWidth()/16, group.getHeight() - group.getWidth()/8, group.getWidth()/8, group.getWidth()/8);
        Rectangle2D.Float r4 = new Rectangle2D.Float(group.getWidth()/2 + group.getWidth()/16, group.getY() + 10, group.getWidth()/8, group.getWidth()/8);
        Rectangle2D.Float r5 = new Rectangle2D.Float(group.getWidth(), group.getHeight()/2 - group.getWidth()/16, group.getWidth()/8, group.getWidth()/8);
        foodGenerationAreas.add(r1);
        foodGenerationAreas.add(r2);
        foodGenerationAreas.add(r3);
        foodGenerationAreas.add(r4);
        foodGenerationAreas.add(r5);
    }

    /**
     * update human's satiety
     * @// FIXME: 27.04.2023 need to rework or replace
     */
    private void satietyUpdate(float deltaSatiety){

        ArrayList<Human> toDelete = new ArrayList<>();

        for(Human human : humans){
            human.setSatiety(deltaSatiety * human.getMetabolism());
            if(human.getSatiety()<=0 || human.getAge() >= Human.MAX_AGES_OF_LIFE){
                toDelete.add(human);
            }
        }

        for(Human human : toDelete){
            humans.remove(human);
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
        if (generateTime >= generatePeriod) {
            generateTime = 0;
            generateFood(foodAdd);
            foodCount += foodAdd;
            satietyUpdate(deltaSatiety);
            ArrayList<Human> humansTmp = new ArrayList<>();
            for (Human h : humans) {
                h.updateAge();
                h.updateAgesAfterChildbirth();
                if (h.giveBirthOpportunity()) {
                    humansTmp.add(h.giveBirth(resourceManager.humanTexture));
                }
            }
            humans.addAll(humansTmp);
            humansTmp.clear();
        }

        for (Human h : humans) {
            if (!foods.contains(h.getFoodToEat())) {
                h.findFood(foods);
            }
            h.update();
            h.isEaten(this);
        }
    }

    /**
     * @// FIXME: 27.04.2023 add threads, maybe need to replace
     */
    public void draw(float delta){
        Gdx.gl.glClearColor(1f, 1f, 1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        proxy.getBatch().setProjectionMatrix(apiCam.combined);

        proxy.getBatch().begin();
        for (int i = 0; i < humans.size(); i++) {
            humans.get(i).draw(proxy.getBatch());
        }
        for (int i = 0; i < foods.size(); i++) {
            foods.get(i).draw(proxy.getBatch());
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
        switch(simulationState){
            case SIMULATION_RUNNING:
                updateRunning(delta);
                break;
            case SIMULATION_PAUSED:
                updatePaused(delta);
                break;
        }
    }

    /**
     * set new simulation's state
     */
    public void setSimulationState(SimulationState state){
        switch (state) {
            case SIMULATION_RUNNING:
                simulationState = SimulationState.SIMULATION_RUNNING;
                break;
            case SIMULATION_PAUSED:
                if(simulationState == SimulationState.SIMULATION_PAUSED){
                    simulationState = SimulationState.SIMULATION_RUNNING;
                }
                else if(simulationState == SimulationState.SIMULATION_RUNNING){
                    simulationState = SimulationState.SIMULATION_PAUSED;
                }
                break;
        }
    }

    /**
     * @// FIXME: 27.04.2023 need to replace
     */
    public ArrayList<Food> getFoods() {
        return foods;
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
        final TextArea textArea = createTextArea(infoTable.getWidth(), infoTable.getHeight()/4, 0, 50, true, infoTable);
        Actor thisTextArea = infoTable.getCells().get(0).getActor();
        thisTextArea.addAction(new Action() {
            @Override
            public boolean act(float delta) {
                float avgSatiety = 0f;
                float avgAge = 0f;
                float avgMetabolism = 0f;
                for(Human h : humans){
                    avgSatiety += h.getSatiety();
                    avgAge += h.getAge();
                    avgMetabolism += h.getMetabolism();
                }
                avgSatiety /= humans.size();
                avgAge /= humans.size();
                avgMetabolism /= humans.size();

                textArea.setText("Simulation time: " + (int) simulatorTime +
                                "\nPeople in simulation: " + humans.size() +
                                "\nFood in simulation: " + foods.size() +
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
                setSimulationState(SimulationState.SIMULATION_PAUSED);
            }
        });
    }

    private void handleSpeedSlider(){
        final Slider speedSlider = createSlider(infoTable.getWidth(), 10, 0, 50, 0.5f, 5.0f, 0.5f, false, infoTable);
        speedSlider.setValue(1f);
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
        //group.setDebug(true);
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
