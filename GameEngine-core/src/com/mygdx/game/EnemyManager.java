package com.mygdx.game;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

/**
 * EnemyManager class spawns and keeps track of all
 * enemy objects.
 *
 */
public class EnemyManager {

	private Player player;
	private World world;
	private Random rand;
	private Swarmer swarmer;
	private Spitter spitter;
	private Demon demon;
	private ArrayList<Enemy> enemies;
	private Array<Body> bodies;
	private int viewWidth;
	private int viewHeight;
	private float playerX;
	private float playerY;
	private int spawnX;
	private int spawnY;
	private float swarmerAccumulator;
	private float spitterAccumulator;
	private float demonAccumulator;
	private float demonTime = 90; // half-seconds(roughly)
	private float playerSum;
	private float spawnSum;
	private int killCount;
	private boolean validSpawn;
	private boolean spawned = false;
	private boolean demonSpawned = false;;
	private float time;
	private float swarmerSpawnTime = 2.0f;
	private float spitterSpawnTime = 10;
	private float multiplier = 1;
	private float demonsSlain;
	
	//if number of enemy kills is above a certain number, increase player shot rate
	//after certain amount of time, increase enemy spawn rate/type
	//every 10 seconds create a spawn packet so that enemies spawn  close together
	public EnemyManager(int viewWidth, int viewHeight, Player player) {
		this.viewWidth = viewWidth;
		this.viewHeight = viewHeight;
		this.player = player;
		world = GameEngine.getWorld();
		enemies = new ArrayList<Enemy>();
		bodies = new Array<Body>();
		rand = new Random();
		//test
		swarmer = new Swarmer(0,1); //320 x 180 view
		System.out.println(viewWidth + " " + viewHeight);
		enemies.add(swarmer);
	}
	/**
	 * Updates timers and spawner methods. Clears enemies if their health is below 0.
	 * @param playerX
	 * @param playerY
	 * @param deltaTime
	 */
	public void update(float playerX, float playerY, float deltaTime) {
		this.playerX = playerX;
		this.playerY = playerY;
		killCount = player.getKillCount();
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
				if (enemies.get(i).setDeletable()) {// only returns true for a dead demon
					demonSpawned = false;
					demonSlain();
				}
				enemies.remove(i);
			}
			else {
				enemies.get(i).update(playerX, playerY);				
			}
		}
	}

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
	private void spawnSwarmerGroup() {
		playerSum = playerX + playerY;
		validSpawn = false;
		while (validSpawn == false && demonSpawned == false) {
			spawnX = rand.nextInt(viewWidth - 50);
			spawnX += 25;
			spawnY = rand.nextInt(viewHeight - 50);
			spawnY += 25;
			spawnSum = spawnX + spawnY;
			if (Math.abs(spawnSum - playerSum) > (viewWidth/2 - 25)) {
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

	private boolean checkOverlap(int spawnX, int spawnY) {
		world.getBodies(bodies);
		for (Body b : bodies) {
			//if (spawnX == b.getPosition().x && spawnY == b.getPosition().y) {
				//return true;
			//}
			if (spawnX > (b.getPosition().x - 1) && spawnX < (b.getPosition().x + 1) 
					&& spawnY > (b.getPosition().y - 1) && spawnY < (b.getPosition().y + 1)) {
				System.out.println("we got an OVERLAAAAAAAAAAAAAAAAAAAAAAAAAAAAAP");
				return true;
			}
		}
		return false;
	}
	
	public void demonSlain() {
		demonAccumulator = 0;
		demonsSlain++;
		// adjust player's shoot speed
		multiplier += 0.15f;
		player.multMovement(multiplier);
		if (demonsSlain >= 2) {
			
		}
	}
	
	private void trackProgress() {
		// only spawn once every 10 seconds, and not immediately
		if (!demonSpawned) {
			if ((int)time % 10 == 0 && spawned == false && time >= 10) {
				//swarmerSpawnTime = 0.05f;
				spawnSwarmerGroup();
				spawned = true;
			}
			else if ((int)time % 10 == 1) {
				//swarmerSpawnTime = 2.0f;
				spawned =  false;
			}
			if ((int)time % 15 == 0) {
			}
		}

		//if ((int)time % 45 == 0 && !demonSpawned && time >= 45) { //use demon spawn  accumulator?
		if (demonAccumulator >= demonTime && !demonSpawned) {
			demon = new Demon(GameEngine.getViewWidth()/2, GameEngine.getViewHeight()/2);
			demon.setMultiplier(multiplier);
			enemies.add(demon);
			demonSpawned = true;
			demonAccumulator = 0;
		}
	}
}
