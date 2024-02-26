package com.loginov.simulator.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

import com.loginov.simulator.Evolved;


public class DesktopLauncher {
	public static void main (String[] arg) {
		final Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setWindowedMode(1920,1000);
		config.setWindowPosition(10, 10);
		config.setForegroundFPS(60);
		config.setResizable(false);
		new Lwjgl3Application(new Evolved(), config);
	}
}
