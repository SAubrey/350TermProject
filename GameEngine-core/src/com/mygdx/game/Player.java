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
 * @author Sean Aubrey, Gabriel Fountain, Brandon Conn 
 *
 */
public class Player {
	
	/** */
	private final int scale = GameEngine.SCALE;
	
	/** */
	private int windowHeight = GameEngine.windowHeight;
	
	/** */
	private int windowWidth = GameEngine.windowWidth;
	
	/** */
	private int playerRadius = GameEngine.playerRadius;
	
	/** */
	private float viewportHeight = scale(windowHeight);
	
	/** */
	private float viewportWidth = scale(windowWidth);
	
	/** */
	private float maxVelocity = 50;
	
	/** */
	private Circle body;
	
	/** */
	private BodyDef bodyDef;
	
	/** */
	private Body solidBody;
	
	/** */
	private CircleShape circle;
	
	/** */
	private FixtureDef fixtureDef;
	
	/** */
	@SuppressWarnings("unused")
	
	/** */
	private Fixture fixture;
	
	/** */
	private ArrayList<Projectile> projectiles; // List of all existing projectiles
	
	/** */
	private ArrayList<Body> deletableBodies;
	
	/**
	 * 
	 */
	public Player() {
		body = new Circle();
		bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.position.set(viewportWidth / 2, viewportHeight / 2);
		solidBody = GameEngine.world.createBody(bodyDef);
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
	 * @param mouseX
	 * @param mouseY
	 */
	public void fireProjectile(final float mouseX, final float mouseY) {
		Projectile p = new Projectile(body.x, body.y, mouseX, mouseY);
		projectiles.add(p);
	}
	
	/**
	 * @param time
	 * @return
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
	 * Called in GameEngine. 
	 * When a directional button is not being pressed,(indicated by caseNum), 
	 * and the velocity of that direction is not zero,
	 * then accelerate negatively to cancel velocity even while accelerating
	 * in other directions.
	 * 
	 * @param caseNum
	 */
	public void simulateResistance(final int caseNum) {
		
		/**  */
		float xVelocity = solidBody.getLinearVelocity().x;
		/**  */
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
	 * @param direction
	 */
	public void moveVertical(final float direction) {
		solidBody.applyForceToCenter(0, direction, true);
	}
	
	/**
	 * @param direction
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
	 * 
	 */
	public void setPos() {
		solidBody.setUserData(body);
		body.setPosition(solidBody.getPosition());
	}
	
	/**
	 * @return
	 */
	public float getX() {
		return body.x;
	}
	
	/**
	 * @return
	 */
	public float getY() {
		return body.y;
	}
	
	/**
	 * @param val
	 * @return
	 */
	public float scale(final float val) {
		return val / scale;
	}
}

