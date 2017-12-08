package com.mygdx.game; 

import java.util.ArrayList;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
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
	private float playerAcceleration = 30.0f; //30->79
	
	/** Amount of damage Player can take */
	private float health = 100f;//100->1000
	
	/** Amount of damage of Player projectiles. */
	private float bulletDamage = 10f;
	
	/** Number of enemies defeated. */
	private int killCount;
	
	/** Killcount with respect to value of each enemy. */
	private int score;
	
	/**  Time between shots in seconds.*/
	private float shotTime = 0.8f; //0.8->0.05
	
	/**  Time between shots in seconds.*/
	private float shotgunTime = 1.0f; //1.0->0.05
	
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
		solidBody.setUserData("player"); //user data is any data type, for any purpose
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
	
	public void update(final boolean right, final boolean left, 
			final boolean up, final boolean down, final float dT) {
		setPos();
		applyForce(move(right, left, up, down));
		applyForce(simulateResistance(getVelocity()));
		setVelocity(velocityCap(getVelocity()));
		manageProjectiles(dT);
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
	
	/**
	 * 
	 * @param mouseX
	 * @param mouseY
	 */
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
	private Vector2 simulateResistance(Vector2 v) {
		Vector2 vec = new  Vector2();
		float xResistance = 1 + Math.abs(v.x / 5);
		float yResistance = 1 + Math.abs(v.y / 5);
		if (v.x < 0)  {
			vec.set(xResistance, vec.y);
		} else if (v.x > 0) {
			vec.set(-xResistance, vec.y);
		} 
		if (v.y > 0) {
			vec.set(vec.x, -yResistance);
		} else if (v.y < 0)  {
			vec.set(vec.x, yResistance);
		}
		return vec;
	}
	
	/**
	 * For every render, detects if directional velocity is greater than
	 * the maximum velocity and sets it at the max.
	 * 
	 * Check again maximum ratios of the max velocity given the current ratio of velocities
	 */
	private Vector2 velocityCap(Vector2 currentV) {
		float xVelocity = currentV.x;
		float yVelocity = currentV.y;
		
		float sum = Math.abs(xVelocity) + Math.abs(yVelocity);
		float xRat = xVelocity / sum;
		float yRat = yVelocity / sum;
		float xCap = Math.abs(xRat * maxVelocity);
		float yCap = Math.abs(yRat * maxVelocity);
		Vector2 v = new Vector2(xVelocity, yVelocity);
		if (xVelocity > xCap) {
			v.x = xCap;
		} else if (xVelocity < -xCap) {
			v.x = -xCap;
		}
		if (yVelocity > yCap) {
			v.y = yCap;
		} else if (yVelocity < -yCap) {
			v.y = -yCap;
		}
		return v;
	}

	 //Attempted fix of movement bugs. Almost works.
	 //all this really does is even out acceleration diagonally - not cap it

	/**
	 * 
	 * @param right
	 * @param left
	 * @param up
	 * @param down
	 */
	private Vector2 move(final boolean right, final boolean left,
			final boolean up, final boolean down) {
		Vector2 force = new Vector2();
		float diagForce = (playerAcceleration * 2) / 3;
		if (left && !up && !down) {
			force.x = -playerAcceleration;
		} else if (right && !up && !down) {
			force.x = playerAcceleration;
		} else if (up && !right && !left) {
			force.y =  playerAcceleration;
		} else if (down && !right && !left) {
			force.y = -playerAcceleration;
		} else if (left && (up || down)) {
			if (up) {
				force.set(-diagForce, diagForce);
			} else if (down) {
				force.set(-diagForce, -diagForce);
			}
		} else if (right && (up || down)) {
			if (up) {
				force.set(diagForce, diagForce);
			} else if (down) {
				force.set(diagForce, -diagForce);
			}
		}
		return force;
	}
	
	/**
	 * Sets graphical object's position to a physical body's position.
	 */
	public void setPos() {
		body.setPosition(solidBody.getPosition());
	}
	
	public Vector2 getVelocity() {
		return solidBody.getLinearVelocity();
	}
	
	public void setVelocity(Vector2 vel) {
		solidBody.setLinearVelocity(vel);
	}
	
	public void applyForce(Vector2 force) {
		solidBody.applyForceToCenter(force, true);
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
	 * 
	 * @param damage
	 */
	public void takeDamage(final float damage) {
		health -= damage;
	}
	
	/**
	 * 
	 * @return
	 */
	public float getHealth() {
		return health;
	}
	
	/**
	 * 
	 */
	public void incrementKillCount() {
		killCount++;
		if (health != 100) {
			health++;
		}
		score++;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getKillCount() {
		return killCount;
	}
	
	/**
	 * 
	 * @return
	 */
	public float getShotTime() {
		return shotTime;
	}
	
	/**
	 * 
	 * @return
	 */
	public float getShotgunTime() {
		return shotgunTime;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getScore() {
		return score;
	}
	
	/**
	 * 
	 * @param mult
	 */
	public void multMovement(final float mult) {
		maxVelocity *= mult;
		playerAcceleration *= mult;
		if (shotTime > .05f) {
			shotTime -= mult * .01f;
			shotgunTime -= mult * .02f;
		}
	}
}
