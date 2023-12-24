package com.loginov.simulator.Actor.Interfaces;

import com.loginov.simulator.Actor.Human;
import com.loginov.simulator.Actor.Warrior;
import com.loginov.simulator.util.HumanGenerator;

import java.util.ArrayList;

public interface Steal {

    public void findActor(ArrayList<Human> humans);

    public void steal(HumanGenerator humanGenerator);

    public void addVictim(Human human);

}
