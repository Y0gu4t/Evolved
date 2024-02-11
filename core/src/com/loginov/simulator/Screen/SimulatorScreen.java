package com.loginov.simulator.Screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.loginov.simulator.Actor.Collector;
import com.loginov.simulator.Actor.Food;
import com.loginov.simulator.Actor.Human;
import com.loginov.simulator.Actor.Thief;
import com.loginov.simulator.Actor.Warrior;
import com.loginov.simulator.Clan.Clan;
import com.loginov.simulator.Enums.ApplicationState;
import com.loginov.simulator.Enums.HumanState;
import com.loginov.simulator.Enums.SimulationState;
import com.loginov.simulator.Evolved;
import com.loginov.simulator.util.ClanFactory;
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
    private ClanFactory clanFactory;
    private float simulatorTime = 0f;
    private int daysPast = 0;
    public static float generateTime = 0f;
    public static float simulationSpeed = 0.0f;

    public SimulatorScreen(Evolved proxy, BaseScreen previousScreen, ResourceManager resourceManager) {
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
        infoTable = new Table();
        group = new Group();

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
        clanFactory = new ClanFactory();
        foodGenerator = new FoodGenerator(group);
        clanFactory.createClans(humanGenerator, resourceManager);
        foodGenerator.generate(SimulationParams.getFoodCount(), resourceManager);
        humanGenerator.generate(clanFactory, resourceManager);
        // set input
        multiplexer.addProcessor(stage);
        Gdx.input.setInputProcessor(multiplexer);
    }

    /**
     * update human's satiety
     */
    private void satietyUpdate(float deltaSatiety) {

        ArrayList<Human> toDelete = new ArrayList<>();

        for (Human human : humanGenerator.getHumans()) {

            human.setSatiety(deltaSatiety * human.getMetabolism() * human.getACCELERATION());

            if (human.getSatiety() <= 0 || human.getAge() >= Human.MAX_AGES_OF_LIFE) {
                toDelete.add(human);
            }
        }

        for (Human human : toDelete) {
            humanGenerator.remove(human);
            human.getClan().remove(human);
        }
    }

    // update when simulation paused
    public void updatePaused(float delta) {
    }

    /**
     * update simulation variables
     *
     * @param delta
     */
    public void updateRunning(float delta) {
        simulatorTime += delta * simulationSpeed;
        generateTime += delta * simulationSpeed;
        if (generateTime >= simulationState.getDuration()) {
            // perform actions after the end of day/night
            switch (simulationState) {
                case NIGHT:
                    clanFactory.removeEmptyClans();
                    for (Clan clan :
                            clanFactory.getClans()) {
                        clan.feedMembers();
                        int childrenCount = 0;
                        for (Human human :
                                clan.getMembers()) {
                            if (human.giveBirthOpportunity() && human.getState() == HumanState.AT_HOME) {
                                human.setAgesAfterChildbirth(0);
                                childrenCount++;
                            }
                        }
                        humanGenerator.addChildren(resourceManager, clan, clan.classifyHumansByType(childrenCount));
                    }
                    clanFactory.updateClanTerritories(humanGenerator);
                    daysPast++;
                    for (Human h : humanGenerator.getHumans()) {
                        h.updateAge();
                        h.updateAgesAfterChildbirth();
                        h.setState(HumanState.WORK);
                    }
                    break;

                case DAY:
                    for (Human h : humanGenerator.getHumans()) {
                        h.findNearestHome();
                        h.setState(HumanState.GO_HOME);
                    }
                    break;
            }

            foodGenerator.generate(SimulationParams.getFoodAdd(), resourceManager);
            satietyUpdate(SimulationParams.getDeltaSatiety());
            changeSimulationState();
        }

        for (Human h : humanGenerator.getHumans()) {
            h.operate(foodGenerator, humanGenerator, simulationState);
        }
    }

    private void changeSimulationState() {
        generateTime = 0;

        switch (simulationState) {
            case DAY:
                simulationState = SimulationState.NIGHT;
                break;

            case NIGHT:
                simulationState = SimulationState.DAY;
                break;
        }
    }

    /**
     * FIXME: 27.04.2023 add threads, maybe need to replace
     */
    public void draw(float delta) {
        Gdx.gl.glClearColor(250 / 255f, 235 / 255f, 215 / 255f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        proxy.getBatch().setProjectionMatrix(apiCam.combined);
        proxy.getShapeRenderer().setProjectionMatrix(apiCam.combined);

        // show simulation areas
        // debugAreas(humanGenerator.getAreas());
        // debugAreas(foodGenerator.getAreas());

        for (Clan clan : clanFactory.getClans()) {
            clan.draw(proxy.getShapeRenderer());
        }

        // show a human's path to the goal
        for (Human h : humanGenerator.getHumans()) {
            h.debug(proxy.getShapeRenderer(), apiCam);
        }

        proxy.getBatch().begin();

        for (Food f : foodGenerator.getFood()) {
            f.draw(proxy.getBatch());
        }

        for (Human h : humanGenerator.getHumans()) {
            h.draw(proxy.getBatch());
        }

        proxy.getBatch().end();

        stage.act(delta);
        stage.draw();
    }

    /**
     * update simulation
     *
     * @// FIXME: 27.04.2023 need to rework
     */
    public void update(float delta) {
        switch (applicationState) {
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
    public void setApplicationState(ApplicationState state) {
        switch (state) {
            case RUNNING:
                applicationState = ApplicationState.RUNNING;
                break;

            case PAUSED:
                if (applicationState == ApplicationState.PAUSED) {
                    applicationState = ApplicationState.RUNNING;
                } else if (applicationState == ApplicationState.RUNNING) {
                    applicationState = ApplicationState.PAUSED;
                }
                break;
        }
    }

    /**
     * set table's params
     */
    private void handleInfoTable() {
        infoTable.setWidth(stage.getWidth() / 6);
        infoTable.setHeight(stage.getHeight());
        infoTable.padLeft(10).padTop(10);
        infoTable.align(Align.topLeft);
    }

    /**
     * create simulation's information field and add info updater
     */
    private void handleTextFieldSimulationInfo() {
        final TextArea textArea = createTextArea("", infoTable.getWidth(), infoTable.getHeight() / 1.8f, 0, 50, true, infoTable);

        Actor thisTextArea = infoTable.getCells().get(0).getActor();
        thisTextArea.addAction(new Action() {
            @SuppressWarnings("DefaultLocale")
            @Override
            public boolean act(float delta) {
                float avgSatiety = 0f;
                float avgAge = 0f;
                float avgMetabolism = 0f;
                int collectorsCount = 0;
                int warriorsCount = 0;
                int thievesCount = 0;
                float collectorsPercentage = 0f;
                float warriorsPercentage = 0f;
                float thievesPercentage = 0f;

                for (Human h : humanGenerator.getHumans()) {
                    avgSatiety += h.getSatiety();
                    avgAge += h.getAge();
                    avgMetabolism += h.getMetabolism();
                    if (h.getClass() == Collector.class) {
                        collectorsCount++;
                    } else if (h.getClass() == Warrior.class) {
                        warriorsCount++;
                    } else if (h.getClass() == Thief.class) {
                        thievesCount++;
                    }
                }
                avgSatiety /= humanGenerator.getHumans().size();
                avgAge /= humanGenerator.getHumans().size();
                avgMetabolism /= humanGenerator.getHumans().size();
                if (humanGenerator.getHumans().size() != 0) {
                    collectorsPercentage = (float) collectorsCount * 100 / humanGenerator.getHumans().size();
                    warriorsPercentage = (float) warriorsCount * 100 / humanGenerator.getHumans().size();
                    thievesPercentage = (float) thievesCount * 100 / humanGenerator.getHumans().size();
                }

                textArea.setText(String.format("Simulation time: %d\n", (int) simulatorTime) +
                        String.format("\nTimes of day: %s\n", simulationState) +
                        String.format("\nDays past: %d\n", daysPast) +
                        String.format("\nHumans: %d\n", humanGenerator.getHumans().size()) +
                        String.format("\nCollectors: %d (%d %%)\n", collectorsCount, MathUtils.floor(collectorsPercentage)) +
                        String.format("\nWarriors: %d (%d %%)\n", warriorsCount, MathUtils.floor(warriorsPercentage)) +
                        String.format("\nThief: %d (%d %%)\n", thievesCount, MathUtils.floor(thievesPercentage)) +
                        String.format("\nFood: %d\n", foodGenerator.getFood().size()) +
                        String.format("\nAverage satiety: %.2f\n", avgSatiety) +
                        String.format("\nAverage age: %.2f\n", avgAge) +
                        String.format("\nAverage meta: %.2f\n", avgMetabolism));
                // false - update info on screen, true - not update
                return false;
            }
        });
    }

    /**
     * create back to menu button and set new screen listener
     */
    private void handleBackButton() {
        createButton("Back", infoTable.getWidth(), 50, 0, 50, infoTable);
        Actor thisButton = infoTable.getCells().get(2).getActor();
        thisButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                proxy.setScreen(new MenuScreen(proxy, resourceManager));
            }
        });
    }

    /**
     * create pause button and simulation pause listener
     */
    private void handlePauseButton() {
        createButton("Pause", infoTable.getWidth(), 50, 0, 50, infoTable);
        Actor thisButton = infoTable.getCells().get(1).getActor();
        thisButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                setApplicationState(ApplicationState.PAUSED);
            }
        });
    }

    private void handleSpeedSlider() {
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

    private void handleGroup() {
        group.setBounds(infoTable.getWidth() + 20, 20, stage.getWidth() - infoTable.getWidth() - 30, stage.getHeight() - 30);
        group.setDebug(false);
    }

    public static float getGenerateTime() {
        return generateTime;
    }

    private void debugAreas(ArrayList<Circle> areas) {
        proxy.getShapeRenderer().setAutoShapeType(true);
        proxy.getShapeRenderer().begin(ShapeRenderer.ShapeType.Filled);
        proxy.getShapeRenderer().setColor(Color.CYAN);
        for (Circle area : areas) {
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
