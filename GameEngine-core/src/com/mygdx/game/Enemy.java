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
 * Enemy class acts as the parent class for all Enemy children.
 * 
 * @author Sean Aubrey, Gabriel Fountain, Brandon Conn
 */
public class Enemy {
	
	/**  Velocity limit.*/
	private float maxVelocity;
	
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
	
	/** position. */
	private Vector2 position;
	/** velocity. */
	private Vector2 velocity;
	
	/** All projectiles fired from Enemy. */
	private ArrayList<EnemyProjectile> projectiles;
	
	/**  Player's X position.*/
	private float playerX;
    
	/**  Player's Y position.*/
	private float playerY;
	
	/** Enemy health. */
	private float health;
	
	/** Damage inflicted on touch. */
	private float bodyDamage;
	
	/** Time accumulator. */
	private float accumulator;
	
	/** Damage inflicted by projectiles. */
	private int bulletDamage;
	
	/**
	 * 
	 * @param spawnX X spawn coordinate
	 * @param spawnY Y spawn coordinate
	 */
	public Enemy(final float spawnX, final float spawnY) {
		position = new Vector2(spawnX, spawnY);
		velocity = new Vector2();
		accumulator = 1.0f;
		projectiles = new ArrayList<EnemyProjectile>();
	}
	
	/**
	 * Constructs a physical body in the world.
	 * @param spawnX X spawn coordinate
	 * @param spawnY Y spawn coordinate
	 * @param radius radius of body
	 * @param density density of body
	 * @param restitution bounciness of body
	 */
	public void buildBody(final float spawnX, final float spawnY, final float radius,
			final float density, final float restitution) {
		body = new Circle();
		bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.position.set(spawnX, spawnY); // determine spawn operation
		solidBody = GameEngine.getWorld().createBody(bodyDef);
		circle = new CircleShape();
		circle.setRadius(radius);
		
		fixtureDef = new FixtureDef();
		fixtureDef.shape = circle;
		fixtureDef.density = density;
		fixtureDef.friction = 0.4f;
		fixtureDef.restitution = restitution; // bounciness
		fixture = solidBody.createFixture(fixtureDef);
	}
	
	/**
	 * Updates all necessary generic enemy values.
	 * @param x player's x coordinate
	 * @param y player's y coordinate
	 */
	public void update(final float x, final float y) {
		setPlayerX(x);
		setPlayerY(y);
		updatePos();
		updateVel();
		accumulator += GameEngine.getDeltaTime();
	}

	/**
	 * Sets position.
	 */
	public void updatePos() {
		position.set(getBody().getPosition());
	}
	
	/**
	 * Sets current velocity.
	 */
	public void updateVel() {
		velocity.set(getBody().getLinearVelocity());
	}
	
	/**
	 * Used when in contact with player, this sends an enemy
	 * flying in the opposite direction it approached the player.
	 * @return vec
	 */
	public Vector2 pushAway() {
		Vector2 vec =  new Vector2();
		float x = velocity.x;
		float y = velocity.y;
		accumulator = 0;
		float xBurst = 90f;
		float yBurst = 90f;
		if (x > 0) {
			vec.x = -xBurst;
		} else if (x < 0) {
			vec.x = xBurst;
		}
		if (y > 0) {
			vec.y = -yBurst;
		} else if (y < 0) {
			vec.y = yBurst;
		}
		return vec;
	}
	

	/**
	 * Applies impulse to body. Used in conjunction with pushAway().
	 * @param v velocity
	 */
	public void applyImpulse(final Vector2 v) {
		solidBody.applyLinearImpulse(v.x, v.y, 0, 0, true);
	}

	/**
	 * Calculates and returns x and y velocity, aiming towards the player.
	 * @return velocity
	 */
	public Vector2 calculateVelocity() {
		if (accumulator > 1.0f) {
			float dX, dY, slope;
			dX = playerX - position.x;
			dY = playerY - position.y;
			slope = Math.abs(dX / dY);
			if (dX > 0) {
				velocity.x = maxVelocity * (slope / (slope + 1));
			} else if (dX < 0) {
				velocity.x = -maxVelocity * (slope / (slope + 1));
			}
			if (dY >= 0) {
				velocity.y = maxVelocity * (1 / (slope + 1));
			} else if (dY < 0) {
				velocity.y = -maxVelocity * (1 / (slope + 1));
			}
		}
		return velocity;
	}
	
	/**
	 * Collects each projectile fired from the Demon that can be deleted.
	 * @param time time between frames.
	 */
	public void manageProjectiles(final float time) {
		if (!getProjectiles().isEmpty()) {
			for (int i = 0; i < getProjectiles().size(); i++) {
				if (getProjectiles().get(i).deletable(time)) { 
					getProjectiles().remove(i);
				}
			}
		}
	}
	
	/**
	 * Creates a new Projectile object and gives it the Player's 
	 * position and the cursor coordinates to which it will be directed.
	 * @param playerX the horizontal pixel position.
	 * @param playerY the vertical pixel position.
	 */
	public void spit(final float playerX, final float playerY) {
		EnemyProjectile p = new EnemyProjectile(
				getX(), getY(), playerX, playerY, bulletDamage);
		getProjectiles().add(p);
	}
	
	/**
	 * Reduces health of enemy.
	 * @param damage damage inflicted upon enemy.
	 * @return boolean
	 */
	public boolean takeDamage(final float damage) {
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
		}
	}
	
	/**
	 * Sets health.
	 * @param health enemy health
	 */
	public void setHealth(final float health) {
		this.health = health;
	}
	
	/**
	 * Returns health.
	 * @return health
	 */
	public float getHealth() {
		return health;
	}
	
	/**
	 * Multiplies health by multiplier.
	 * @param mult multiplier
	 */
	public void multHealth(final float mult) {
		health *= mult;
	}
	
	/**
	 * Returns amount of body damage is inflicted.
	 * @return bodyDamage
	 */
	public float getBodyDamage() {
		return bodyDamage;
	}
	
	/**
	 * Empties the enemy's array of projectiles and marks the body for deletion.
	 * @return false
	 */
	public boolean setDeletable() {
		purgeProjectiles();
		solidBody.setUserData("deletable");
		return false;
	}
	
	/**
	 * Multiplies max velocity.
	 * @param mult multiplier
	 */
	public void multMaxVelocity(final float mult) {
		maxVelocity *= mult;
	}
	
	/**
	 * Returns list of projectiles.
	 * @return projectiles
	 */
	public ArrayList<EnemyProjectile> getProjectiles() {
		return projectiles;
	}
	
	/**
	 * Returns physical body.
	 * @return Enemy's physical body
	 */
	public Body getBody() {
		return solidBody;
	}
	
	/**
	 * Returns the shape of the body.
	 * @return body
	 */
	public Circle getShapeBody() {
		return body;
	}
	
	/**
	 * Returns body fixture.
	 * @return Enemy's body fixture
	 */
	public Fixture getFixture() {
		return fixture;
	}
	
	/**
	 * Sets the body damage.
	 * @param damage damage
	 */
	public void setBodyDamage(final float damage) {
		bodyDamage = damage;
	}
	
	/**
	 * Sets body's maximum velocity.
	 * @param v velocity
	 */
	public void setMaxVelocity(final float v) {
		maxVelocity = v;
	}
	
	/**
	 * Sets coordinates.
	 * @param pos position
	 */
	public void setPosition(final Vector2 pos) {
		position = pos;
	}
	
	/**
	 * Increases accumulator of delta.
	 * @param dt delta time
	 */
	public void incAccumulator(final float dt) {
		accumulator += dt;
	}
	
	/**
	 * Sets bullet damage.
	 * @param d damage
	 */
	public void setBulletDamage(final int d) {
		bulletDamage = d;
	}
	
	/**
	 * Sets X position of the player.
	 * @param x x coordinate
	 */
	public void setPlayerX(final float x) {
		playerX = x;
	}
	
	/**
	 * Sets Y position of the player.
	 * @param y y coordinate
	 */
	public void setPlayerY(final float y) {
		playerY = y;
	}
	
	/**
	 * Returns X position of the player.
	 * @return Player's x coordinate.
	 */
	public float getPlayerX() {
		return playerX;
	}
	
	/**
	 * Returns Y position of the player.
	 * @return Player's y coordinate.
	 */
	public float getPlayerY() {
		return playerY;
	}
	
	/**
	 * Returns body's horizontal position.
	 * @return physical body's X.
	 */
	public float getX() {
		return getBody().getPosition().x;
	}
	
	/**
	 * Returns body's vertical position.
	 * @return physical body's Y.
	 */
	public float getY() {
		return getBody().getPosition().y;
	}
	
}
