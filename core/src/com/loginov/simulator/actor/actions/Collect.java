package com.loginov.simulator.actor.actions;

import com.loginov.simulator.actor.Food;
import com.loginov.simulator.util.FoodGenerator;

import java.util.ArrayList;

public interface Collect {

    void findFood(ArrayList<Food> food);

    void collect(FoodGenerator foodGenerator);

    Food getFoodToEat();
}

