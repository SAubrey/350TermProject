package com.mygdx.game; 

/**
 * Big red boss monster, fires in rapid bursts of 3 projectiles, 
 * chases the player and then periodically charges towards the player.
 * @author Sean Aubrey, Gabriel Fountain, Brandon Conn
 *
 */
public class Demon extends Enemy {
	
	/**  Duration between game frames. */
	private float deltaTime;
	
	/** Time between firing projectiles.  */
	private float spitAccumulator;
	
	/** Time between charges.  */
	private float chargeAccumulator;
	
	/**  Time before charge. */
	private float preChargeAccumulator;
	
	/** True if in the process of charging.  */
	private boolean charging = false;
	
	/** True if the demon has charged.  */
	private boolean charged = false;
	
	/**  Time duration between firing projectiles. */
	private float spitInterval = 1.0f;
	
	/**  Time before next charge. */
	private float chargeTime = 5.0f;
	
	/**  Time demon is stationary before a charge. */
	private float preChargeTime = 0.6f;
	
	/** Time when a charge ends. */
	private float endChargeTime;
	
	/** Quantity other values are scaled by.  */
	private float multiplier;
	
	/** Health at start.  */
	private float initialHealth;
	
	/**
	 * Demon sets object specific variables then uses parent class for creation.
	 * @param spawnX X spawn coordinate
	 * @param spawnY Y spawn coordinate
	 */
	public Demon(final float spawnX, final float spawnY) {
		super(spawnX, spawnY);
		setMaxVelocity(50f);
		setHealth(1000f);
		initialHealth = getHealth();
		setBodyDamage(25f);
		setBulletDamage(15);
		endChargeTime = preChargeTime + 1.5f;

		buildBody(spawnX, spawnY, GameEngine.getDemonRadius(), 0.9f, 0.5f);
		getBody().setUserData("demon");
		getFixture().setUserData(this);
		
	}
	
	/**
	 * 
	 * Updates.
	 * @param x player's x coordinate
	 * @param y player's y coordinate
	 */
	@Override
	public void update(final float x, final float y) {
		super.update(x, y);
		
		deltaTime = GameEngine.getDeltaTime();
		spitAccumulator += deltaTime;
		chargeAccumulator += deltaTime;
		manageProjectiles(deltaTime);
		checkEnraged();
		checkFire();
	}
	
	/**
	 * Fire projectiles if the time has come and the 
	 * demon isn't charging.
	 */
	private void checkFire() {
		if (spitAccumulator >= spitInterval && !charging) {
			standardFire();
			spitAccumulator = 0;
		}
	}
	
	/**
	 * Shoot three projectiles in slightly offset directions.
	 */
	private void standardFire() {
		spit(getPlayerX(), getPlayerY() + 20);
		spit(getPlayerX(), getPlayerY());
		spit(getPlayerX() - 20, getPlayerY());
	}
	
	/**
	 * This executes while charging is true. 
	 */
	private void charge() {
		if (!charged) { // halt motion before charge
			getBody().setLinearVelocity(0, 0);			
		}
		preChargeAccumulator += deltaTime;
		if (preChargeAccumulator >= (preChargeTime - (multiplier * 0.1f))) { // charge
			setMaxVelocity(200 * (multiplier + (multiplier * 0.4f)));
			if (multiplier >= 1.4f) {
				getBody().setLinearVelocity(calculateVelocity());
			}
			if (!charged) {
				getBody().setLinearVelocity(calculateVelocity());
				charged = true;
			}
		}
		if (preChargeAccumulator >= endChargeTime) { // after charge
			getBody().setLinearVelocity(0, 0);
			setMaxVelocity(50 * multiplier);
			preChargeAccumulator =  0;	
			chargeAccumulator = 0;
			charging = false;
			charged = false;
		}
		
	}
	
	/**
	 * 
	 */
	private void checkEnraged() {
		if (getHealth() <= initialHealth * 0.6f) {
			spitInterval = 0.5f;
		}
		if (getHealth() <= initialHealth * 0.5f && !charging) {
			getBody().setLinearVelocity(calculateVelocity());
		}
		if (getHealth() <= initialHealth * 0.35f && chargeAccumulator >= chargeTime) {
			charging = true;
			charge();
		}
	}
	
	
	/**
	 * 
	 */
	@Override
	public boolean setDeletable() {
		purgeProjectiles();
		getBody().setUserData("deletable");
		return true;
	}
	
	/**
	 * 
	 * @param mult multiplier
	 */
	public void setMultiplier(final float mult) {
		multiplier = mult;
		multHealth(mult);
	}
}
