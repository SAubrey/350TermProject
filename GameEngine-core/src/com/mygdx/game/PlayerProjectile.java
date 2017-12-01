package com.mygdx.game;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class PlayerProjectile extends Projectile {
	
	/**  Cursor click X position.*/
	public float mouseX;
	
	/**  Cursor click Y position.*/
	public float mouseY;
	
	/**
	 * Constructor that assigns passed values locally that will be used
	 * to calculate trajectory. Creates graphical and physical body objects.
	 * @param playerX player's X coordinate in pixels.
	 * @param playerY player's Y coordinate in pixels.
	 * @param mouseX cursor's X coordinate in pixels.
	 * @param mouseY cursor's Y coordinate in pixels.
	 */
	public PlayerProjectile(final float sourceX, final float sourceY,
			final float targetX, final float targetY, float bulletDamage) {
		super(sourceX, sourceY, targetX, targetY, bulletDamage);
		dY = (viewportHeight - targetY) - sourceY;
		maxVelocity = 150;
		
		body = new Circle();
		bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		vec = new Vector2(determineQuadrant());
		bodyDef.position.set(sourceX + vec.x, sourceY + vec.y);
		solidBody = GameEngine.getWorld().createBody(bodyDef);
		solidBody.setUserData("playerProj");
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
	}
}
