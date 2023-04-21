package com.loginov.simulator.Screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.loginov.simulator.Actor.Food;
import com.loginov.simulator.Actor.Human;
import com.loginov.simulator.Listeners.PausedListener;

import static com.loginov.simulator.Evolved.proxy;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Random;

public class SimulatorScreen extends InputApiAdapter{
    public static final int SIMULATION_RUNNING = 1;
    public static final int SIMULATION_PAUSED = 2;
    private int state;
    private OrthographicCamera apiCamera;
    private Viewport apiPort;
    private final Random random;
    private Table table;
    private Group group;
    private ShapeRenderer shapeRenderer;
    private Stage stage;
    private Skin buttonSkin;
    private TextureAtlas atlas;
    private TextButton backToMenuButton;
    private Slider speedSlider;
    private TextArea textFieldSimulationInfo;
    private ArrayList<Human> humans;
    private ArrayList<Food> foods;
    public int humanCount = 20;
    public int foodCount = 30;
    public int foodAdd = 10;
    private float simulatorTime = 0f;
    private static final float METABOLISM = (float) (Math.random() + 0.5f);
    private ArrayList<Rectangle2D> humanGenerationAreas;
    private ArrayList<Rectangle2D> foodGenerationAreas;
    public float generatePeriod = 3f;
    public float generateTime = 0f;
    public float deltaSatiety = -10f;
    public static float simulationSpeed = 1f;

    public SimulatorScreen(){
        super(proxy);
        state = SIMULATION_RUNNING;
        apiCamera = new OrthographicCamera();
        apiPort = new ScreenViewport(apiCamera);
        stage = new Stage(apiPort);
        random = new Random();
        buttonSkin = new Skin();
        table = new Table();
        group = new Group();
        humans = new ArrayList<>();
        foods = new ArrayList<>();
        shapeRenderer = new ShapeRenderer();
        humanGenerationAreas = new ArrayList<>();
        foodGenerationAreas = new ArrayList<>();

        table.setWidth(stage.getWidth()/8);
        table.setHeight(stage.getHeight());
        table.padLeft(10).padTop(10);
        table.align(Align.topLeft);

        group.setBounds(table.getWidth(), 0, stage.getWidth() - table.getWidth(), stage.getHeight());


        atlas = new TextureAtlas("flat-earth/skin/flat-earth-ui.atlas");
        buttonSkin.addRegions(atlas);
        buttonSkin.load(Gdx.files.internal("flat-earth/skin/flat-earth-ui.json"));
        textFieldSimulationInfo = new TextArea("Hello, World", buttonSkin, "default");
        textFieldSimulationInfo.setDisabled(true);
        backToMenuButton = new TextButton("Back", buttonSkin, "default");
        TextButton pauseButton = new TextButton("Pause", buttonSkin, "default");
        pauseButton.addListener(new PausedListener(this));
        speedSlider = new Slider(0.5f, 5f, 0.5f, false, buttonSkin,"default-horizontal");
        speedSlider.setValue(simulationSpeed);
        table.add(textFieldSimulationInfo).width(table.getWidth()-table.getPadLeft()).height(300).padTop(20).row();
        table.add(speedSlider).width(table.getWidth()-table.getPadLeft()).padTop(20).row();
        table.add(backToMenuButton).width(table.getWidth()/2).padTop(20).row();
        table.add(pauseButton).width(table.getWidth()/2).padTop(20).row();


        stage.addActor(table);
        stage.addActor(group);
        group.setDebug(true);
        generateHumans(humanCount);
        generateFood(foodCount);
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(stage);
        multiplexer.addProcessor(this);
        Gdx.input.setInputProcessor(multiplexer);
    }

    private void generateHumans(int humanToGenerate){
        addHumanGenerationAreas();
        for(int i=0; i < humanToGenerate; i++){
            int rand = random.nextInt(humanGenerationAreas.size());
            System.out.println(rand);
            Rectangle2D area =  humanGenerationAreas.get(rand);
            float x = (float)(area.getX() + random.nextFloat()*(area.getWidth() - Human.getHumanWidth()) + Human.getHumanWidth()/2);
            float y = (float)(area.getY() + random.nextFloat()*(area.getHeight() - Human.getHumanHeight()) + Human.getHumanHeight()/2);
            humans.add(new Human(new Texture("human.png"), x, y, METABOLISM));
        }
    }

    private void generateFood(int foodToGenerate){
        for(int i=0; i < foodToGenerate; i++){
            float x = group.getX() + random.nextFloat()*(group.getWidth() - Food.getFoodWidth())+Food.getFoodWidth()/2;
            float y = random.nextFloat()*(group.getHeight() - Food.getFoodHeight()/2) + Food.getFoodHeight()/2;
            foods.add(new Food(new Texture("food.png"), x, y));
        }
    }

    private void addHumanGenerationAreas(){
        Rectangle2D r1 = new Rectangle2D.Float(group.getX() + 10, group.getY() + 10, group.getWidth()/8, group.getWidth()/8);
        Rectangle2D r2 = new Rectangle2D.Float(group.getX() + 10, group.getHeight() - group.getWidth()/8, group.getWidth()/8, group.getWidth()/8);
        Rectangle2D r3 = new Rectangle2D.Float(group.getWidth() - group.getWidth()/8 - 10, group.getY() + 10, group.getWidth()/8, group.getWidth()/8);
        Rectangle2D r4 = new Rectangle2D.Float(group.getWidth() - group.getWidth()/8 - 10, group.getHeight() - group.getWidth()/8 - 10, group.getWidth()/8, group.getWidth()/8);
        humanGenerationAreas.add(r1);
        humanGenerationAreas.add(r2);
        humanGenerationAreas.add(r3);
        humanGenerationAreas.add(r4);
    }

    private void infoUpdate(){
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
        textFieldSimulationInfo.setText("Simulation time: " + (int) simulatorTime +
                                       "\nPeople in simulation: " + humans.size() +
                                       "\nFood in simulation: " + foods.size() +
                                        String.format("\nAverage satiety: %.2f", avgSatiety) +
                                        String.format("\nAverage age: %.2f", avgAge) +
                                        String.format("\nAverage meta: %.2f", avgMetabolism));
    }


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

    @Override
    public void show() {

    }

    public void updatePaused(float delta){
    }

    public void updateRunning(float delta){
        simulatorTime += delta * simulationSpeed;
        generateTime += delta;
        if (generateTime >= generatePeriod/simulationSpeed) {
            generateTime = 0;
            generateFood(foodAdd);
            foodCount += foodAdd;
            satietyUpdate(deltaSatiety);
            ArrayList<Human> humansTmp = new ArrayList<>();
            for (Human h : humans) {
                h.updateAge();
                h.updateAgesAfterChildbirth();
                if (h.giveBirthOpportunity()) {
                    humansTmp.add(h.giveBirth(new Texture("human.png")));
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

    public void draw(float delta){
        Gdx.gl.glClearColor(1f, 1f, 1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        proxy.getBatch().setProjectionMatrix(apiCamera.combined);

        proxy.getBatch().begin();
        for (int i = 0; i < humans.size(); i++) {
            humans.get(i).draw(proxy.getBatch());
        }
        for (int i = 0; i < foods.size(); i++) {
            foods.get(i).draw(proxy.getBatch());
        }
        proxy.getBatch().end();
        infoUpdate();
        stage.act(delta);
        stage.draw();
    }

    public void update(float delta){
        simulationSpeed = speedSlider.getValue();
        switch(state){
            case SIMULATION_RUNNING:
                updateRunning(delta);
                break;
            case SIMULATION_PAUSED:
                updatePaused(delta);
                break;
        }

        if(backToMenuButton.isPressed()){
            this.dispose();
            //proxy.setScreen(new MenuScreen());
        }
    }

    public ArrayList<Food> getFoods() {
        return foods;
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
        simulationSpeed = 1f;
        stage.dispose();
        atlas.dispose();
        buttonSkin.dispose();
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
