package com.mygdx.game; 

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;

/**
 * 
 * @author Sean Aubrey, Gabriel Fountain, Brandon Conn
 */
public class Listener implements ContactListener {

	/**   */
	private String A;
	
	/**   */
	private String B;
	
	/**   */
	private String path = "class com.mygdx.game.";
	
	/**   */
	private GameEngine gE;
	
	/**
	 * 
	 * @param gE
	 */
	Listener(final GameEngine gE) {
		this.gE = gE;
	}
	
	/**
	 * 
	 * @param contact
	 */
	//need to make sure spawning does not happen on top of another object
	@Override
	public void beginContact(final Contact contact) {
		Object objA = contact.getFixtureA().getUserData();
		A = objA.getClass().toString();
		
		Object objB = contact.getFixtureB().getUserData();
		B = objB.getClass().toString();
		
		if (A.equals(path + "PlayerProjectile")) {
			A = "playerpProj";	
		} else if (B.equals(path + "PlayerProjectile")) {
			B = "playerpProj";
		}
		if (A.equals(path + "EnemyProjectile")) {
			A = "enemyProj";	
		} else if (B.equals(path + "EnemyProjectile")) {
			B = "enemyProj";
		}
		if (A.equals(path + "Player")) {
			A = "player";
		} else if (B.equals(path + "Player")) {
			B = "player";
		}
		if (A.equals(path + "Swarmer")) {
			A = "swarmer";
		} else if (B.equals(path + "Swarmer")) {
			B = "swarmer";
		}
		if (A.equals(path + "Spitter")) {
			A = "spitter";
		} else if (B.equals(path + "Spitter")) {
			B = "spitter";
		}
		if (A.equals(path + "Demon")) {
			A = "demon";
		} else if (B.equals(path + "Demon")) {
			B = "demon";
		}
		
		if ((A.equals("swarmer") || A.equals("spitter") || A.equals("demon")) 
				&& B.equals("playerpProj")) {
			Enemy a;
			if (A.equals("swarmer")) {
				a = (Swarmer) contact.getFixtureA().getUserData();
			} else if (A.equals("spitter")) {
				a = (Spitter) contact.getFixtureA().getUserData();
			} else {
				a = (Demon) contact.getFixtureA().getUserData();
			}
			PlayerProjectile b = (PlayerProjectile) contact.getFixtureB().getUserData();
			if (a.takeDamage(b.getBulletDamage())) {
				gE.incrementKillCount();
			}
			b.setDeletable();
		} else if (A.equals("playerpProj") 
				&& (B.equals("swarmer") || B.equals("spitter") || B.equals("demon"))) {
			Enemy b;
			if (B.equals("swarmer")) {
				b = (Swarmer) contact.getFixtureB().getUserData();
			} else if (B.equals("spitter")) {
				b = (Spitter) contact.getFixtureB().getUserData();
			} else {
				b = (Demon) contact.getFixtureB().getUserData();
			}
			PlayerProjectile a = (PlayerProjectile) contact.getFixtureA().getUserData();
			if (b.takeDamage(a.getBulletDamage())) {
				gE.incrementKillCount();
			}
			a.setDeletable();
		}
		
		if ((A.equals("swarmer") || A.equals("spitter") || A.equals("demon")) 
				&& B.equals("player")) {
			Enemy a;
			if (A.equals("swarmer")) {
				a = (Swarmer) contact.getFixtureA().getUserData();
			} else if (A.equals("spitter")) {
				a = (Spitter) contact.getFixtureA().getUserData();
			} else {
				a = (Demon) contact.getFixtureA().getUserData();
			}
			Player b = (Player) contact.getFixtureB().getUserData();
			a.pushAway();
			b.takeDamage(a.getBodyDamage());
			gE.flashRed();
		} else if (A.equals("player") 
				&& (B.equals("swarmer") || B.equals("spitter") || B.equals("demon"))) {
			Enemy b;
			if (B.equals("swarmer")) {
				b = (Swarmer) contact.getFixtureB().getUserData();
			} else if (B.equals("spitter")) {
				b = (Spitter) contact.getFixtureB().getUserData();
			} else {
				b = (Demon) contact.getFixtureB().getUserData();
			}
			Player a = (Player) contact.getFixtureA().getUserData();
			b.pushAway();
			a.takeDamage(b.getBodyDamage());
			gE.flashRed();
			//send inverted impulse to enemy
		}
		
		if (A.equals("player") && B.equals("enemyProj")) {
			Player a = (Player) contact.getFixtureA().getUserData();
			EnemyProjectile b = (EnemyProjectile) contact.getFixtureB().getUserData();
			a.takeDamage(b.getBulletDamage());
			b.setDeletable();
			gE.flashRed();
		} else if (A.equals("enemyProj") && B.equals("player")) {
			Player b = (Player) contact.getFixtureB().getUserData();
			EnemyProjectile a = (EnemyProjectile) contact.getFixtureA().getUserData();
			b.takeDamage(a.getBulletDamage());
			a.setDeletable();
			gE.flashRed();
		}
		//conditionals for enemy projectile and player
	}

	/**
	 * 
	 * @param contact
	 */
	@Override
	public void endContact(final Contact contact) {
	}

	/**
	 * 
	 * @param contact
	 * @param oldManifold
	 */
	@Override
	public void preSolve(final Contact contact, final Manifold oldManifold) {
	}

	/**
	 * 
	 * @param contact
	 * @param impulse
	 */
	@Override
	public void postSolve(final Contact contact, final ContactImpulse impulse) {
	}
}
