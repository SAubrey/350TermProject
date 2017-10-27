package com.mygdx.game;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.badlogic.gdx.Game;

//import com.badlogic.gdx.backends.lwjgl.LwjglApplication;

public class Testing {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		GameEngine game = new GameEngine();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		
	}

	@Test
	public void test() {
		GameEngine game = new GameEngine();
		
	}

}
