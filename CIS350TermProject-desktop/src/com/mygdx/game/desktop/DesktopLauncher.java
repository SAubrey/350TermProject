package com.mygdx.game.desktop;

import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mygdx.game.GameEngine;

public class DesktopLauncher {	
	
	public static void main (String[] arg) {
		DisplayMode displayMode = LwjglApplicationConfiguration.getDesktopDisplayMode();
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.setFromDisplayMode(displayMode);
		config.title = "It's party time";
		new LwjglApplication(new GameEngine(), config);
		GameEngine.windowHeight = displayMode.height;
		GameEngine.windowWidth = displayMode.width;
	}
}
