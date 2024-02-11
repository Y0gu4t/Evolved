package com.loginov.simulator.util;

import com.badlogic.gdx.math.Circle;
import com.loginov.simulator.Clan.Clan;
import com.loginov.simulator.Clan.Sector;

import java.util.ArrayList;

public class ClanFactory {
    private ArrayList<Clan> clans;
    public static float minAngle;
    public static float maxAngle;

    public ClanFactory() {
        clans = new ArrayList<>();
    }

    public void createClans(HumanGenerator humanGenerator, ResourceManager resourceManager) {
        if (humanGenerator.getAreas().size() == 1) {
            minAngle = 360f / (SimulationParams.getClanCount() * 4);
            maxAngle = 360f / (SimulationParams.getClanCount() - 1) - minAngle - 10;
            float angle = 360f / (SimulationParams.getClanCount()) - 10;
            Circle area = humanGenerator.getAreas().get(0);
            for (int i = 0; i < SimulationParams.getClanCount(); i++) {
                float middle = i * 360f / SimulationParams.getClanCount();
                Sector territory = new Sector(area.x, area.y, area.radius, middle, angle, SimulationParams.getClanColors().get(i));
                Clan clan = new Clan(territory);
                resourceManager.setTextureForClan(clan, resourceManager.humanTextures.get(i));
                clans.add(clan);
            }
            for (int i = 0; i < clans.size(); i++) {
                clans.get(i).setLeftEnemy(clans.get((i - 1 + clans.size()) % clans.size()));
                clans.get(i).setRightEnemy(clans.get((i + 1 + clans.size()) % clans.size()));
            }
        } else {
            System.err.println("Error when creating clans");
        }
    }

    public void updateClanTerritories(HumanGenerator humanGenerator) {
        float humansCount = humanGenerator.getHumans().size();
        for (Clan clan : clans) {
            float angle = 360f * (clan.getMembers().size() / humansCount) - 10;
            angle = Math.max(Math.min(angle, maxAngle), minAngle);
            clan.getTerritory().setAngle(angle);
        }
    }

    public void removeEmptyClans() {
        ArrayList<Clan> clansToRemove = new ArrayList<>();
        for (Clan clan:
             clans) {
            if (clan.getMembers().size() == 0) {
                clansToRemove.add(clan);
            }
        }
        clans.removeAll(clansToRemove);
    }

    public ArrayList<Clan> getClans() {
        return clans;
    }
}
