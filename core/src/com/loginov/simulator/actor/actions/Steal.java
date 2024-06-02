package com.loginov.simulator.actor.actions;

import com.loginov.simulator.actor.Human;
import com.loginov.simulator.util.HumanGenerator;

import java.util.ArrayList;

public interface Steal {

    void findActor(ArrayList<Human> humans);

    void steal(HumanGenerator humanGenerator);

    void addVictim(Human human);

}
