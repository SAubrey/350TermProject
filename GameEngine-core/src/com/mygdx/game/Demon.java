package com.mygdx.game;

/**
 * 
 * @author Sean Aubrey, Gabriel Fountain, Brandon Conn
 *
 */
public class Demon extends Enemy {
	
	/**   */
	private float deltaTime;
	
	/**   */
	private float spitAccumulator;
	
	/**   */
	private float chargeAccumulator;
	
	/**   */
	private float preChargeAccumulator;
	
	/**   */
	private boolean charging = false;
	
	/**   */
	private boolean charged = false;
	
	/**   */
	private float spitInterval = 1.0f;
	
	/**   */
	private float chargeTime = 5.0f;
	
	/**   */
	private float preChargeTime = 0.6f;
	
	/**   */
	private float endChargeTime;
	
	/**   */
	private float multiplier;
	
	/**   */
	private float initialHealth;
	
	/**
	 * 
	 * @param spawnX
	 * @param spawnY
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
	 * 
	 * @param x
	 * @param y
	 */
	@Override
	public void update(final float x, final float y) {
		setPlayerX(x);
		setPlayerY(y);
		getShapeBody().setPosition(getBody().getPosition());
		setPosition(getBody().getPosition());
		
		deltaTime = GameEngine.getDeltaTime();
		incAccumulator(GameEngine.getDeltaTime());
		spitAccumulator += deltaTime;
		chargeAccumulator += deltaTime;
		manageProjectiles(deltaTime);
		checkEnraged();
		checkFire();
	}
	
	/**
	 * 
	 */
	private void checkFire() {
		if (spitAccumulator >= spitInterval && !charging) {
			standardFire();
			spitAccumulator = 0;
		}
	}
	
	/**
	 * 
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
				calculateVelocity();
			}
			if (!charged) {
				calculateVelocity();
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
			calculateVelocity();
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
	 * @param mult
	 */
	public void setMultiplier(final float mult) {
		multiplier = mult;
		multHealth(mult);
	}
}
