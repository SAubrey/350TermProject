package com.mygdx.game; 

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

/**
 * EnemyManager class spawns and keeps track of all enemy objects.
 * 
 * @author Sean Aubrey, Gabriel Fountain, Brandon Conn
 */
public class EnemyManager {

	/** Pre-instantiated player. */
	private Player player;
	
	/** Pre-instantiated world. */
	private World world;

	/**  */
	private Random rand;
	
	/**  */
	private Swarmer swarmer;
	
	/**  */
	private Spitter spitter;
	
	/**  */
	private Demon demon;
	
	/**  */
	private ArrayList<Enemy> enemies;
	
	/**  */
	private Array<Body> bodies;
	
	/**  */
	private int viewWidth;
	
	/**  */
	private int viewHeight;
	
	/**  */
	private float playerX;
	
	/**  */
	private float playerY;
	
	/**  */
	private int spawnX;
	
	/**  */
	private int spawnY;
	
	/**  */
	private float swarmerAccumulator;
	
	/**  */
	private float spitterAccumulator;
	
	/**  */
	private float demonAccumulator;
	
	/**  */
	private float demonTime = 90; // half-seconds(roughly)
	
	/**  */
	private float playerSum;
	
	/**  */
	private float spawnSum;
	
	/**  */
	private boolean validSpawn;
	
	/**  */
	private boolean spawned = false;
	
	/**  */
	private boolean demonSpawned = false;;
	
	/**  */
	private float time;
	
	/**  */
	private float swarmerSpawnTime = 2.0f;
	
	/**  */
	private float spitterSpawnTime = 10;
	
	/**  */
	private float multiplier = 1;
	
	/**  */
	private float demonsSlain;
	
	//if number of enemy kills is above a certain number, increase player shot rate
	//after certain amount of time, increase enemy spawn rate/type
	//every 10 seconds create a spawn packet so that enemies spawn  close together
	/**
	 * 
	 * @param viewWidth viewport width
	 * @param viewHeight viewport height
	 * @param player instantiated player
	 */
	public EnemyManager(final int viewWidth, final int viewHeight, final Player player) {
		this.viewWidth = viewWidth;
		this.viewHeight = viewHeight;
		this.player = player;
		world = GameEngine.getWorld();
		enemies = new ArrayList<Enemy>();
		bodies = new Array<Body>();
		rand = new Random();
	}
	
	/**
	 * Updates timers and spawner methods. Clears enemies if their health is below 0.
	 * @param playerX Player's x position
	 * @param playerY Player's y position
	 * @param deltaTime time between frames
	 */
	public void update(final float playerX, final float playerY, final float deltaTime) {
		this.playerX = playerX;
		this.playerY = playerY;
		swarmerAccumulator += deltaTime;
		spitterAccumulator += deltaTime;
		if (!demonSpawned) {
			demonAccumulator += deltaTime;
		}
		time += deltaTime;
		trackProgress();
		spawn();
		for (int i = 0; i < enemies.size(); i++) {
			if (enemies.get(i).getHealth() <= 0) {
				if (enemies.get(i).setDeletable()) { // only returns true for a dead demon
					demonSpawned = false;
					demonSlain();
				}
				enemies.remove(i);
			} else {
				enemies.get(i).update(playerX, playerY);				
			}
		}
	}

	/**
	 * 
	 */
	private void spawn() {
		playerSum = playerX + playerY;
		validSpawn = false;
		if (spitterAccumulator >= spitterSpawnTime) { // spitter
			while (!validSpawn && !demonSpawned) {
				spawnX = rand.nextInt(viewWidth - 2);
				spawnX++; // in case 0
				spawnY = rand.nextInt(viewHeight - 2);
				spawnY++;
				spawnSum = spawnX + spawnY;
				if (Math.abs(spawnSum - playerSum) > 120) {
					spitter = new Spitter(spawnX, spawnY);
					//swarmer.multFireRate(multiplier);
					enemies.add(spitter);
					spitterAccumulator = 0;
					validSpawn = true;
				}
			}
		}
		if (swarmerAccumulator >= swarmerSpawnTime) { // swarmer
			while (!validSpawn && !demonSpawned) {
				spawnX = rand.nextInt(viewWidth - 2);
				spawnX++;
				spawnY = rand.nextInt(viewHeight - 2);
				spawnY++;
				spawnSum = spawnX + spawnY;
				if (Math.abs(spawnSum - playerSum) > 120) {
					if (!checkOverlap(spawnX, spawnY)) {
						swarmer = new Swarmer(spawnX, spawnY);
						swarmer.multMaxVelocity(multiplier);
						enemies.add(swarmer);
						swarmerAccumulator = 0;
						validSpawn = true;
					}
				}
			}
		}
	}
	
	//"Or maybe you can do AABB query before spawning a body to check if there is 
	//already a body at the spawn location"
	// make sure it's spawning in bounds, and not on another body
	// use try catch to check failure location?
	/**
	 * 
	 */
	private void spawnSwarmerGroup() {
		playerSum = playerX + playerY;
		validSpawn = false;
		while (!validSpawn && !demonSpawned) {
			spawnX = rand.nextInt(viewWidth - 50);
			spawnX += 25;
			spawnY = rand.nextInt(viewHeight - 50);
			spawnY += 25;
			spawnSum = spawnX + spawnY;
			if (Math.abs(spawnSum - playerSum) > (viewWidth / 2 - 25)) {
				validSpawn = true;
				for (int i = 0; i < 3; i++) {
					for (int j =  0; j < 6; j++) {
						int x = spawnX + j * 3;
						int y = spawnY + i * 3;
						if (!checkOverlap(x, y)) {
							swarmer = new Swarmer(x, y);
							swarmer.multMaxVelocity(multiplier);
							enemies.add(swarmer);
						}
					}
				}
			}
		}
	}

	/**
	 * 
	 * @param spawnX Proposed x spawn coordinate
	 * @param spawnY Proposed y spawn coordinate
	 * @return true if overlap
	 */
	private boolean checkOverlap(final int spawnX, final int spawnY) {
		world.getBodies(bodies);
		for (Body b : bodies) {
			if (spawnX > (b.getPosition().x - 1) && spawnX < (b.getPosition().x + 1) 
				&& spawnY > (b.getPosition().y - 1) && spawnY < (b.getPosition().y + 1)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 
	 */
	public void demonSlain() {
		demonAccumulator = 0;
		demonsSlain++;
		// adjust player's shoot speed
		multiplier += 0.15f;
		player.multMovement(multiplier);
	}
	
	/**
	 * 
	 */
	private void trackProgress() {
		// only spawn once every 10 seconds, and not immediately
		if (!demonSpawned) {
			if ((int) time % 10 == 0 && !spawned && time >= 10) {
				//swarmerSpawnTime = 0.05f;
				spawnSwarmerGroup();
				spawned = true;
			} else if ((int) time % 10 == 1) {
				//swarmerSpawnTime = 2.0f;
				spawned =  false;
			}
		}

		if (demonAccumulator >= demonTime && !demonSpawned) {
			demon = new Demon(GameEngine.getViewWidth() / 2, GameEngine.getViewHeight() / 2);
			demon.setMultiplier(multiplier);
			enemies.add(demon);
			demonSpawned = true;
			demonAccumulator = 0;
		}
	}
}
