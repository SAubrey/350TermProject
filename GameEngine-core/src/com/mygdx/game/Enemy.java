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
 * Enemy class acts as the parent class for all Enemy children
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
	
	/** */
	private Vector2 position;
	
	/** All projectiles fired from Enemy. */
	private ArrayList<EnemyProjectile> projectiles;
	
	/**  Player's X position.*/
	private float playerX;
    
	/**  Player's Y position.*/
	private float playerY;
	
	/** */
	private float health;
	
	/** */
	private float bodyDamage;
	
	/** */
	private float accumulator;
	
	/** */
	private int bulletDamage;
	
	/**
	 * 
	 * @param spawnX
	 * @param spawnY
	 */
	public Enemy(final float spawnX, final float spawnY) {
		position = new Vector2(spawnX, spawnY);
		accumulator = 1.0f;
		projectiles = new ArrayList<EnemyProjectile>();
	}
	
	/**
	 * 
	 * @param spawnX
	 * @param spawnY
	 * @param radius
	 * @param density
	 * @param restitution
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
	 * 
	 * @param x
	 * @param y
	 */
	public void update(final float x, final float y) {
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
			} else if (y <= 0) {
				y = yBurst;
			}
		}
		solidBody.applyLinearImpulse(x, y, 0, 0, true);
	}
	
	/**
	 * 
	 */
	public void calculateVelocity() {
		if (accumulator > 1.0f) {
			Vector2 vel = new Vector2();
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
	 * 
	 * @param damage
	 * @return
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
	 * 
	 * @param health
	 */
	public void setHealth(final float health) {
		this.health = health;
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
	 * @param mult
	 */
	public void multHealth(final float mult) {
		health *= mult;
	}
	
	/**
	 * 
	 * @return
	 */
	public float getBodyDamage() {
		return bodyDamage;
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean setDeletable() {
		purgeProjectiles();
		solidBody.setUserData("deletable");
		return false;
	}
	
	/**
	 * 
	 * @param mult
	 */
	public void multMaxVelocity(final float mult) {
		maxVelocity *= mult;
	}
	
	/**
	 * 
	 * @return
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
	 * 
	 * @return
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
	 * 
	 * @param damage
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
	 * 
	 * @param pos
	 */
	public void setPosition(final Vector2 pos) {
		position = pos;
	}
	
	/**
	 * 
	 * @param dt
	 */
	public void incAccumulator(final float dt) {
		accumulator += dt;
	}
	
	/**
	 * 
	 * @param d
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
