package com.loginov.simulator.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.loginov.simulator.Evolved;


public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 1200;
		config.height = 700;
		config.x = 10;
		config.y = 50;
		config.forceExit = false;
		new LwjglApplication(new Evolved(), config);
	}
}
