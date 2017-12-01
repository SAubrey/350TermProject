package com.mygdx.game;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class Swarmer extends Enemy {
	
	public Swarmer(float spawnX, float spawnY) {
		super(spawnX, spawnY);
		maxVelocity = 60f;
		health = 10f;
		bodyDamage = 10f;
		
		body = new Circle();
		bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.position.set(spawnX, spawnY); // determine spawn operation
		solidBody = GameEngine.getWorld().createBody(bodyDef);
		solidBody.setUserData("swarmer");
		circle = new CircleShape();
		circle.setRadius(GameEngine.getSwarmRadius());
		
		fixtureDef = new FixtureDef();
		fixtureDef.shape = circle;
		fixtureDef.density = 0.1f; 
		fixtureDef.friction = 0.4f;
		fixtureDef.restitution = 0.95f; // bounciness
		fixture = solidBody.createFixture(fixtureDef);
		fixture.setUserData(this);
	}
	
	@Override
	public void update(float x, float y) {
		playerX = x;
		playerY = y;
		accumulator += GameEngine.getDeltaTime();
		position = solidBody.getPosition();
		calculateVelocity();
	}
	/*
	public void calculateVelocity() {
		if (accumulator > 1.0f) {
			vel = new Vector2();
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
	}*/
}
