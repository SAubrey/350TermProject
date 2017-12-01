package com.mygdx.game;

import java.util.ArrayList;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class Spitter extends Enemy{
	
	private int bulletDamage;
	
	private float spitAccumulator;
	
	private float spitInterval = 3.0f;
	
	public Spitter(float spawnX, float spawnY) {
		super(spawnX, spawnY);
		//maxVelocity = 100f;
		health = 30f;
		bodyDamage = 15f;
		bulletDamage = 15;
		
		body = new Circle();
		bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.position.set(spawnX, spawnY); // determine spawn operation
		solidBody = GameEngine.getWorld().createBody(bodyDef);
		solidBody.setUserData("spitter");
		circle = new CircleShape();
		circle.setRadius(GameEngine.getSpitterRadius());
		
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
		accumulator += GameEngine.getDeltaTime();
		spitAccumulator += GameEngine.getDeltaTime();
		manageProjectiles(GameEngine.getDeltaTime());
		position = solidBody.getPosition();
		if (spitAccumulator >= spitInterval) {
			spit(playerX, playerY);
			spitAccumulator = 0;
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
	 * Collects each projectile fired from the Spitter that can be deleted.
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
