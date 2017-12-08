package com.mygdx.game;
 
/**
 * Mobile enemy that chases the player.
 * @author Sean Aubrey, Gabriel Fountain, Brandon Conn
 */
public class Swarmer extends Enemy {
	
	/**
	 * 
	 * @param spawnX spawn X coordinate
	 * @param spawnY spawn Y coordinate
	 */
	public Swarmer(final float spawnX, final float spawnY) {
		super(spawnX, spawnY);
		setMaxVelocity(60f);
		setHealth(10f);
		setBodyDamage(10f);
		
		buildBody(spawnX, spawnY, GameEngine.getSwarmRadius(), 0.1f, 0.95f);
		getBody().setUserData("swarmer");
		getFixture().setUserData(this);
	}
	
	/**
	 * 
	 * @param x x coordinate
	 * @param y y coordinate
	 */
	@Override
	public void update(final float x, final float y) {
		super.update(x, y);
		getBody().setLinearVelocity(calculateVelocity());
	}
}
