package com.mygdx.game;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

/**
 * Projectile class creates its own body and calculates 
 * its velocity relative to the cursor click and the 
 * Player's position. After a certain time it will signify
 * that it is ready for deletion, however it cannot delete
 * itself and so must pass this information back up to the
 * GameEngine so that it can delete it.
 * @author Sean Aubrey, Gabriel Fountain, Brandon Conn
 */
public class Projectile {
	
	/**  Interval in seconds before Projectile triggers despawn.*/
	public final float despawnTime = 2.0f;
	
	/** Scaled Y value of the window. */
	public int viewportHeight;
	
	/**  Velocity limit.*/
	public int maxVelocity;
	
	public float damage;
    
	/**  Size of physical body.*/
	public float size;
    
	/**  Projectile source's X position.*/
	public float sourceX;
    
	/**  Projectile source's Y position.*/
	public float sourceY;
	
	/**  Accumulates time between frames. */
	public float accumulator;
	
	public float targetX;
	
	public float targetY;
	
	public float dX;
	
	public float dY;
	
	public float slope;
	
	public float displacement;
	
	/**  X and Y velocity.*/
	public Vector2 vel;
	
	/**  Temporary position vector.*/
	public Vector2 vec;
	
	/**  X and Y values for where Projectiles spawns relative to Player.*/
	public Vector2 quad;
	
	/**  Circle shape.*/
	public Circle body;
	
	/**  Characteristics of physical body.*/
	public BodyDef bodyDef;
	
	/**  Physical body.*/
	public Body solidBody;
	
	/**  Box2D circle shape.*/
	public CircleShape circle;
	
	/**  Characteristics of fixture.*/
	public FixtureDef fixtureDef;
	
	/**  Attaches a physical body to its qualities.*/
	@SuppressWarnings("unused")
	public Fixture fixture;
	
	/**
	 * Constructor that assigns passed values locally that will be used
	 * to calculate trajectory. Creates graphical and physical body objects.
	 * @param playerX player's X coordinate in pixels.
	 * @param playerY player's Y coordinate in pixels.
	 * @param mouseX cursor's X coordinate in pixels.
	 * @param mouseY cursor's Y coordinate in pixels.
	 */
	public Projectile(final float sourceX, final float sourceY,
			final float targetX, final float targetY, float bulletDamage) {
		this.targetX = targetX;
		this.targetY = targetY;
		this.sourceX = sourceX;
		this.sourceY = sourceY;
		damage = bulletDamage;
		dX = targetX - sourceX;
		dY = targetY - sourceY;
		viewportHeight = GameEngine.getViewHeight();
		size = GameEngine.getProjRadius();
		/*
		body = new Circle();
		bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		vec = new Vector2(determineQuadrant());
		bodyDef.position.set(sourceX + vec.x, sourceY + vec.y);
		solidBody = GameEngine.getWorld().createBody(bodyDef);// problem here???
		solidBody.setUserData("");
		circle = new CircleShape();
		circle.setRadius(size);
		
		fixtureDef = new FixtureDef();
		fixtureDef.shape = circle;
		fixtureDef.density = 0.1f; 
		fixtureDef.friction = 0.4f;
		fixtureDef.restitution = 0.8f; // bounciness
		fixture = solidBody.createFixture(fixtureDef);
		fixture.setUserData(this);

		calculateVelocity();
		*/
	}

	/**
	 * Takes the differences in X and Y between the cursor click location and the player
	 * to calculate how much X and Y velocities should be used with respect to 
	 * the Projectile's maximum velocity in order to angle the trajectory accurately.
	 * Formula to determine X + Y velocities: BF(n) = B(n/(n+1)) + B(1/(n+1)),
	 * where n is X/Y slope and B is max velocity.
	 */
	public void calculateVelocity() {
		vel = new Vector2();
		slope = Math.abs(dX / dY);
		if (dX > 0) {
			if (dY >= 0) {
				vel.x = maxVelocity * (slope / (slope + 1));
				vel.y = maxVelocity * (1 / (slope + 1));
			} else if (dY < 0) {
				vel.x = maxVelocity * (slope / (slope + 1));
				vel.y = -maxVelocity * (1 / (slope + 1));
			}
		} else if (dX < 0) {
			if (dY >= 0) {
				vel.x = -maxVelocity * (slope / (slope + 1));
				vel.y = maxVelocity * (1 / (slope + 1));
			} else if (dY < 0) {
				vel.x = -maxVelocity * (slope / (slope + 1));
				vel.y = -maxVelocity * (1 / (slope + 1));
			}
		}
		//Used for debug
		/*
		System.out.println(" pX: " + playerX + " dX: " 
		+ dX + " pY: " + playerY +  " dY: " + dY + " slope: " +
		slope + " xVel: " + vel.x + " yVel: " + vel.y);
		*/
		solidBody.setLinearVelocity(vel);
	}
	
	/**
	 * Determines which corner of the player to spawn the projectile to avoid
	 * intersecting with the player.
	 * @return vector signifying quadrant of player.
	 */
	public Vector2 determineQuadrant() {
		displacement = 0.1f;
		quad = new Vector2();
		if (dX > 0) {
			if (dY > 0) {
				quad.x = displacement;
				quad.y = displacement;
			} else if (dY < 0) {
				quad.x = displacement;
				quad.y = -displacement;
			}
		} else if (dX < 0) {
			if (dY > 0) {
				quad.x = -displacement;
				quad.y = displacement;
			} else if (dY < 0) {
				quad.x = -displacement;
				quad.y = -displacement;
			}
		}
		return quad;
	}
	
	/**
	 * Applies acceleration against the velocity of the projectile
	 * to slow it down. 
	 */
	/*
	public void simulateResistance() {
		float xVelocity = solidBody.getLinearVelocity().x;
		float yVelocity = solidBody.getLinearVelocity().y;
		float resistance = 3f;
		
		if (xVelocity < 0)  {
			solidBody.applyForceToCenter(resistance, 0, true);
		} else if (xVelocity > 0) {
			solidBody.applyForceToCenter(-resistance, 0, true);
		} 
		
		if (yVelocity > 0) {
			solidBody.applyForceToCenter(0, -resistance, true);
		} else if (yVelocity < 0)  {
			solidBody.applyForceToCenter(0, resistance, true);
		}
	}*/
	
	/**
	 * Sets graphical object's position to a physical body's position.
	 */
	public void setPos() {
		body.setPosition(solidBody.getPosition());
	}
	
	/**
	 * Accumulates time until the despawn time is reached.
	 * @param time difference between frames.
	 */
	public boolean deletable(final float time) {
		accumulator += time;
		
		if (accumulator >= despawnTime) {
			//GameEngine.getWorld().destroyBody(solidBody);
			solidBody.setUserData("deletable");
			return true;
		}
		return false;
	}
	
	public void setDeletable() {
		accumulator = despawnTime;
	}
	
	/**
	 * Returns physical body.
	 * @return projectile's physical body.
	 */
	public Body getBody() {
		return solidBody;
	}
	
	public float getBulletDamage() {
		return damage;
	}
}

