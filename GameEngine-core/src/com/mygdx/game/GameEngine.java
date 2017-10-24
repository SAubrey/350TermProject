/*
 * libGDX game development library suite
 * Shapes drawn with ShapeRenderer
 * Physics calculated with Box2d
 * 
 */

package com.mygdx.game;

import java.util.ArrayList;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
//import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

public class GameEngine extends ApplicationAdapter {
	
	public static final int SCALE = 6;
	public static int windowHeight;
	public static int windowWidth;
	public static int playerRadius = 1; // in units of meters
	public static float projectileRadius = 0.5f;
	public static World world;
	private final float shotTime = 0.5f;
	private final float playerAcceleration = 15.0f;
	private SpriteBatch batch;
	private Texture img;
	private OrthographicCamera camera;
	private ShapeRenderer sr;
	//private Box2DDebugRenderer debugRenderer;
	private PolygonShape xWallBox, wallBox;
	private Body wall, xWall;
	private BodyDef wallDef, xWallDef;
	private Player player;
	private Array<Body> bodies;
	private ArrayList<Body> deletableProjectiles;
	private float x, y, shotAccumulator;
	FPSLogger fps = new FPSLogger(); //debugging purposes
	
	@Override
	public void create() {
		batch = new SpriteBatch();
		sr = new ShapeRenderer();
		img = new Texture("grid.jpg");
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

	/*
	 * This method updates constantly. 
	 * All user control can be received directly from 
	 * here instead of making a controller class. (for now, anyway)
	 */
	@Override
	public void render() {
		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		checkResized();
		x = player.getX();
		y = player.getY();
		player.setPos();
		
		//ShapeRenderer works like batch with begin/end
		sr.setProjectionMatrix(camera.combined);
		sr.begin(ShapeType.Filled);
		sr.setColor(1, 1, 1, 1);
		sr.circle(x, y, playerRadius);
		manageBodies();
		sr.end();
		
		//Render images between batch.begin and batch.end. 
		//Collects and sends them together for efficiency
		batch.begin();
		batch.setProjectionMatrix(camera.combined);
		batch.end();
		
		checkMovement();
		
		player.velocityCap();
		checkClick();
		
		camera.update();
		fps.log();
		world.step(1 / 60f, 6, 2);
	}
	
	//create projectile, send it touch vector2
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
	
	private void manageBodies() {
		deletableProjectiles = player.manageProjectiles(Gdx.graphics.getDeltaTime());
		world.getBodies(bodies);
		
		for (Body b : bodies) {
			sr.circle(b.getPosition().x, b.getPosition().y, projectileRadius); // update visually
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
	
	/*
	 * Scales pixel quantity to the camera's viewport size.
	 * Because the physics of Box2D take the object sizes as units of meters
	 * to calculate mass, etc. In other words,
	 * rather than scaling the objects to 
	 * the camera, we scale the camera to the object.
	 */
	private float scale(final float val) {
		return val / SCALE;
	}
	
	private void checkResized() {
		if (Gdx.graphics.getWidth() != windowWidth || Gdx.graphics.getHeight() != windowHeight) {
			windowWidth = Gdx.graphics.getWidth();
			windowHeight = Gdx.graphics.getHeight();
			resize(windowWidth, windowHeight);
		}
	}
	
	/*
	 * Adjusts camera view to new window dimensions
	 */
	@Override
	public void resize(final int width, final int height) {
        camera.viewportHeight = (scale(windowWidth) / width) * height;
        camera.viewportWidth = (scale(windowHeight) / height) * width;
	}
	
	private void createBorders() {
		// floor
		xWallDef = new BodyDef();
		xWallDef.position.set(0, 0);
		
		xWall = world.createBody(xWallDef);
		
		xWallBox = new PolygonShape();
		xWallBox.setAsBox(camera.viewportWidth, 0.0f);
		xWall.createFixture(xWallBox, 0.0f);
		
		// ceiling
		xWallDef.position.set(0, camera.viewportHeight);
		xWall = world.createBody(xWallDef);
		xWallBox.setAsBox(camera.viewportWidth, 0.0f);
		xWall.createFixture(xWallBox, 0.0f);
		
		// wall 1
		wallDef = new BodyDef();
		wallDef.position.set(0, 0);
		
		wall = world.createBody(wallDef);
		
		wallBox = new PolygonShape();
		wallBox.setAsBox(0, camera.viewportHeight);
		wall.createFixture(wallBox, 0.0f);
		
		// wall 2
		wallDef.position.set(camera.viewportWidth, 0);
		wall = world.createBody(wallDef);
		wallBox.setAsBox(0, camera.viewportHeight);
		wall.createFixture(wallBox, 0.0f);
	}
	
	public class FPSLogger {
		long startTime;

		public FPSLogger() {
			startTime = TimeUtils.nanoTime();
		}

		// Logs the current frames per second to the console
		/* 1,000,000,000ns == one second */
		public void log() {
			if (TimeUtils.nanoTime() - startTime > 1000000000)  {
				Gdx.app.log("FPSLogger", "fps: " 
					+ Gdx.graphics.getFramesPerSecond());
				startTime = TimeUtils.nanoTime();
			}
		}
	}
	
	//Any asset should be disposed of manually before exiting the application
	@Override
	public void dispose() {
		batch.dispose();
		img.dispose();
		sr.dispose();
		xWallBox.dispose();
		wallBox.dispose();
	}
}
	