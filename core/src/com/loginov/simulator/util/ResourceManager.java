package com.loginov.simulator.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.loginov.simulator.clan.Clan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ResourceManager {
    private final AssetManager assetManager;
    private final TextureAtlas textureAtlas;
    public ArrayList<Texture> humanTextures;
    private final Map<Clan, Texture> clanTextureMap;
    public Texture foodTexture;
    public Skin toolSkin;
    private final static String TEXTURE_ATLAS_PATH = "flat-earth/skin/flat-earth-ui.atlas";
    private final static String SKIN_PATH = "flat-earth/skin/flat-earth-ui.json";

    public ResourceManager() {
        assetManager = new AssetManager();
        humanTextures = new ArrayList<>();

        assetManager.load("flat-earth/skin/flat-earth-ui.atlas", TextureAtlas.class);

        assetManager.load("human_1.png", Texture.class);
        assetManager.load("human_2.png", Texture.class);
        assetManager.load("human_3.png", Texture.class);
        assetManager.load("bread.png", Texture.class);

        assetManager.finishLoading();

        textureAtlas = assetManager.get(TEXTURE_ATLAS_PATH, TextureAtlas.class);
        humanTextures.add(assetManager.get("human_1.png"));
        humanTextures.add(assetManager.get("human_2.png"));
        humanTextures.add(assetManager.get("human_3.png"));
        foodTexture = assetManager.get("bread.png");

        clanTextureMap = new HashMap<>();

        toolSkin = new Skin();
        toolSkin.addRegions(textureAtlas);
        toolSkin.load(Gdx.files.internal(SKIN_PATH));
    }

    public void setTextureForClan(Clan clan, Texture texture) {
        clanTextureMap.put(clan, texture);
    }

    public Texture getClanTexture(Clan clan) {
        return clanTextureMap.get(clan);
    }

    public void dispose() {
        assetManager.dispose();
        textureAtlas.dispose();
        for (Texture texture : humanTextures) {
            texture.dispose();
        }
        foodTexture.dispose();
    }
}
