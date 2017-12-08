package com.mygdx.game; 

/**
 * Stationary enemy that fires projectiles towards the player.
 * @author Sean Aubrey, Gabriel Fountain, Brandon Conn
 */
public class Spitter extends Enemy {
	
	/**   */
	private float spitAccumulator;
	
	/**   */
	private float spitInterval = 3.0f;
	
	/**
	 * 
	 * @param spawnX spawnX
	 * @param spawnY spawnY
	 */
	public Spitter(final float spawnX, final float spawnY) {
		super(spawnX, spawnY);
		setHealth(30f);
		setBodyDamage(15f);
		setBulletDamage(15);
		
		buildBody(spawnX, spawnY, GameEngine.getSpitterRadius(), 0.9f, 0.5f);
		getBody().setUserData("spitter");
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
		//getShapeBody().setPosition(getBody().getPosition());
		spitAccumulator += GameEngine.getDeltaTime();
		manageProjectiles(GameEngine.getDeltaTime());
		
		if (spitAccumulator >= spitInterval) {
			spit(x, y);
			spitAccumulator = 0;
		}
	}
}
