package com.mygdx.game;

import java.util.ArrayList;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;

/**
 * Player class instantiates once per play session. 
 * It creates its own body.
 * It keeps track of and manages the projectiles it shoots.
 * to facilitate their deletion.
 * @author Sean Aubrey, Gabriel Fountain, Brandon Conn
 */
public class Player {
	
	/** Circular player size in meters.*/
	private float playerRadius = GameEngine.getPlayRadius();
	
	/** Scaled Y value of the window. */
	private float viewportHeight = GameEngine.getViewHeight();
	
	/** Scaled X value of the window. */
	private float viewportWidth = GameEngine.getViewWidth();
	
	/** Velocity limit. */
	private float maxVelocity = 80f;
	
	/**  Applied acceleration upon movement in m/s^2.*/
	private float playerAcceleration = 30.0f;
	
	/**  */
	private float health = 100f;
	
	/**  */
	private float bulletDamage = 10f;
	
	/**  */
	private int killCount;
	
	/**  */
	private int score;
	
	/**  Time between shots in seconds.*/
	private float shotTime = 0.2f;
	
	/**  Time between shots in seconds.*/
	private float shotgunTime = 1f;
	
	/** Circle shape. */
	private Circle body;
	
	/** Characteristics of physical body.*/
	private BodyDef bodyDef;
	
	/** Physical body. */
	private Body solidBody;
	
	/** Box2D circle shape. */
	private CircleShape circle;
	
	/** Characteristics of fixture. */
	private FixtureDef fixtureDef;
	
	/** Attaches a physical body to its qualities. */
	
	private Fixture fixture;
	
	/** All projectiles fired from Player. */
	private ArrayList<PlayerProjectile> projectiles;
	
	/**
	 * 
	 */
	public Player() {
		body = new Circle();
		bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.position.set(viewportWidth / 2, viewportHeight / 2);
		solidBody = GameEngine.getWorld().createBody(bodyDef);
		solidBody.setUserData("player"); //user data is an arbitrary data type used for any purpose
		circle = new CircleShape();
		circle.setRadius(playerRadius);
		
		fixtureDef = new FixtureDef();
		fixtureDef.shape = circle;
		fixtureDef.density = 0.15f; 
		fixtureDef.friction = 0.4f;
		fixtureDef.restitution = 0.8f; // bounciness
		
		fixture = solidBody.createFixture(fixtureDef);
		fixture.setUserData(this);
		projectiles = new ArrayList<PlayerProjectile>();
	}
	
	/**
	 * Creates a new Projectile object and gives it the Player's 
	 * position and the cursor coordinates to which it will be directed.
	 * @param mouseX the horizontal pixel position.
	 * @param mouseY the vertical pixel position.
	 */
	public void fireProjectile(final float mouseX, final float mouseY) {
			PlayerProjectile p = new PlayerProjectile(getX(), getY(), mouseX, mouseY, bulletDamage);
			projectiles.add(p);
	}
	
	public void fireShotgun(final float mouseX, final float mouseY)  {
		PlayerProjectile a = new PlayerProjectile(getX(), getY(), mouseX, mouseY, bulletDamage);
		projectiles.add(a);
		PlayerProjectile b = new PlayerProjectile(getX(), getY(), mouseX + 10, mouseY + 10, bulletDamage);
		projectiles.add(b);
		PlayerProjectile c = new PlayerProjectile(getX(), getY(), mouseX + 4, mouseY + 4, bulletDamage);
		projectiles.add(c);
		PlayerProjectile d = new PlayerProjectile(getX(), getY(), mouseX - 4, mouseY - 4, bulletDamage);
		projectiles.add(d);
		PlayerProjectile e = new PlayerProjectile(getX(), getY(), mouseX - 10, mouseY - 10, bulletDamage);
		projectiles.add(e);
	}
	
	/**
	 * Collects each projectile fired from the Player that can be deleted
	 * and returns this to GameEngine.
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
	 * 
	 */
	public void simulateResistance() {
		/** Current X velocity. */
		float xVelocity = solidBody.getLinearVelocity().x;
		/** Current Y velocity. */
		float yVelocity = solidBody.getLinearVelocity().y;
		float xResistance = 1 + Math.abs(xVelocity / 5);
		float yResistance = 1 + Math.abs(yVelocity / 5);
		if (xVelocity < 0)  {
			solidBody.applyForceToCenter(xResistance, 0, true);
		} else if (xVelocity > 0) {
			solidBody.applyForceToCenter(-xResistance, 0, true);
		} 
		
		if (yVelocity > 0) {
			solidBody.applyForceToCenter(0, -yResistance, true);
		} else if (yVelocity < 0)  {
			solidBody.applyForceToCenter(0, yResistance, true);
		}
	}
	
	/**
	 * Applies vertical acceleration to a body.
	 * @param direction acceleration greater or less than zero.
	 */
	public void moveVertical(final float direction) {
		solidBody.applyForceToCenter(0, direction, true);
	}
	
	/**
	 * Applies horizontal acceleration to a body.
	 * @param direction acceleration greater or less than zero.
	 */
	public void moveHorizontal(final float direction) {
		solidBody.applyForceToCenter(direction, 0, true);
	}
	
	/**
	 * For every render, detects if directional velocity is greater than
	 * the maximum velocity and sets it at the max.
	 * 
	 * Check again maximum ratios of the max velocity given the current ratio of velocities
	 */
	public void velocityCap() {
		
		float xVelocity = solidBody.getLinearVelocity().x;
		float yVelocity = solidBody.getLinearVelocity().y;
		
		float sum = Math.abs(xVelocity) + Math.abs(yVelocity);
		float xRat = xVelocity / sum;
		float yRat = yVelocity / sum;
		float xCap = Math.abs(xRat * maxVelocity);
		float yCap = Math.abs(yRat * maxVelocity);
		float newX = xVelocity;
		float newY = yVelocity;
		if (xVelocity > xCap) {
			newX = xCap;
		} else if (xVelocity < -xCap) {
			newX = -xCap;
		}
		if (yVelocity > yCap) {
			newY = yCap;
		} else if (yVelocity < -yCap) {
			newY = -yCap;
		}
		solidBody.setLinearVelocity(newX, newY);
	}
	
	/*
	 * Attempted fix of movement bugs. Almost works.
	 * all this really does is even out acceleration diagonally - not cap it
	 */
	
	public void move(final boolean right, final boolean left, final boolean up, final boolean down) {
		float xVelocity = solidBody.getLinearVelocity().x;
		float yVelocity = solidBody.getLinearVelocity().y;
		float force = (playerAcceleration * 2) / 3;
		if ((Math.abs(xVelocity) < maxVelocity) && (Math.abs(yVelocity) < maxVelocity)) {
			if (left && !up && !down) {
				solidBody.applyForceToCenter(-playerAcceleration, 0, true);
			} else if (right && !up && !down) {
				solidBody.applyForceToCenter(playerAcceleration, 0, true);
			} else if (up && !right && !left) {
				solidBody.applyForceToCenter(0, playerAcceleration, true);
			} else if (down && !right && !left) {
				solidBody.applyForceToCenter(0, -playerAcceleration, true);
			} else if (left && (up || down)) {
				if (up) {
					solidBody.applyForceToCenter(-force, force, true);
				} else if (down) {
					solidBody.applyForceToCenter(-force, -force, true);
				}
			} else if (right && (up || down)) {
				if (up) {
					solidBody.applyForceToCenter(force, force, true);
				} else if (down) {
					solidBody.applyForceToCenter(force, -force, true);
				}
			}
		}
		simulateResistance();
		velocityCap();
	}
	
	/**
	 * Sets graphical object's position to a physical body's position.
	 */
	public void setPos() {
		body.setPosition(solidBody.getPosition());
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
	
	public void takeDamage(final float damage) {
		health -= damage;
	}
	
	public float getHealth() {
		return health;
	}
	
	public void incrementKillCount() {
		killCount++;
		if (health != 100) {
			health++;
		}
		score++;
	}
	
	public int getKillCount() {
		return killCount;
	}
	
	public float getShotTime() {
		return shotTime;
	}
	
	public float getShotgunTime() {
		return shotgunTime;
	}
	
	public int getScore() {
		return score;
	}
	
	public void multMovement(final float mult) {
		maxVelocity *= mult;
		playerAcceleration *= mult;
		if (shotTime > .05f) {
			shotTime -= mult * .01f;
			shotgunTime -= mult * .02f;
		}
	}
}
