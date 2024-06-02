package com.loginov.simulator.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.files.FileHandle;
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
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.loginov.simulator.actor.Collector;
import com.loginov.simulator.actor.Food;
import com.loginov.simulator.actor.Human;
import com.loginov.simulator.actor.Thief;
import com.loginov.simulator.actor.Warrior;
import com.loginov.simulator.clan.Clan;
import com.loginov.simulator.states.ApplicationState;
import com.loginov.simulator.states.ClanInfoState;
import com.loginov.simulator.states.DiagramInfoState;
import com.loginov.simulator.states.HumanState;
import com.loginov.simulator.states.SimulationState;
import com.loginov.simulator.Evolved;
import com.loginov.simulator.util.ClanFactory;
import com.loginov.simulator.util.DiagramDrawer;
import com.loginov.simulator.util.FoodGenerator;
import com.loginov.simulator.util.HumanGenerator;
import com.loginov.simulator.util.ResourceManager;
import com.loginov.simulator.util.SimulationParams;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SimulatorScreen extends BaseScreen {
    private BaseScreen previousScreen;
    private final DiagramDrawer diagramDrawer;
    private ApplicationState applicationState;
    private ClanInfoState clanInfoState;
    private DiagramInfoState diagramInfoState;
    private SimulationState simulationState;
    private final Viewport apiPort;
    private final Table infoTable;
    private final Table clanTable;
    private final Group simulationGroup;
    private final Group diagramGroup;
    private final FoodGenerator foodGenerator;
    private final HumanGenerator humanGenerator;
    private final ClanFactory clanFactory;
    private float simulatorTime = 0f;
    private int daysPast = 0;
    public static float generateTime = 0f;
    public static float simulationSpeed = 1.0f;
    private FileHandle logFile;

    public SimulatorScreen(Evolved proxy, BaseScreen previousScreen, ResourceManager resourceManager) {
        super(proxy, resourceManager);
        this.previousScreen = previousScreen;
        generateTime = 0.0f;
        simulationSpeed = 1.0f;
        setApplicationState(ApplicationState.RUNNING);
        clanInfoState = ClanInfoState.HIDE;
        diagramInfoState = DiagramInfoState.HIDE;
        simulationState = SimulationState.DAY;

        apiCam = new OrthographicCamera();
        apiCam.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        apiPort = new ScreenViewport(apiCam);

        InputMultiplexer multiplexer = new InputMultiplexer();
        stage = new Stage(apiPort);

        diagramDrawer = new DiagramDrawer();
        infoTable = new Table();
        clanTable = new Table();
        simulationGroup = new Group();
        diagramGroup = new Group();

        handleInfoTable();
        handleSimulationGroup();
        handleDiagramGroup();
        handleClanTable();

        handleTextFieldSimulationInfo();
        handlePauseButton();
        handleClanInfoButton();
        handleDiagramInfoButton();
        handleBackButton();
        handleSpeedSlider();
        handleTextFieldClanInfo();

        stage.addActor(infoTable);
        stage.addActor(clanTable);
        stage.addActor(simulationGroup);
        stage.addActor(diagramGroup);

        humanGenerator = new HumanGenerator(simulationGroup);
        clanFactory = new ClanFactory();
        foodGenerator = new FoodGenerator(simulationGroup);
        clanFactory.createClans(humanGenerator, resourceManager);
        foodGenerator.generate(SimulationParams.getFoodCount(), resourceManager);
        humanGenerator.generate(clanFactory, resourceManager);

        createLogFile();

        multiplexer.addProcessor(stage);
        Gdx.input.setInputProcessor(multiplexer);
    }

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

    public void updatePaused(float delta) {
    }

    public void updateRunning(float delta) {
        simulatorTime += delta * simulationSpeed;
        generateTime += delta * simulationSpeed;
        if (simulatorTime % 1.0f >= 1.0f - delta * simulationSpeed) {
            writeLog();
        }
        if (generateTime >= simulationState.getDuration()) {
            switch (simulationState) {
                case NIGHT:
                    clanFactory.removeEmptyClans();
                    List<Integer> membersList = new ArrayList<>();
                    for (Clan clan : clanFactory.getClans()) {
                        if (clan != null){
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
                            membersList.add(clan.getMembers().size());
                        } else {
                            membersList.add(0);
                        }
                    }
                    clanFactory.updateClanTerritories(humanGenerator);
                    diagramDrawer.addPoint(daysPast, membersList);
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

    public void draw(float delta) {
        Gdx.gl.glClearColor(250 / 255f, 235 / 255f, 215 / 255f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        proxy.getBatch().setProjectionMatrix(apiCam.combined);
        proxy.getShapeRenderer().setProjectionMatrix(apiCam.combined);

        for (Clan clan : clanFactory.getClans()) {
            if (clan != null) {
                clan.draw(proxy.getShapeRenderer());
            }
        }

        // show a human's path to the goal
        /*for (Human h : humanGenerator.getHumans()) {
            h.debug(proxy.getShapeRenderer(), apiCam);
        }*/

        proxy.getBatch().begin();

        for (Food f : foodGenerator.getFood()) {
            f.draw(proxy.getBatch());
        }

        for (Human h : humanGenerator.getHumans()) {
            h.draw(proxy.getBatch());
        }

        stage.getActors().get(0).draw(proxy.getBatch(), 1);
        if (clanInfoState == ClanInfoState.SHOW) {
            stage.getActors().get(1).draw(proxy.getBatch(), 1);
        }

        proxy.getBatch().end();

        if (diagramInfoState == DiagramInfoState.SHOW) {
            diagramDrawer.drawDiagram(proxy.getShapeRenderer(), proxy.getBatch(), resourceManager, diagramGroup);
        }

        stage.act(delta);
    }

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

    public void changeClanInfoState() {
        switch (clanInfoState) {
            case HIDE:
                clanInfoState = ClanInfoState.SHOW;
                diagramInfoState = DiagramInfoState.HIDE;
                break;
            case SHOW:
                clanInfoState = ClanInfoState.HIDE;
                break;
        }
    }

    public void changeDiagramInfoState() {
        switch (diagramInfoState) {
            case HIDE:
                diagramInfoState = DiagramInfoState.SHOW;
                clanInfoState = ClanInfoState.HIDE;
                break;
            case SHOW:
                diagramInfoState = DiagramInfoState.HIDE;
                break;
        }
    }

    private void handleInfoTable() {
        infoTable.setX(stage.getWidth() / 95);
        infoTable.setY(stage.getHeight() / 100);
        infoTable.setWidth(stage.getWidth() / 7);
        infoTable.setHeight(stage.getHeight() - 2 * stage.getHeight() / 100);
        infoTable.align(Align.topLeft);
    }

    private void handleClanTable() {
        clanTable.setX(simulationGroup.getX() + simulationGroup.getWidth() + stage.getWidth() / 190);
        clanTable.setY(stage.getHeight() / 100);
        clanTable.setWidth(stage.getWidth() - clanTable.getX() - stage.getWidth() / 95);
        clanTable.setHeight(stage.getHeight() - 2 * stage.getHeight() / 100);
    }

    /**
     * Create simulation's information field and add info updater
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

                textArea.setText(String.format("\n Simulation time: %d\n", (int) simulatorTime) +
                        String.format("\n Times of day: %s\n", simulationState) +
                        String.format("\n Days past: %d\n", daysPast) +
                        String.format("\n Humans: %d\n", humanGenerator.getHumans().size()) +
                        String.format("\n Collectors: %d (%d %%)\n", collectorsCount, MathUtils.floor(collectorsPercentage)) +
                        String.format("\n Warriors: %d (%d %%)\n", warriorsCount, MathUtils.floor(warriorsPercentage)) +
                        String.format("\n Thief: %d (%d %%)\n", thievesCount, MathUtils.floor(thievesPercentage)) +
                        String.format("\n Food: %d\n", foodGenerator.getFood().size()) +
                        String.format("\n Average satiety: %.2f\n", avgSatiety) +
                        String.format("\n Average age: %.2f\n", avgAge) +
                        String.format("\n Average meta: %.2f\n", avgMetabolism));
                return false;
            }
        });
    }

    private void handleTextFieldClanInfo() {
        for (int i = 0; i < SimulationParams.getClanCount(); i++) {
            boolean newRow = i % 2 == 1;
            final TextArea textArea = createTextArea("", clanTable.getWidth() / 2 - clanTable.getWidth() / 40,
                    clanTable.getHeight() / 2 - clanTable.getHeight()/20, clanTable.getWidth() / 60, clanTable.getHeight() / 40,
                    newRow, clanTable, SimulationParams.getClanList().get(i).getTextAreaStyle());
            Actor thisTextArea = infoTable.getCells().get(i).getActor();
            final int finalI = i;
            thisTextArea.addAction(new Action() {
                @SuppressWarnings("DefaultLocale")
                @Override
                public boolean act(float delta) {
                    if (clanFactory.getClans().get(finalI) != null) {
                        int clanFood = clanFactory.getClans().get(finalI).getFoodStorage();
                        int allMembers = clanFactory.getClans().get(finalI).getMembers().size();
                        List<Integer> membersTypeCount = clanFactory.getClans().get(finalI).getMembersTypeCount();

                        textArea.setText(String.format("\n Members: %d\n", allMembers) +
                                String.format("\n Collectors: %d\n", membersTypeCount.get(0)) +
                                String.format("\n Thieves: %d\n", membersTypeCount.get(1)) +
                                String.format("\n Warriors: %d\n", membersTypeCount.get(2)) +
                                String.format("\n Food: %d\n", clanFood));
                    } else {
                        textArea.setText(String.format("\n Members: %d\n", 0) +
                                String.format("\n Collectors: %d\n", 0) +
                                String.format("\n Thieves: %d\n", 0) +
                                String.format("\n Warriors: %d\n", 0) +
                                String.format("\n Food: %d\n", 0));
                    }
                    return false;
                }
            });
        }
    }

    /**
     * Create back to menu button and set new screen listener
     */
    private void handleBackButton() {
        createButton("Back", infoTable.getWidth(), 50, 0, 25, infoTable);
        Actor thisButton = infoTable.getCells().get(4).getActor();
        thisButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                proxy.setScreen(new MenuScreen(proxy, resourceManager));
            }
        });
    }

    /**
     * Create pause button and simulation pause listener
     */
    private void handlePauseButton() {
        createButton("Pause", infoTable.getWidth(), 50, 0, 25, infoTable);
        Actor thisButton = infoTable.getCells().get(1).getActor();
        thisButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                setApplicationState(ApplicationState.PAUSED);
            }
        });
    }

    private void handleClanInfoButton() {
        createButton("Clan info", infoTable.getWidth(), 50, 0, 25, infoTable);
        Actor thisButton = infoTable.getCells().get(2).getActor();
        thisButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                changeClanInfoState();
            }
        });
    }

    private void handleDiagramInfoButton() {
        createButton("Graph", infoTable.getWidth(), 50, 0, 25, infoTable);
        Actor thisButton = infoTable.getCells().get(3).getActor();
        thisButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                changeDiagramInfoState();
            }
        });
    }

    private void handleSpeedSlider() {
        final Slider speedSlider = createSlider(infoTable.getWidth(), 10, 0, 25, 0.0f, 5.0f, 0.5f, false, infoTable);
        speedSlider.setValue(simulationSpeed);
        Actor thisSlider = infoTable.getCells().get(5).getActor();
        thisSlider.addAction(new Action() {
            @Override
            public boolean act(float delta) {
                simulationSpeed = speedSlider.getValue();
                return false;
            }
        });
    }

    private void handleSimulationGroup() {
        simulationGroup.setBounds(infoTable.getX() + infoTable.getWidth() + stage.getWidth() / 190, stage.getHeight() / 100,
                stage.getWidth() - infoTable.getWidth() * 3.5f + 6 * stage.getWidth() / 190, stage.getHeight() - stage.getHeight() / 50);
    }

    private void handleDiagramGroup() {
        float x = simulationGroup.getX() + simulationGroup.getWidth() + stage.getWidth() / 190;
        float y = stage.getHeight() / 100;
        float width = stage.getWidth() - x - stage.getWidth() / 190;
        float height = stage.getHeight() - stage.getHeight() / 50;
        diagramGroup.setBounds(x, y,width, height);
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

    @SuppressWarnings("DefaultLocale")
    private void writeLog(){
        int i = 1;
        for (Clan clan:
             clanFactory.getClans()) {
            List<Integer> membersTypeCount = clan.getMembersTypeCount();
            logFile.writeString(String.format("\n%d,%d,%d,%d,%d,%d,%d",
                    i,
                    MathUtils.floor(simulatorTime),
                    clan.getMembers().size(),
                    membersTypeCount.get(0),
                    membersTypeCount.get(1),
                    membersTypeCount.get(2),
                    clan.getFoodStorage()), true);
            i++;
        }
    }

    @SuppressWarnings("DefaultLocale")
    private void createLogFile() {
        String fileName = String.format("logs/log:c1-%d-%d-%d:c2-%d-%d-%d(%s).log",
                SimulationParams.getClanList().get(0).getCollectorCount(),
                SimulationParams.getClanList().get(0).getThiefCount(),
                SimulationParams.getClanList().get(0).getWarriorCount(),
                SimulationParams.getClanList().get(1).getCollectorCount(),
                SimulationParams.getClanList().get(1).getThiefCount(),
                SimulationParams.getClanList().get(1).getWarriorCount(),
                LocalDate.now());
        logFile = Gdx.files.local(fileName);
        logFile.writeString("Clan,Time,Members,Collectors,Thieves,Warriors,Food",false);
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
    public void dispose() {
        super.dispose();
        simulationSpeed = 1f;
        stage.dispose();
    }

}
