package com.mygdx.game.desktop;

import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mygdx.game.GameEngine;
import com.mygdx.game.ScreenManager;

/**
 * DesktopLauncher class is one of many platforms provided by
 * the LibGDX API in which to run the game, which is itself
 * separate from any platform, though not necessarily compatible.
 * This game has only been designed to be compatible on a desktop.
 * @author Sean Aubrey, Gabriel Fountain, Brandon Conn
 */
public class DesktopLauncher {	
	
	/**
	 * Detects computer's window size and sets the window to 
	 * these dimensions to be fullscreen.
	 * @param arg main
	 */
	public static void main (String[] arg) {
		DisplayMode displayMode = LwjglApplicationConfiguration.getDesktopDisplayMode();
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.setFromDisplayMode(displayMode);
		config.title = "It's party time";
		//config.fullscreen = false;
		//new LwjglApplication(new GameEngine(), config);
		new LwjglApplication(new ScreenManager(), config);
		GameEngine.setWinHeight(displayMode.height);
		GameEngine.setWinWidth(displayMode.width);
	}
}
