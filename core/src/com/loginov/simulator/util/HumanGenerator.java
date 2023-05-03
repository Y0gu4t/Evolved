package com.loginov.simulator.util;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.loginov.simulator.Actor.Food;
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

    @Override
    protected void defineArea(ResourceManager resourceManager){
        int areaId = MathUtils.random(areas.size()-1);
        float minX = areas.get(areaId).x + Food.getFoodWidth()/2;
        float maxX = minX + areas.get(areaId).width - Food.getFoodWidth();
        float minY = areas.get(areaId).y + Food.getFoodHeight()/2;
        float maxY = minY + areas.get(areaId).height - Food.getFoodHeight();
        humans.add(new Human(resourceManager.humanTexture, MathUtils.random(minX, maxX), MathUtils.random(minY, maxY), SimulationParams.getMETABOLISM()));
    }

    public ArrayList<Human> getHumans() {
        return humans;
    }

    public void remove(Human human) {
        humans.remove(human);
    }

    public void add(ArrayList<Human> humans){
        this.humans.addAll(humans);
    }

    public void add(Human human){
        humans.add(human);
    }
}
