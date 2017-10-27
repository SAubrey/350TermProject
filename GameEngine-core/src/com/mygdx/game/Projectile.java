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
	
	/**  Window to viewport dimension ratio.*/
	private final int scale = GameEngine.SCALE;
	
	/**  Interval in seconds before Projectile triggers despawn.*/
	private final float despawnTime = 3.0f;
	
	/**  Y value in pixels.*/
	private int windowHeight = GameEngine.windowHeight;
	
	/** Scaled Y value of the window. */
	private float viewportHeight = scale(windowHeight);
	
	/**  Velocity limit.*/
	private float maxVelocity = 70;
    
	/**  Size of physical body.*/
	private float size = 0.5f;
    
	/**  Player's X position.*/
	private float playerX;
    
	/**  Player's Y position.*/
	private float playerY;
	
	/**  Cursor click X position.*/
	private float mouseX;
	
	/**  Cursor click Y position.*/
	private float mouseY;
	
	/**  Accumulates time between frames. */
	private float accumulator;
	
	/**  X and Y velocity.*/
	private Vector2 vel;
	
	/**  Temporary position vector.*/
	private Vector2 vec;
	
	/**  X and Y values for where Projectiles spawns relative to Player.*/
	private Vector2 quad;
	
	/**  Circle shape.*/
	private Circle body;
	
	/**  Characteristics of physical body.*/
	private BodyDef bodyDef;
	
	/**  Physical body.*/
	private Body solidBody;
	
	/**  Box2D circle shape.*/
	private CircleShape circle;
	
	/**  Characteristics of fixture.*/
	private FixtureDef fixtureDef;
	
	/**  Attaches a physical body to its qualities.*/
	@SuppressWarnings("unused")
	private Fixture fixture;
	
	/**
	 * Constructor that assigns passed values locally that will be used
	 * to calculate trajectory. Creates graphical and physical body objects.
	 * @param playerX player's X coordinate in pixels.
	 * @param playerY player's Y coordinate in pixels.
	 * @param mouseX cursor's X coordinate in pixels.
	 * @param mouseY cursor's Y coordinate in pixels.
	 */
	public Projectile(final float playerX, final float playerY,
			final float mouseX, final float mouseY) {
		this.mouseX = scale(mouseX);
		this.mouseY = scale(mouseY);
		this.playerX = playerX;
		this.playerY = playerY;
		
		body = new Circle();
		bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		vec = new Vector2(determineQuadrant());
		bodyDef.position.set(playerX + vec.x, playerY + vec.y);
		solidBody = GameEngine.world.createBody(bodyDef);
		circle = new CircleShape();
		circle.setRadius(size);
		
		fixtureDef = new FixtureDef();
		fixtureDef.shape = circle;
		fixtureDef.density = 0.1f; 
		fixtureDef.friction = 0.4f;
		fixtureDef.restitution = 0.8f; // bounciness
		fixture = solidBody.createFixture(fixtureDef);
		
		calculateVelocity();
	}

	/**
	 * Takes the differences in X and Y between the cursor click location and the player
	 * to calculate how much X and Y velocities should be used with respect to 
	 * the Projectile's maximum velocity in order to angle the trajectory accurately.
	 * Formula to determine X + Y velocities: BF(n) = B(n/(n+1)) + B(1/(n+1)),
	 * where n is X/Y slope and B is max velocity.
	 */
	private void calculateVelocity() {
		vel = new Vector2();
		float dX, dY, slope;
		dX = mouseX - playerX;
		//System.out.println("(vpH: " + viewportHeight + " - mouseY: " + mouseY + ")
		//- pY: " + playerY);
		dY = (viewportHeight - mouseY) - playerY;
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
		//Used for debug
		//System.out.println(" pX: " + playerX + " dX: " 
		//+ dX + " pY: " + playerY +  " dY: " + dY + " slope: " +
		//slope + " xVel: " + vel.x + " yVel: " + vel.y);
		solidBody.setLinearVelocity(vel);
	}
	
	/**
	 * Determines which corner of the player to spawn the projectile to avoid
	 * intersecting with the player.
	 * @return vector signifying quadrant of player.
	 */
	private Vector2 determineQuadrant() {
		
		/**  */
		float dX = mouseX - playerX;
		
		/**  */
		float dY = (viewportHeight - mouseY) - playerY;
		
		/**  */
		float displacement = 0.05f;
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
	public void simulateResistance() {
		
		/**  Current X velocity.*/
		float xVelocity = solidBody.getLinearVelocity().x;
		
		/**  Current Y velocity.*/
		float yVelocity = solidBody.getLinearVelocity().y;
		
		if (xVelocity < 0)  {
			solidBody.applyForceToCenter(0.3f, 0, true);
		} else if (xVelocity > 0) {
			solidBody.applyForceToCenter(-0.3f, 0, true);
		} 
		
		if (yVelocity > 0) {
			solidBody.applyForceToCenter(0, -0.3f, true);
		} else if (yVelocity < 0)  {
			solidBody.applyForceToCenter(0, 0.3f, true);
		}
	}
	
	/**
	 * Sets graphical object's position to a physical body's position.
	 */
	public void setPos() {
		solidBody.setUserData(body);
		body.setPosition(solidBody.getPosition());
	}
	
	/**
	 * Accumulates time until the despawn time is reached.
	 * @param time difference in time between frames.
	 * @return true if projectile is deletable.
	 */
	public boolean deletable(final float time) {
		accumulator += time;
		return (accumulator >= despawnTime);
	}
	
	/**
	 * Returns physical body.
	 * @return projectile's physical body.
	 */
	public Body getBody() {
		return solidBody;
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

