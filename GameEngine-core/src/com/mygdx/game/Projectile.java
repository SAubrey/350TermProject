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
 * @author Sean Aubrey, Gabriel Fountain, Brandon Conn
 *
 */
public class Projectile {
	
	/**  */
	private final int scale = GameEngine.SCALE;
	
	/**  */
	private final float despawnTime = 3.0f;
	
	/**  */
	private int windowHeight = GameEngine.windowHeight;
	
	/**  */
	private float viewportHeight = scale(windowHeight);
	
	/**  */
	private float maxVelocity = 70;
    
	/**  */
	private float size = 0.5f;
    
	/**  */
	private float playerX;
    
	/**  */
	private float playerY;
	
	/**  */
	private float mouseX;
	
	/**  */
	private float mouseY;
	
	/**  */
	private float accumulator;
	
	/**  */
	private Vector2 vel;
	
	/**  */
	private Vector2 vec;
	
	/**  */
	private Vector2 quad;
	
	/**  */
	private Circle body;
	
	/**  */
	private BodyDef bodyDef;
	
	/**  */
	private Body solidBody;
	
	/**  */
	private CircleShape circle;
	
	/**  */
	private FixtureDef fixtureDef;
	
	/**  */
	@SuppressWarnings("unused")
	
	/**  */
	private Fixture fixture;
	
	/**
	 * @param playerX
	 * @param playerY
	 * @param mouseX
	 * @param mouseY
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
	 * pixel locations must be adapted to the viewport
	 * works for first sector.
	 * Formula to determine X + Y velocities: BF(n) = B(n/(n+1)) + B(1/(n+1))
	 * where n is X/Y slope and B is max velocity
	 */
	private void calculateVelocity() {
		vel = new Vector2();
		float dX, dY, slope;
		dX = mouseX - playerX;
		//System.out.println("(vpH: " + viewportHeight + " - mouseY: " + mouseY + ") - pY: " + playerY);
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
		//System.out.println(" pX: " + playerX + " dX: " + dX + " pY: " + playerY +  " dY: " + dY + " slope: " +
		//slope + " xVel: " + vel.x + " yVel: " + vel.y);
		solidBody.setLinearVelocity(vel);
	}
	
	/**
	 * Determines which corner of the player to spawn the projectile to avoid
	 * intersecting with the player
	 * 
	 * @return
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
	 * 
	 */
	public void simulateResistance() {
		
		/**  */
		float xVelocity = solidBody.getLinearVelocity().x;
		
		/**  */
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
	 * 
	 */
	public void setPos() {
		solidBody.setUserData(body);
		body.setPosition(solidBody.getPosition()); 
	}
	
	/**
	 * Bodies can't be deleted during a physics step, add body to a list then delete
	 * in GameEngine after physics step
	 * 
	 * @param time
	 * @return
	 */
	public boolean deletable(final float time) {
		accumulator += time;
		return (accumulator >= despawnTime);
	}
	
	/**
	 * @return
	 */
	public Body getBody() {
		return solidBody;
	}
	
	/**
	 * @param val
	 * @return
	 */
	public float scale(final float val) {
		return val / scale;
	}
}

