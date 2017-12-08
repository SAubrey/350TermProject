package com.mygdx.game; 
  
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;

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
	private final float despawnTime = 2.0f;
	
	/**  Velocity limit.*/
	private int maxVelocity;
	
	/** */
	private float damage;
	
	/**  Accumulates time between frames. */
	private float accumulator;
	
	/** */
	private float dX;
	
	/** */
	private float dY;
	
	/**  Temporary position vector.*/
	private Vector2 vec;
	
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
	private Fixture fixture;
	
	/**
	 * Constructor that assigns passed values locally that will be used
	 * to calculate trajectory. Creates graphical and physical body objects.
	 * @param sourceX player's X coordinate in pixels.
	 * @param sourceY player's Y coordinate in pixels.
	 * @param targetX cursor's X coordinate in pixels.
	 * @param targetY cursor's Y coordinate in pixels.
	 * @param bulletDamage projectile's damage
	 */
	public Projectile(final float sourceX, final float sourceY,
			final float targetX, final float targetY, final float bulletDamage) {
		damage = bulletDamage;
		dX = targetX - sourceX;
		dY = targetY - sourceY;
	}
	
	/**
	 * Allows subclasses to build the body after modifying passed in data.
	 * @param sourceX Projectile's source x position.
	 * @param sourceY Projectile's source y position.
	 */
	public void buildBody(final float sourceX, final float sourceY) {
		body = new Circle();
		bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		vec = new Vector2(determineQuadrant());
		bodyDef.position.set(sourceX + vec.x, sourceY + vec.y);
		solidBody = GameEngine.getWorld().createBody(bodyDef);
		circle = new CircleShape();
		circle.setRadius(GameEngine.getProjRadius());
		
		fixtureDef = new FixtureDef();
		fixtureDef.shape = circle;
		fixtureDef.density = 0.1f; 
		fixtureDef.friction = 0.4f;
		fixtureDef.restitution = 0.8f; // bounciness
		fixture = solidBody.createFixture(fixtureDef);
	}

	/**
	 * Takes the differences in X and Y between the cursor click location and the player
	 * to calculate how much X and Y velocities should be used with respect to 
	 * the Projectile's maximum velocity in order to angle the trajectory accurately.
	 * Formula to determine X + Y velocities: BF(n) = B(n/(n+1)) + B(1/(n+1)),
	 * where n is X/Y slope and B is max velocity.
	 */
	public void calculateVelocity() {
		Vector2 vel = new Vector2();
		float slope = Math.abs(dX / dY);
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
		solidBody.setLinearVelocity(vel);
	}
	
	/**
	 * Determines which corner of the player to spawn the projectile to avoid
	 * intersecting with the player.
	 * @return vector signifying quadrant of player.
	 */
	public Vector2 determineQuadrant() {
		float displacement = .1f;
		Vector2 quad = new Vector2();
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
	 * Sets graphical object's position to a physical body's position.
	 */
	public void setPos() {
		body.setPosition(solidBody.getPosition());
	}
	
	/**
	 * Accumulates time until the despawn time is reached.
	 * @param time difference between frames.
	 * @return true if the projectile has been set for deletion
	 */
	public boolean deletable(final float time) {
		accumulator += time;
		if (accumulator >= despawnTime) {
			solidBody.setUserData("deletable");
			return true;
		}
		return false;
	}
	
	/**
	 * Sets accumulated time to its despawn time.
	 */
	public void setDeletable() {
		accumulator = despawnTime;
	}
	
	/**
	 * Sets body's maximum velocity.
	 * @param v velocity
	 */
	public void setMaxVelocity(final int v) {
		maxVelocity = v;
	}
	
	/**
	 * Sets difference in Y between source and target.
	 * @param dY deltaY
	 */
	public void setDeltaY(final float dY) {
		this.dY = dY;
	}
	
	/**
	 * Returns physical body.
	 * @return projectile's physical body
	 */
	public Body getBody() {
		return solidBody;
	}
	
	/**
	 * Returns body fixture.
	 * @return projectile's body fixture
	 */
	public Fixture getFixture() {
		return fixture;
	}
	
	/**
	 * Returns projectile contact damage.
	 * @return damage bullet's damage
	 */
	public float getBulletDamage() {
		return damage;
	}
}

