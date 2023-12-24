package com.loginov.simulator.util;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.loginov.simulator.Actor.Collector;
import com.loginov.simulator.Actor.Human;
import com.loginov.simulator.Actor.Thief;
import com.loginov.simulator.Actor.Warrior;

import java.util.ArrayList;

public class HumanGenerator extends UnitGenerator {
    private ArrayList<Human> humans;

    private ArrayList<Human> children;

    public HumanGenerator(Group group) {
        super(group);
        this.minAreaSize = SimulationParams.getMinHumanAreaSize();
        this.maxAreaSize = SimulationParams.getMaxHumanAreaSize();
        this.minDistance = SimulationParams.getMinHumanAreaDistance();
        this.amountArea = SimulationParams.getHumanAreas();
        humans = new ArrayList<>();
        children = new ArrayList<>();
        generateAreas(group);
    }

    @Override
    protected void defineArea(ResourceManager resourceManager) {

    }

    /**
     * select a random area from the available ones and
     * position the person at the edge of this area
     */
    private void defineArea(ResourceManager resourceManager, String human) {
        int areaId = MathUtils.random(areas.size() - 1);
        float randomAngle = MathUtils.random(-MathUtils.PI, MathUtils.PI);
        float radius = areas.get(areaId).radius;
        float x = areas.get(areaId).x - Human.getHumanWidth() / 2 + (radius - Human.getHumanHeight()) * MathUtils.cos(randomAngle);
        float y = areas.get(areaId).y - Human.getHumanHeight() / 2 + (radius - Human.getHumanHeight()) * MathUtils.sin(randomAngle);

        switch (human) {
            case "collector":
                humans.add(new Collector(resourceManager.humanTexture, x, y, SimulationParams.getMETABOLISM()));
                break;
            case "warrior":
                humans.add(new Warrior(resourceManager.humanTexture, x, y, SimulationParams.getMETABOLISM()));
                break;
            case "thief":
                humans.add(new Thief(resourceManager.humanTexture, x, y, SimulationParams.getMETABOLISM()));
                break;
        }
    }

    public void generate(int collectorCount, int warriorCount, int thiefCount, ResourceManager resourceManager) {
        for (int i = 0; i < warriorCount; i++) {
            defineArea(resourceManager, "warrior");
        }
        for (int i = 0; i < collectorCount; i++) {
            defineArea(resourceManager, "collector");
        }
        for (int i = 0; i < thiefCount; i++) {
            defineArea(resourceManager, "thief");
        }
    }


    public ArrayList<Human> getHumans() {
        return humans;
    }

    public void remove(Human human) {
        humans.remove(human);
    }

    public void add(ArrayList<Human> humans) {
        this.humans.addAll(humans);
    }

    public void add(Human human) {
        humans.add(human);
    }

    public void prepareChildren(Human human) {
        Circle area = null;

        for (Circle circle : areas) {
            if (circle.contains(human.getPosition())) {
                area = new Circle(circle);
                break;
            }
        }

        if (area == null) {
            area = new Circle(areas.get(0));
        }

        float randomAngle = MathUtils.random(MathUtils.PI / 8, MathUtils.PI / 2) *
                (MathUtils.randomBoolean(0.5f) ? 1 : -1);
        float angle = MathUtils.acos(
                (human.getHome().x - area.x + Human.getHumanWidth() / 2)
                        / (area.radius - Human.getHumanHeight())) + randomAngle;
        float x = area.x - Human.getHumanWidth() / 2 + (area.radius - Human.getHumanHeight()) * MathUtils.cos(angle);
        float y = area.y - Human.getHumanHeight() / 2 + (area.radius - Human.getHumanHeight()) * MathUtils.sin(angle);

        human.setHome(new Vector2(x, y));
        children.add(human);
    }

    public void addChildren() {
        humans.addAll(children);
        children.clear();
    }

    public boolean children() {
        return children.size() > 0;
    }
}
