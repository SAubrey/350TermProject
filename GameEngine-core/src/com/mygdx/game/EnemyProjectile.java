package com.mygdx.game;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class EnemyProjectile extends Projectile {
	public EnemyProjectile(final float sourceX, final float sourceY,
			final float targetX, final float targetY, float bulletDamage) {
		super(sourceX, sourceY, targetX, targetY, bulletDamage);
		
		maxVelocity = 170;
		//damage = 15;
		
		body = new Circle();
		bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		vec = new Vector2(determineQuadrant());
		bodyDef.position.set(sourceX + vec.x, sourceY + vec.y);
		solidBody = GameEngine.getWorld().createBody(bodyDef);
		solidBody.setUserData("enemyProj");
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
