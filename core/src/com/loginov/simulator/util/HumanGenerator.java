package com.loginov.simulator.util;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.loginov.simulator.Actor.Human;

import java.util.ArrayList;

public class HumanGenerator extends UnitGenerator {
    private ArrayList<Human> humans;

    public HumanGenerator(Group group) {
        super(group);
        this.minAreaSize = SimulationParams.getMinHumanAreaSize();
        this.maxAreaSize = SimulationParams.getMaxHumanAreaSize();
        this.minDistance = SimulationParams.getMinHumanAreaDistance();
        this.amountArea = SimulationParams.getHumanAreas();
        humans = new ArrayList<>();
        generateAreas(group);
    }

    /**
     *  select a random area from the available ones and
     *  position the person at the edge of this area
     */
    @Override
    protected void defineArea(ResourceManager resourceManager) {
        int areaId = MathUtils.random(areas.size() - 1);
        float randomAngle = MathUtils.random(-MathUtils.PI, MathUtils.PI);
        float radius = areas.get(areaId).radius;
        float x = areas.get(areaId).x - Human.getHumanWidth() / 2 + radius * MathUtils.cos(randomAngle);
        float y = areas.get(areaId).y - Human.getHumanHeight() / 2 + radius * MathUtils.sin(randomAngle);
        humans.add(new Human(resourceManager.humanTexture, x, y, SimulationParams.getMETABOLISM()));
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
}
