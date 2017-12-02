package com.mygdx.game;

/**
 * Subclass of Projectile.
 */
public class PlayerProjectile extends Projectile {
	
	/**
	 * Parent's constructor assigns passed values locally that will be used
	 * to calculate trajectory. Creates graphical and physical body objects.
	 * Children of Projectile must set their own velocity and user data for 
	 * the body and its fixture, and also build the body and calculate velocity.
	 * @param sourceX player's X coordinate in pixels.
	 * @param sourceY player's Y coordinate in pixels.
	 * @param targetX cursor's X coordinate in pixels.
	 * @param targetY cursor's Y coordinate in pixels.
	 * @param bulletDamage projectile's damage
	 */
	public PlayerProjectile(final float sourceX, final float sourceY,
			final float targetX, final float targetY, final float bulletDamage) {
		super(sourceX, sourceY, targetX, targetY, bulletDamage);
		
		setDeltaY((GameEngine.getViewHeight() - targetY) - sourceY);
		setMaxVelocity(150);
		buildBody(sourceX, sourceY);
		getFixture().setUserData(this);
		getBody().setUserData("playerProj");
		calculateVelocity();
	}
}
