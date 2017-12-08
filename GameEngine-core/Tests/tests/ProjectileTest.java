package tests;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.badlogic.gdx.math.Vector2;

import mimics.MProjectile;

public class ProjectileTest {

	MProjectile p;
	
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
	public void MProjectile() {
		p = new MProjectile(1, 1, 2, 2, 10);
		assertTrue("object not created", !p.equals(null));
	}
	
	//dX > 0, dY > 0
	@Test
	public void calculateVelocity1() {
		Vector2 expected = new Vector2(1/2,1/2);
		p = new MProjectile(1, 1, 2, 2, 10);
		assertTrue(null, expected.x == p.calculateVelocity().x);
		assertTrue(null, expected.y == p.calculateVelocity().y);
	}
	
	//dX < 0, dY < 0
	@Test
	public void calculateVelocity3() {
		Vector2 expected = new Vector2(-1/2,-1/2);
		p = new MProjectile(2, 2, 1, 1, 10);
		assertTrue(null, expected.x == p.calculateVelocity().x);
		assertTrue(null, expected.y == p.calculateVelocity().y);
	}
	
	//dX = 0, dY = 0
	@Test
	public void calculateVelocity4() {
		Vector2 expected = new Vector2(0, 0);
		p = new MProjectile(0, 0, 0, 0, 10);
		assertTrue(null, expected.x == p.calculateVelocity().x);
		assertTrue(null, expected.y == p.calculateVelocity().y);
	}
	
	//DETERMINE QUADRANT
	//dX > 0, dY > 0
	@Test
	public void determineQuadrant1() {
		Vector2 expected = new Vector2(.1f, .1f);
		p = new MProjectile(1, 1, 2, 2, 10);
		assertTrue(null, expected.x == p.determineQuadrant().x);
		assertTrue(null, expected.y == p.determineQuadrant().y);
	}
	
	//dX < 0, dY < 0
	@Test
	public void determineQuadrant2() {
		Vector2 expected = new Vector2(-.1f, -.1f);
		p = new MProjectile(2, 2, 1, 1, 10);
		assertTrue(null, expected.x == p.determineQuadrant().x);
		assertTrue(null, expected.y == p.determineQuadrant().y);
	}

	//dX = 0, dY = 0
	@Test
	public void determineQuadrant3() {
		Vector2 expected = new Vector2(0, 0);
		p = new MProjectile(0, 0, 0, 0, 10);
		assertTrue(null, expected.x == p.determineQuadrant().x);
		assertTrue(null, expected.y == p.determineQuadrant().y);
	}
	
}
