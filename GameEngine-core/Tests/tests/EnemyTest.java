package tests;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.badlogic.gdx.math.Vector2;

import mimics.MEnemy;

public class EnemyTest {
	
	MEnemy e;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void MEnemy() {
		e = new MEnemy(0,0);
		assertTrue(e != null);
	}
	
	@Test
	public void update() {
		e = new MEnemy(-10, 10);
		e.update(3, 3);
		assertTrue(e.getPlayerX() == 3 && e.getPlayerY() == 3);
	}
	
	@Test 
	public void pushAway1() {
		Vector2 v =  new Vector2(0, 0);
		Vector2 exp = new Vector2(0, 0);
		e = new MEnemy(0, 0);
		e.setVelocity(v);
		assertTrue(e.pushAway().x == exp.x);
		assertTrue(e.pushAway().y == exp.y);
	}

	// x > 0 y > 0
	@Test 
	public void pushAway2() {
		Vector2 v =  new Vector2(1, 1);
		Vector2 exp = new Vector2(-90, -90);
		e = new MEnemy(0, 0);
		e.setVelocity(v);
		assertTrue(e.pushAway().x == exp.x);
		assertTrue(e.pushAway().y == exp.y);
	}

	// x < 0 y < 0
	@Test 
	public void pushAway3() {
		Vector2 v =  new Vector2(-1, -1);
		Vector2 exp = new Vector2(90, 90);
		e = new MEnemy(0, 0);
		e.setVelocity(v);
		assertTrue(e.pushAway().x == exp.x);
		assertTrue(e.pushAway().y == exp.y);
	}

	@Test
	public void calculateVelocity1() {
		Vector2 v = new Vector2(0, 0);
		e = new MEnemy(0, 0);
		e.update(0, 0);
		e.incAccumulator(0.1f);
		assertTrue(e.calculateVelocity().x == v.x);
		assertTrue(e.calculateVelocity().y == v.y);
	}
	
	@Test
	public void calculateVelocity2() {
		Vector2 v = new Vector2(0, 0);
		e = new MEnemy(0, 0);
		e.update(0, 0);
		e.incAccumulator(0);
		assertTrue(e.calculateVelocity().x == v.x);
		assertTrue(e.calculateVelocity().y == v.y);
	}
	
	//dX > 0 dY > 0
	@Test
	public void calculateVelocity3() {
		Vector2 v = new Vector2(1/2, 1/2);
		e = new MEnemy(0, 0);
		e.update(1, 1);
		e.incAccumulator(0.1f);
		assertTrue(e.calculateVelocity().x == v.x);
		assertTrue(e.calculateVelocity().y == v.y);
	}

	//dX < 0 dY < 0
	@Test
	public void calculateVelocity4() {
		Vector2 v = new Vector2(-1/2, -1/2);
		e = new MEnemy(1, 1);
		e.update(0, 0);
		e.incAccumulator(0.1f);
		assertTrue(e.calculateVelocity().x == v.x);
		assertTrue(e.calculateVelocity().y == v.y);
	}

	@Test
	public void takeDamage1() {
		e = new MEnemy(0, 0);
		e.takeDamage(10f);
		assertTrue(e.getHealth() == -10);
	}
	
	@Test
	public void takeDamage2() {
		e = new MEnemy(0, 0);
		e.takeDamage(-10f);
		assertTrue(e.getHealth() == 10);
	}


}
