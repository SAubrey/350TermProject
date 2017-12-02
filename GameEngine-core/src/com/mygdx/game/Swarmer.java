package com.mygdx.game;

public class Swarmer extends Enemy {
	
	public Swarmer(final float spawnX, final float spawnY) {
		super(spawnX, spawnY);
		setMaxVelocity(60f);
		setHealth(10f);
		setBodyDamage(10f);
		
		buildBody(spawnX, spawnY, GameEngine.getSwarmRadius(), 0.1f, 0.95f);
		getBody().setUserData("swarmer");
		getFixture().setUserData(this);
	}
	
	@Override
	public void update(final float x, final float y) {
		setPlayerX(x);
		setPlayerY(y);
		incAccumulator(GameEngine.getDeltaTime());
		setPosition(getBody().getPosition());
		calculateVelocity();
	}
}
