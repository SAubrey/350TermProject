
package com.mygdx.game;

import java.util.ArrayList;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

/**
 * GameEngine class is the core class that updates the game state 
 * graphically and physically every frame. It populates the window 
 * with a world, its initial {@link Box2D} physics objects, and its 
 * camera. It also manages user input.
 * 
 * @author Sean Aubrey, Gabriel Fountain, Brandon Conn
 */
public class GameEngine extends ApplicationAdapter {
	
	/** Window to viewport dimension ratio. */
	public static final int SCALE = 6;
	
	/**  Y value in pixels.*/
	public static int windowHeight;
	
	/**  X value in pixels.*/
	public static int windowWidth;
	
	/**  Circular player size in meters.*/
	public static int playerRadius = 1;
	
	/**  Circular projectile size in meters.*/
	public static float projectileRadius = 0.5f;
	
	/**  Box2D physical object management plane.*/
	public static World world;
	
	/**  Time between shots in seconds.*/
	private final float shotTime = 0.2f;
	
	/**  Applied acceleration upon movement in m/s^2.*/
	private final float playerAcceleration = 15.0f;
	
	/**  Collects visual elements to be updated together.*/
	private SpriteBatch batch;
	
	/**  A camera with an orthographic projection. */
	private OrthographicCamera camera;
	
	/**  Collects shape objects to be updated together.*/
	private ShapeRenderer sr;
	
	/**  X and Y physical wall shapes.*/
	private PolygonShape xWallBox, wallBox;
	
	/**  X and Y physical wall bodies.*/
	private Body wall, xWall;
	
	/**  Characteristics of Y wall.*/
	private BodyDef wallDef;
	
	/**  Characteristics of X wall.*/
	private BodyDef xWallDef;
	
	/**  Player object.*/
	private Player player;
	
	/**  All bodies detected in the world.*/
	private Array<Body> bodies;
	
	/**  Projectile bodies to be deleted.*/
	private ArrayList<Body> deletableProjectiles;
	
	/**  X position of the Player.*/
	private float x;
	
	/**  Y position of the Player.*/
	private float y;
	
	/**  Time counter before a shot is fired.*/
	private float shotAccumulator;
	
	/**
	 * Called once at creation to set up initial graphical objects 
	 * and create constant physical objects.
	 */
	@Override
	public void create() {
		batch = new SpriteBatch();
		sr = new ShapeRenderer();
		//img = new Texture("grid.jpg");
		world = new World(new Vector2(0, 0), true);
		
		camera = new OrthographicCamera();
		camera.setToOrtho(false, scale(windowWidth), scale(windowHeight));
		camera.position.set(scale(windowWidth) / 2, scale(windowHeight) / 2, 0);
		camera.update();
		
		player = new Player();
		deletableProjectiles = new ArrayList<Body>();
		bodies = new Array<Body>();
		createBorders();
	}

	/**
	 * This method updates constantly. 
	 * Updates graphical state.
	 * Updates physical state.
	 * Listens for user input.
	 * Performs a physics step of the world.
	 */
	@Override
	public void render() {
		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		checkResized();
		x = player.getX();
		y = player.getY();
		player.setPos();
		
		// Render images between begin and end. 
		sr.setProjectionMatrix(camera.combined);
		sr.begin(ShapeType.Filled);
		sr.setColor(1, 1, 1, 1);
		sr.circle(x, y, playerRadius);
		manageBodies();
		sr.end();
		
		// Render images between batch.begin and batch.end. 
		batch.begin();
		batch.setProjectionMatrix(camera.combined);
		batch.end();
		
		checkMovement();
		
		player.velocityCap();
		checkClick();
		
		camera.update();
		world.step(1 / 60f, 6, 2);
	}
	
	/**
	 * Updates a time accumulator with the time between frames
	 * and tells Player to fire a projectile if the mouse button
	 * clicked or held down.
	 */
	private void checkClick() {
		shotAccumulator += Gdx.graphics.getDeltaTime();
		if (shotAccumulator >= shotTime) {
			if (Gdx.input.isTouched()) {
			player.fireProjectile(Gdx.input.getX(),
					Gdx.input.getY());
			shotAccumulator = 0;
			}
		}
	}
	
	/**
	 * Retrieves all bodies in the world and bodies to be deleted to delete
	 * those that match. Also updates each body's graphical position.
	 */
	private void manageBodies() {
		deletableProjectiles = player.manageProjectiles(Gdx.graphics.getDeltaTime());
		world.getBodies(bodies);
		for (Body b : bodies) {
			sr.circle(b.getPosition().x, b.getPosition().y, projectileRadius);
			if (!deletableProjectiles.isEmpty()) {
				for (int i = 0; i < deletableProjectiles.size(); i++) {
					if (b.equals(deletableProjectiles.get(i))) {
						world.destroyBody(b);
						b.setUserData(null);
						b = null;
					}
				}
			}
		}
	}
	
	/**
	 * Checks the source of user input and sends which direction
	 * to be moved to Player. Also lets Player know if a key is not being 
	 * pressed so that that direction's movement can be slowed.
	 */
	private void checkMovement() {
		if (Gdx.input.isKeyPressed(Input.Keys.A)) {
			player.moveHorizontal(-playerAcceleration);
		} else if (!Gdx.input.isKeyPressed(Input.Keys.A)) {
			player.simulateResistance(1);
		}
		
		if (Gdx.input.isKeyPressed(Input.Keys.D)) {
			player.moveHorizontal(playerAcceleration);
		} else if (!Gdx.input.isKeyPressed(Input.Keys.D)) {
			player.simulateResistance(2);
		}
		
		if (Gdx.input.isKeyPressed(Input.Keys.W)) {
			player.moveVertical(playerAcceleration);
		} else if (!Gdx.input.isKeyPressed(Input.Keys.W)) {
			player.simulateResistance(3);
		}
		
		if (Gdx.input.isKeyPressed(Input.Keys.S)) {
			player.moveVertical(-playerAcceleration);
		} else if (!Gdx.input.isKeyPressed(Input.Keys.S)) {
			player.simulateResistance(4);
		}
	}
	
	/* Attempted fix of movement bugs. Almost works. Pairs with player.move
	private void checkMovement() {
		//sends 4 booleans to Player?
		boolean right = false, left = false, up = false, down = false;
		if (Gdx.input.isKeyPressed(Input.Keys.A)) {
			left = true;
			if (Gdx.input.isKeyPressed(Input.Keys.W)) {
				up = true;
			}
			else if (Gdx.input.isKeyPressed(Input.Keys.S)) {
				down = true;
			}
		}
		else if (Gdx.input.isKeyPressed(Input.Keys.D)) {
			right = true;
			if (Gdx.input.isKeyPressed(Input.Keys.W)) {
				up = true;
			}
			else if (Gdx.input.isKeyPressed(Input.Keys.S)) {
				down = true;
			}
		}
		player.move(right, left, up, down);
	}
	*/
	
	/**
	 * Scales pixel dimensions to the camera's viewport size so that physical
	 * object dimensions can be declared in units of meters to function 
	 * correctly within Box2D. In other words,
	 * rather than scaling the objects to 
	 * the window size, we scale the window size to the object.
	 * 
	 * @param val window dimension
	 * @return viewport dimension
	 */
	private float scale(final float val) {
		return val / SCALE;
	}
	
	/**
	 * Checks if the window has been resized and retrieves
	 * and assigns their new values.
	 */
	private void checkResized() {
		if (Gdx.graphics.getWidth() != windowWidth 
				|| Gdx.graphics.getHeight() != windowHeight) {
			windowWidth = Gdx.graphics.getWidth();
			windowHeight = Gdx.graphics.getHeight();
			resize(windowWidth, windowHeight);
		}
	}
	
	/**
	 * Adjusts camera view to new window dimensions.
	 * 
	 * @param width
	 * @param height
	 */
	@Override
	public void resize(final int width, final int height) {
        camera.viewportHeight = (scale(windowWidth) / width) * height;
        camera.viewportWidth = (scale(windowHeight) / height) * width;
	}
	
	/**
	 * Generates physical boundaries at the edge of the window.
	 */
	private void createBorders() {
		// Floor
		xWallDef = new BodyDef();
		xWallDef.position.set(0, 0);
		
		xWall = world.createBody(xWallDef);
		
		xWallBox = new PolygonShape();
		xWallBox.setAsBox(camera.viewportWidth, 0.0f);
		xWall.createFixture(xWallBox, 0.0f);
		
		// Ceiling
		xWallDef.position.set(0, camera.viewportHeight);
		xWall = world.createBody(xWallDef);
		xWallBox.setAsBox(camera.viewportWidth, 0.0f);
		xWall.createFixture(xWallBox, 0.0f);
		
		// Wall 1
		wallDef = new BodyDef();
		wallDef.position.set(0, 0);
		
		wall = world.createBody(wallDef);
		
		wallBox = new PolygonShape();
		wallBox.setAsBox(0, camera.viewportHeight);
		wall.createFixture(wallBox, 0.0f);
		
		// Wall 2
		wallDef.position.set(camera.viewportWidth, 0);
		wall = world.createBody(wallDef);
		wallBox.setAsBox(0, camera.viewportHeight);
		wall.createFixture(wallBox, 0.0f);
	}
	/*
	public class FPSLogger {
		long startTime;

		public FPSLogger() {
			startTime = TimeUtils.nanoTime();
		}

		// Logs the current frames per second to the console
		// 1,000,000,000ns == one second 
		public void log() {
			if (TimeUtils.nanoTime() - startTime > 1000000000)  {
				Gdx.app.log("FPSLogger", "fps: " 
					+ Gdx.graphics.getFramesPerSecond());
				startTime = TimeUtils.nanoTime();
			}
		}
	}
	*/
	
	/**
	 * Certain assets should be disposed of manually before exiting the application.
	 */
	@Override
	public void dispose() {
		batch.dispose();
		sr.dispose();
		xWallBox.dispose();
		wallBox.dispose();
	}
}
	