package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * 
 * @author Sean Aubrey, Gabriel Fountain, Brandon Conn
 */
public class ScreenManager extends Game {

	/**   */
	private SpriteBatch batch;
	
	/**   */
	private BitmapFont font;
	
	/**
	 * 
	 */
	public void create() {
		batch = new SpriteBatch();
		//Use LibGDX's default Arial font.
		font = new BitmapFont();
		//font.getData().setScale(1.3f);
		this.setScreen(new MainMenuScreen(this));
	}

	/**
	 * 
	 */
	public void render() {
		super.render(); //important!
	}
	
	/**
	 * 
	 */
	public void dispose() {
		batch.dispose();
		font.dispose();
	}
}
