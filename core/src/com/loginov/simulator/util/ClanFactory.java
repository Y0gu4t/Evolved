package com.loginov.simulator.util;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.loginov.simulator.Clan.Clan;
import com.loginov.simulator.Clan.Sector;

import java.util.ArrayList;

public class ClanFactory {
    private ArrayList<Clan> clans;

    public ClanFactory() {
        clans = new ArrayList<>();
    }

    public void createClans(HumanGenerator humanGenerator) {
        if (humanGenerator.getAreas().size() == 1) {
            Circle area = humanGenerator.getAreas().get(0);
            float degree = 360 / (SimulationParams.getClanCount() + 1.0f);
            for (int i = 0; i < SimulationParams.getClanCount(); i++) {
                float start = 90 + i * degree * (1f + 1f / SimulationParams.getClanCount());
                Sector territory = new Sector(area.x, area.y, area.radius, start, degree, generateRandomColor());
                clans.add(new Clan(territory));
            }
        } else {
            System.err.println("Error when creating clans");
        }
    }

    private Color generateRandomColor() {
        float red = MathUtils.random();
        float green = MathUtils.random();
        float blue = MathUtils.random();
        return new Color(red, green, blue, 1);
    }

    public ArrayList<Clan> getClans() {
        return clans;
    }
}
