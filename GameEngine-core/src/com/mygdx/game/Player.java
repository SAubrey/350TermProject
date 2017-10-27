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
	
	/** Window to viewport dimension ratio.*/
	private final int scale = GameEngine.SCALE;
	
	/** Y value in pixels.*/
	private int windowHeight = GameEngine.getWinHeight();
	
	/** X value in pixels.*/
	private int windowWidth = GameEngine.getWinWidth();
	
	/** Circular player size in meters.*/
	private float playerRadius = GameEngine.getPlayRadius();
	
	/** Scaled Y value of the window. */
	private float viewportHeight = scale(windowHeight);
	
	/** Scaled X value of the window. */
	private float viewportWidth = scale(windowWidth);
	
	/** Velocity limit. */
	private float maxVelocity = 50;
	
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
	@SuppressWarnings("unused")
	private Fixture fixture;
	
	/** All projectiles fired from Player. */
	private ArrayList<Projectile> projectiles;
	
	/** All deletable Player projectiles. */
	private ArrayList<Body> deletableBodies;
	
	/**
	 * 
	 */
	public Player() {
		body = new Circle();
		bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.position.set(viewportWidth / 2, viewportHeight / 2);
		solidBody = GameEngine.getWorld().createBody(bodyDef);
		circle = new CircleShape();
		circle.setRadius(playerRadius);
		
		fixtureDef = new FixtureDef();
		fixtureDef.shape = circle;
		fixtureDef.density = 0.2f; 
		fixtureDef.friction = 0.4f;
		fixtureDef.restitution = 0.8f; // bounciness
		
		fixture = solidBody.createFixture(fixtureDef);
		projectiles = new ArrayList<Projectile>();
		deletableBodies = new ArrayList<Body>();
	}
	
	/**
	 * Creates a new Projectile object and gives it the Player's 
	 * position and the cursor coordinates to which it will be directed.
	 * @param mouseX the horizontal pixel position.
	 * @param mouseY the vertical pixel position.
	 */
	public void fireProjectile(final float mouseX, final float mouseY) {
		Projectile p = new Projectile(body.x, body.y, mouseX, mouseY);
		projectiles.add(p);
	}
	
	/**
	 * Collects each projectile fired from the Player that can be deleted
	 * and returns this to GameEngine.
	 * @param time time between frames.
	 * @return ArrayList of physical bodies to be deleted.
	 */
	public ArrayList<Body> manageProjectiles(final float time) {
		deletableBodies.clear();
		
		if (!projectiles.isEmpty()) {
			for (int i = 0; i < projectiles.size(); i++) {
				projectiles.get(i).simulateResistance();
				if (projectiles.get(i).deletable(time)) { 
					deletableBodies.add(projectiles.get(i).getBody());
					projectiles.remove(i);
				}
			}
		}
		return deletableBodies;
	}
	
	/**
	 * When a directional button is not being pressed,
	 * and the velocity of that direction is not zero,
	 * then accelerate negatively to cancel velocity even while accelerating
	 * in other directions.
	 * 
	 * @param caseNum direction acceptable for negative acceleration.
	 */
	public void simulateResistance(final int caseNum) {
		
		/** Current X velocity. */
		float xVelocity = solidBody.getLinearVelocity().x;
		/** Current Y velocity. */
		float yVelocity = solidBody.getLinearVelocity().y;
		
		if (caseNum == 1 && xVelocity < 0)  {
			solidBody.applyForceToCenter(8.0f, 0, true);
		} else if (caseNum == 2 && xVelocity > 0) {
			solidBody.applyForceToCenter(-8.0f, 0, true);
		} 
		
		if (caseNum == 3 && yVelocity > 0) {
			solidBody.applyForceToCenter(0, -8.0f, true);
		} else if (caseNum == 4 && yVelocity < 0)  {
			solidBody.applyForceToCenter(0, 8.0f, true);
		}
	}
	
	/**
	 * For every render, detects if directional velocity is greater than
	 * the maximum velocity and sets it at the max.
	 */
	public void velocityCap() {
		
		/**  */
		float xVelocity = solidBody.getLinearVelocity().x;
		/**  */
		float yVelocity = solidBody.getLinearVelocity().y;
		
		if (xVelocity > maxVelocity) {
			solidBody.setLinearVelocity(maxVelocity, yVelocity);
		} else if (xVelocity < -maxVelocity) {
			solidBody.setLinearVelocity(-maxVelocity, yVelocity);
		}
		
		if (yVelocity > maxVelocity) {
			solidBody.setLinearVelocity(xVelocity, maxVelocity);
		} else if (yVelocity < -maxVelocity) {
			solidBody.setLinearVelocity(xVelocity, -maxVelocity);
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
	
	/*
	 * Attempted fix of movement bugs. Almost works.
	 */
	/*
	public void move(boolean right, boolean left, boolean up, boolean down) {
		float halfVel = maxVelocity/2;
		if (left && !up && !down) {
			solidBody.applyForceToCenter(-maxVelocity, 0, true);
		}
		else if (right && !up && !down) {
			solidBody.applyForceToCenter(maxVelocity, 0, true);
		}
		else if (up && !right && !left) {
			solidBody.applyForceToCenter(0, maxVelocity, true);
		}
		else if (down && !right && !left) {
			solidBody.applyForceToCenter(0, -maxVelocity, true);
		}
		else if (left && (up || down)) {
			solidBody.applyForceToCenter(-halfVel, 0, true);
			if (up) {
				solidBody.applyForceToCenter(0, halfVel, true);
			}
			else if (down) {
				solidBody.applyForceToCenter(0, -halfVel, true);
			}
		}
		else if (right && (up || down)) {
			solidBody.applyForceToCenter(halfVel, 0, true);
			if (up) {
				solidBody.applyForceToCenter(0, halfVel, true);
			}
			else if (down) {
				solidBody.applyForceToCenter(0, -halfVel, true);
			}
		}
	}
	*/
	
	/**
	 * Sets graphical object's position to a physical body's position.
	 */
	public void setPos() {
		solidBody.setUserData(body);
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
	
	/**
	 * Scales pixel dimensions to the camera's viewport size so that physical
	 * object dimensions can be declared in units of meters to function 
	 * correctly within Box2D. In other words,
	 * rather than scaling the objects to 
	 * the window size, we scale the window size to the object.
	 * 
	 * @param val window dimension
	 * @return viewport dimension
	 */
	public float scale(final float val) {
		return val / scale;
	}
}

