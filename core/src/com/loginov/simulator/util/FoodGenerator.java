package com.loginov.simulator.util;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.loginov.simulator.Actor.Food;

import java.util.ArrayList;

public class FoodGenerator extends UnitGenerator {
    private ArrayList<Food> food;

    public FoodGenerator(Group group) {
        super(group);
        this.minAreaSize = SimulationParams.getMinFoodAreaSize();
        this.maxAreaSize = SimulationParams.getMaxFoodAreaSize();
        this.minDistance = SimulationParams.getMinFoodAreaDistance();
        this.amountArea = SimulationParams.getFoodAreas();
        food = new ArrayList<>();
        generateAreas(group);
    }

    @Override
    protected void defineArea(ResourceManager resourceManager) {
        int areaId = MathUtils.random(areas.size() - 1);

        float randomAngle = MathUtils.random(-MathUtils.PI, MathUtils.PI);
        float randomRadius = MathUtils.random(0, areas.get(areaId).radius);
        float x = areas.get(areaId).x - Food.getFoodWidth() / 2 + randomRadius * MathUtils.cos(randomAngle);
        float y = areas.get(areaId).y - Food.getFoodHeight() / 2 + randomRadius * MathUtils.sin(randomAngle);

        food.add(new Food(resourceManager.foodTexture, x, y));
    }

    public void removeFood(Food f) {
        food.remove(f);
    }

    public ArrayList<Food> getFood() {
        return food;
    }
}
