package com.mygdx.game;

import java.util.ArrayList;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;

/**
 * Enemy class acts as the parent class for all Enemy 
 * children. 
 *
 */
public class Enemy {
	
	/**  Velocity limit.*/
	public float maxVelocity;
	
	/** Circle shape. */
	public Circle body;
	
	/** Characteristics of physical body.*/
	public BodyDef bodyDef;
	
	/** Physical body. */
	public Body solidBody;
	
	/** Box2D circle shape. */
	public CircleShape circle;
	
	/** Characteristics of fixture. */
	public FixtureDef fixtureDef;
	
	/** Attaches a physical body to its qualities. */
	@SuppressWarnings("unused")
	public Fixture fixture;
	
	public Vector2 position;
	
	public Vector2 vel;
	
	/** All projectiles fired from Enemy. */
	public ArrayList<EnemyProjectile> projectiles;
	
	/**  Player's X position.*/
	public float playerX;
    
	/**  Player's Y position.*/
	public float playerY;
	
	public float health;
	
	public float bodyDamage;
	
	public float accumulator;
	
	public Enemy(float spawnX, float spawnY) {
		position = new Vector2(spawnX, spawnY);
		accumulator = 1.0f;
		projectiles = new ArrayList<EnemyProjectile>();
		/*
		body = new Circle();
		bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.position.set(spawnX, spawnY); // determine spawn operation
		solidBody = GameEngine.getWorld().createBody(bodyDef);
		solidBody.setUserData("enemy");
		circle = new CircleShape();
		circle.setRadius(size);
		
		fixtureDef = new FixtureDef();
		fixtureDef.shape = circle;
		fixtureDef.density = 0.1f; 
		fixtureDef.friction = 0.4f;
		fixtureDef.restitution = 0.8f; // bounciness
		fixture = solidBody.createFixture(fixtureDef);
		*/
	}
	
	public void update(float x, float y) {
		playerX = x;
		playerY = y;
		accumulator += GameEngine.getDeltaTime();
	}
	
	public void pushAway() {
		float x = solidBody.getLinearVelocity().x;
		float y = solidBody.getLinearVelocity().y;
		accumulator = 0;
		float xBurst = 90f;
		float yBurst = 90f;
		if (x > 0) {
			x = -xBurst;
			if (y > 0) {
				y = -yBurst;
			} else if (y < 0) {
				y = yBurst;
			}
		} else if (x <= 0) {
			x = xBurst;
			if (y > 0) {
				y = -yBurst;
			} else if (y <=0 ) {
				y = yBurst;
			}
		}
		solidBody.applyLinearImpulse(x, y, 0, 0, true);
	}
	
	public void calculateVelocity() {
		if (accumulator > 1.0f) {
			vel = new Vector2();
			float dX, dY, slope;
			dX = playerX - position.x;
			dY = playerY - position.y;
			slope = Math.abs(dX / dY);
			if (dX > 0) {
				if (dY >= 0) {
					vel.x = maxVelocity * (slope / (slope + 1));
					vel.y = maxVelocity * (1 / (slope + 1));
				}
				if (dY < 0) {
					vel.x = maxVelocity * (slope / (slope + 1));
					vel.y = -maxVelocity * (1 / (slope + 1));
				}
			} else if (dX < 0) {
				if (dY >= 0) {
					vel.x = -maxVelocity * (slope / (slope + 1));
					vel.y = maxVelocity * (1 / (slope + 1));
				}
				if (dY < 0) {
					vel.x = -maxVelocity * (slope / (slope + 1));
					vel.y = -maxVelocity * (1 / (slope + 1));
				}
			}
			solidBody.setLinearVelocity(vel);
		}
	}
	
	public boolean takeDamage(float damage) {
		health -= damage;
		if (health <= 0) {
			return true;
		}
		return false;
	}
	
	/**
	 * Clears world of projectiles shot by this enemy before deletion of the enemy.
	 */
	public void purgeProjectiles() {
		for (EnemyProjectile x : projectiles) {
			x.deletable(10);
			// use this instead of transferring over array of projectiles to enemy manager with setDeletable()
		}
	}
	
	public float getHealth() {
		return health;
	}
	
	public float getBodyDamage() {
		return bodyDamage;
	}
	
	public boolean setDeletable() {
		purgeProjectiles();
		solidBody.setUserData("deletable");
		return false;
	}
	
	public void multMaxVelocity(float mult) {
		maxVelocity *= mult;
	}
	
	public ArrayList<EnemyProjectile> getProjectiles() {
		return projectiles;
	}
}
