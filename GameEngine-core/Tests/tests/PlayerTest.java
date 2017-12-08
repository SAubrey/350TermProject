package tests;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.badlogic.gdx.math.Vector2;

import mimics.MPlayer;

public class PlayerTest {
	
	MPlayer p;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		p = new MPlayer();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void simulateResistance1() {
		Vector2 v =  new Vector2(10, 10);
		float expX = 1 + Math.abs(v.x / 5);
		float expY = 1 + Math.abs(v.y / 5);
		assertTrue(p.simulateResistance(v).x == -expX);
		assertTrue(p.simulateResistance(v).y == -expY);
	}
	
	@Test
	public void simulateResistance2() {
		Vector2 v =  new Vector2(-10, 0);
		float expX = 1 + Math.abs(v.x / 5);
		assertTrue(p.simulateResistance(v).x == expX);
		assertTrue(p.simulateResistance(v).y == 0);
	}
	
	@Test
	public void simulateResistance3() {
		Vector2 v =  new Vector2(0, -10);
		float expY = 1 + Math.abs(v.y / 5);
		assertTrue(p.simulateResistance(v).x == 0);
		assertTrue(p.simulateResistance(v).y == expY);
	}
	
	@Test
	public void velocityCap1() {
		Vector2 v =  new Vector2(0, 0);
		assertTrue(p.velocityCap(v).x == 0);
		assertTrue(p.velocityCap(v).y == 0);
	}
	
	@Test
	public void velocityCap2() {
		float maxVelocity = 80f;
		Vector2 v =  new Vector2(100, 100);
		float sum = Math.abs(v.x) + Math.abs(v.y);
		float xRat = v.x / sum;
		float yRat = v.y / sum;
		float xCap = Math.abs(xRat * maxVelocity);
		float yCap = Math.abs(yRat * maxVelocity);
		assertTrue(p.velocityCap(v).x == xCap);
		assertTrue(p.velocityCap(v).y == yCap);
	}
	
	@Test
	public void velocityCap3() {
		float maxVelocity = 80f;
		Vector2 v =  new Vector2(-100, -100);
		float sum = Math.abs(v.x) + Math.abs(v.y);
		float xRat = v.x / sum;
		float yRat = v.y / sum;
		float xCap = Math.abs(xRat * maxVelocity);
		float yCap = Math.abs(yRat * maxVelocity);
		assertTrue(p.velocityCap(v).x == -xCap);
		assertTrue(p.velocityCap(v).y == -yCap);
	}
	
	//MOVE
	@Test
	public void move1() {
		Vector2 f =  new Vector2(0, 0);
		f = p.move(false, false, false, false);
		assertTrue(f.x == 0);
		assertTrue(f.y == 0);
	}
	
	//left
	@Test
	public void move2() {
		float pA = p.getPlayerAcceleration();
		Vector2 f =  new Vector2(p.move(false, true, false, false));
		assertTrue(f.x == -pA);
		assertTrue(f.y == 0);
	}
	
	//right
	@Test
	public void move3() {
		float pA = p.getPlayerAcceleration();
		Vector2 f =  new Vector2(p.move(true, false, false, false));
		assertTrue(f.x == pA);
		assertTrue(f.y == 0);
	}
	
	//up
	@Test
	public void move4() {
		float pA = p.getPlayerAcceleration();
		Vector2 f =  new Vector2(p.move(false, false, true, false));
		assertTrue(f.x == 0);
		assertTrue(f.y == pA);
	}
	
	//down
	@Test
	public void move5() {
		float pA = p.getPlayerAcceleration();
		Vector2 f =  new Vector2(p.move(false, false, false, true));
		assertTrue(f.x == 0);
		assertTrue(f.y == -pA);
	}
	
	//left & up
	@Test
	public void move6() {
		float pA = p.getPlayerAcceleration();
		float diagForce = (pA * 2) / 3;
		Vector2 f = new Vector2(p.move(false, true, true, false));
		assertTrue(f.x == -diagForce);
		assertTrue(f.y == diagForce);
	}

	//left & down
	@Test
	public void move7() {
		float pA = p.getPlayerAcceleration();
		float diagForce = (pA * 2) / 3;
		Vector2 f =  new Vector2(p.move(false, true, false, true));
		assertTrue(f.x == -diagForce);
		assertTrue(f.y == -diagForce);
	}
	
	//right & up
	@Test
	public void move8() {
		float pA = p.getPlayerAcceleration();
		float diagForce = (pA * 2) / 3;
		Vector2 f =  new Vector2(p.move(true, false, true, false));
		assertTrue(f.x == diagForce);
		assertTrue(f.y == diagForce);
	}

	//right & down
	@Test
	public void move9() {
		float pA = p.getPlayerAcceleration();
		float diagForce = (pA * 2) / 3;
		Vector2 f =  new Vector2(p.move(true, false, false, true));
		assertTrue(f.x == diagForce);
		assertTrue(f.y == -diagForce);
	}
	
	@Test
	public void takeDamage() {
		p.takeDamage(10f);
		assertTrue(p.getHealth() == 90);
	}
	
	@Test
	public void incrementKillCount1() {
		p.incrementKillCount();
		assertTrue(p.getKillCount() == 1 && p.getScore() == 1);
	}
	
	@Test
	public void incrementKillCount2() {
		p.takeDamage(50);
		p.incrementKillCount();
		assertTrue(p.getHealth() == 51);
	}
	
	@Test
	public void multMovement1() {
		float pA = p.getPlayerAcceleration();
		float maxV = p.getMaxVel();
		p.multMovement(2);
		assertTrue(pA * 2 == p.getPlayerAcceleration());
		assertTrue(maxV * 2 == p.getMaxVel());
	}
	
	@Test
	public void multMovement2() {
		float shotgunTime = p.getShotgunTime();
		float shotTime = p.getShotTime();
		p.multMovement(2);
		assertTrue(shotgunTime - 2 * .02f == p.getShotgunTime());
		assertTrue(shotTime - 2 * .01f == p.getShotTime());
	}
	
	@Test
	public void multMovement3() {
		p.setShotTime(.049f);
		float shotgunTime = p.getShotgunTime();
		float shotTime = p.getShotTime();
		p.multMovement(2);
		assertFalse(shotgunTime - 2 * .02f == p.getShotgunTime());
		assertFalse(shotTime - 2 * .01f == p.getShotTime());
	}

}
