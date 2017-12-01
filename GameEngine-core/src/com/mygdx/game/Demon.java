package com.mygdx.game;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class Demon extends Enemy {
	
	private int bulletDamage;
	
	private float deltaTime;
	
	private float spitAccumulator;
	
	private float chargeAccumulator;
	
	private float preChargeAccumulator;
	
	private boolean charging = false;
	
	private boolean charged = false;
	
	private float spitInterval = 1.0f;
	
	private float chargeTime = 5.0f;
	
	private float preChargeTime = 0.6f;
	
	private float endChargeTime;
	
	private float multiplier;
	
	private float initialHealth;
	
	public Demon(float spawnX, float spawnY) {
		super(spawnX, spawnY);
		maxVelocity = 50f;
		health = 1000f;
		initialHealth = health;
		bodyDamage = 25f;
		bulletDamage = 15;
		endChargeTime = preChargeTime + 1.5f;
		
		body = new Circle();
		bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.position.set(spawnX, spawnY); // determine spawn operation
		solidBody = GameEngine.getWorld().createBody(bodyDef);
		solidBody.setUserData("demon");
		circle = new CircleShape();
		circle.setRadius(GameEngine.getDemonRadius());
		
		fixtureDef = new FixtureDef();
		fixtureDef.shape = circle;
		fixtureDef.density = 0.9f; 
		fixtureDef.friction = 0.4f;
		fixtureDef.restitution = 0.5f; // bounciness
		fixture = solidBody.createFixture(fixtureDef);
		fixture.setUserData(this);
	}
	
	@Override
	public void update(float x, float y) {
		playerX = x;
		playerY = y;
		body.setPosition(solidBody.getPosition());
		position = solidBody.getPosition();
		
		deltaTime = GameEngine.getDeltaTime();
		accumulator += deltaTime;
		spitAccumulator += deltaTime;
		chargeAccumulator += deltaTime;
		manageProjectiles(deltaTime);
		checkEnraged();
		checkFire();
	}
	
	private void checkFire() {
		if (spitAccumulator >= spitInterval && !charging) {
			standardFire();
			spitAccumulator = 0;
		}
	}
	
	private void standardFire() {
		spit(playerX, playerY + 20);
		spit(playerX, playerY);
		spit(playerX - 20, playerY);
	}
	
	/**
	 * This executes while charging is true. 
	 */
	private void charge() {
		if (!charged) { // halt motion before charge
			solidBody.setLinearVelocity(0, 0);			
		}
		preChargeAccumulator += deltaTime;
		if (preChargeAccumulator >= (preChargeTime - (multiplier * 0.1f))) { // charge
			maxVelocity = 200 * (multiplier + (multiplier * 0.4f));
			if (multiplier >= 1.4f) {
				calculateVelocity();
			}
			if (!charged) {
				calculateVelocity();
				charged = true;
			}
		}
		if (preChargeAccumulator >= endChargeTime) { // after charge
			solidBody.setLinearVelocity(0, 0);
			maxVelocity = 50 * multiplier;
			preChargeAccumulator =  0;	
			chargeAccumulator = 0;
			charging = false;
			charged = false;
		}
		
	}
	
	private void checkEnraged() {
		if (health <= initialHealth * 0.6f) {
			spitInterval = 0.5f;
		}
		if (health <= initialHealth * 0.5f && !charging) {
			calculateVelocity();
		}
		if (health <= initialHealth * 0.35f && chargeAccumulator >= chargeTime) {
			charging = true;
			charge();
		}
	}
	
	/**
	 * Creates a new Projectile object and gives it the Player's 
	 * position and the cursor coordinates to which it will be directed.
	 * @param mouseX the horizontal pixel position.
	 * @param mouseY the vertical pixel position.
	 */
	public void spit(final float playerX, final float playerY) {
		EnemyProjectile p = new EnemyProjectile(getX(), getY(), playerX, playerY, bulletDamage);
		projectiles.add(p);
	}
	/**
	 * Collects each projectile fired from the Demon that can be deleted.
	 * @param time time between frames.
	 */
	public void manageProjectiles(final float time) {
		if (!projectiles.isEmpty()) {
			for (int i = 0; i < projectiles.size(); i++) {
				if (projectiles.get(i).deletable(time)) { 
					projectiles.remove(i);
				}
			}
		}
	}
	
	@Override
	public boolean setDeletable() {
		purgeProjectiles();
		solidBody.setUserData("deletable");
		return true;
	}
	
	public void setMultiplier(float mult) {
		multiplier = mult;
		health *= mult;
	}
	
	/**
	 * Returns body's horizontal position.
	 * @return physical body's X.
	 */
	public float getX() {
		return body.x;
	}
	
	/**
	 * Returns body's vertical position.
	 * @return physical body's Y.
	 */
	public float getY() {
		return body.y;
	}
}
