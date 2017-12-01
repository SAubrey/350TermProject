package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

/**
 * GameEngine class is the core class that updates the game state 
 * graphically and physically every frame. It populates the window 
 * with a world, its initial Box2D physics objects, and its 
 * camera. It also manages user input.
 * 
 * @author Sean Aubrey, Gabriel Fountain, Brandon Conn
 */
public class GameEngine implements Screen {
	
	/** Window to viewport dimension ratio. */
	public static final int SCALE = 6;
	
	/**  Y value in pixels.*/
	private static int windowHeight;
	
	/**  X value in pixels.*/
	private static int windowWidth;
	
	/** camera view height.*/
	private static int viewportHeight;
	
	/** camera view width.*/
	private static int viewportWidth;
	
	/**  Circular player size in meters.*/
	private static float playerRadius = 1f;
	
	/**  Circular projectile size in meters.*/
	private static float projectileRadius = 0.7f;
	
	private static float swarmerRadius = 0.8f;
	
	private static float spitterRadius = 2f;
	
	private static float demonRadius = 10f;
	
	private GameState state = GameState.RUN;
	
	private boolean flashRed;
	
	/**  Box2D physical object management plane.*/
	private static World world;
	
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
	
	/**  Attaches a physical body to its qualities.*/
	private Fixture fixture;
	
	/**  Player object.*/
	public Player player;
	
	private Listener listener;
	
	/**  All bodies detected in the world.*/
	private Array<Body> bodies;
	
	/**  X position of the Player.*/
	private float x;
	
	/**  Y position of the Player.*/
	private float y;
	
	/**  Time counter before a shot is fired.*/
	private float shotAccumulator;
	
	/**  Time counter before shotgun is fired.*/
	private float shotgunAccumulator;
	
	private EnemyManager eMan;
	
	private Texture healthBar;
	
	//private Texture vignette;
	
	private Texture background;
	
	Sound song;
	
	Sound shot;
	
	private float playerHealth = 100;
	
	final ScreenManager sM;
	
	/**
	 * Called once at creation to set up initial graphical objects 
	 * and create constant physical objects.
	 */
	public GameEngine(final ScreenManager screenManager) {
		this.sM = screenManager;
		//batch = new SpriteBatch();
		sr = new ShapeRenderer();
		//font = new BitmapFont();

		world = new World(new Vector2(0, 0), true);
		viewportHeight = (int) scale(windowHeight);
		viewportWidth = (int) scale(windowWidth);
		healthBar = new Texture("blank.png");
		//vignette = new Texture("vignette.png");
		background = new Texture("3dgrid.jpg");
		song = Gdx.audio.newSound(Gdx.files.internal("andreonate.mp3"));
		song.setLooping(song.play(), true);
		shot = Gdx.audio.newSound(Gdx.files.internal("kick.wav"));
		
		camera = new OrthographicCamera();
		camera.setToOrtho(false, scale(windowWidth), scale(windowHeight));
		camera.position.set(scale(windowWidth) / 2, scale(windowHeight) / 2, 0);
		camera.update();
		
		player = new Player();
		eMan = new EnemyManager(getViewWidth(), getViewHeight(), player);
		listener =  new Listener(this);
		world.setContactListener(listener);
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
	public void render(float delta) {
		switch (state) {
		case RUN:
			checkResized();
			x = player.getX();
			y = player.getY();
			player.setPos();
			eMan.update(x, y, getDeltaTime());

			checkMovement();
			checkClick();
			updateGraphics();
			camera.update();

			//long javaHeap = Gdx.app.getJavaHeap();
			//System.out.println("javaH: " + javaHeap);
			world.step(1 / 60f, 6, 2);
			break;
		case PAUSE:
			sM.setScreen(new PauseScreen(sM, this));
			break;
		case DEAD:
			sM.setScreen(new DeathScreen(sM, this));
			break;
		}
	}
	
	public static float getDeltaTime() {
		return Gdx.graphics.getDeltaTime();
	}
	
	private void updateGraphics() {
		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		if (flashRed) {
			Gdx.gl.glClearColor(0.3f, 0, 0, 1f);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			flashRed = false;
		}
		
		playerHealth = player.getHealth();
		float hRatio = playerHealth/100;
		// Render images between begin and end. 
		sr.setProjectionMatrix(camera.combined);
		sr.begin(ShapeType.Filled);
		sr.setColor(1, 1 * hRatio, 1 * hRatio, 1); // white to red fade
		sr.setColor(1, 1, 1, 1);
		sr.circle(x, y, playerRadius);
		manageBodies();
		sr.end();
		eMan.update(x,y, getDeltaTime());
		
		// Render images between batch.begin and batch.end. 
		
		sM.batch.begin();
		sM.batch.setProjectionMatrix(camera.combined);
		sM.batch.setColor(1,1,1,.2f);
		sM.batch.draw(background, 0, 0, viewportWidth, viewportHeight);
		//sM.batch.setColor(1,1,1,.5f);
		//sM.batch.draw(vignette, 0, 0, viewportWidth, viewportHeight);
		sM.batch.setColor(1, 1 * hRatio, 1 * hRatio, .8f);
		sM.batch.draw(healthBar, 0, 0, viewportWidth * hRatio, 0.5f);
		sM.batch.end();
	}
	
	public void flashRed() {
		flashRed = true;
	}
	
	/**
	 * Updates a time accumulator with the time between frames
	 * and tells Player to fire a projectile if the mouse button
	 * clicked or held down.
	 */
	private void checkClick() {
		shotAccumulator += Gdx.graphics.getDeltaTime();
		shotgunAccumulator += Gdx.graphics.getDeltaTime();
		if (shotAccumulator >= player.getShotTime()) {
			if (Gdx.input.isTouched()) {
			player.fireProjectile(scale(Gdx.input.getX()),
					scale(Gdx.input.getY()));
			shot.play(0.3f); // volume
			shotAccumulator = 0;
			}
		}
		if (shotgunAccumulator >= player.getShotgunTime()) {
			if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
			player.fireShotgun(scale(Gdx.input.getX()),
					scale(Gdx.input.getY()));
			shot.play(0.5f); // volume
			shotAccumulator = -1;
			shotgunAccumulator = 0;
			}
		}
		if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
			pause();
		}
	}
	
	/**
	 * Retrieves all bodies in the world and bodies to be deleted to delete
	 * those that match. Also updates each body's graphical position.
	 */
	private void manageBodies() {
		player.manageProjectiles(getDeltaTime());
		world.getBodies(bodies);
		for (Body b : bodies) {
			
			if (b.getUserData().equals("player")) {
				if (player.getHealth() <= 0) {
					dead();
				}
			}
			if (b.getUserData().equals("playerProj")) {
				sr.setColor(.95f, .95f, .95f, 1);
				sr.circle(b.getPosition().x, b.getPosition().y, projectileRadius);
			}
			if (b.getUserData().equals("enemyProj")) {
				sr.setColor(1f, .75f, .75f, 1);
				sr.circle(b.getPosition().x, b.getPosition().y, projectileRadius);
			}
			if (b.getUserData().equals("swarmer")) {
				sr.setColor(1f, .4f, .4f, 1);
				sr.circle(b.getPosition().x, b.getPosition().y, swarmerRadius);
			}
			if (b.getUserData().equals("spitter")) {
				sr.setColor(1f, .2f, .2f, 1);
				sr.circle(b.getPosition().x, b.getPosition().y, spitterRadius);
			}
			if (b.getUserData().equals("demon")) {
				sr.setColor(1, 0, 0, 1);
				sr.circle(b.getPosition().x, b.getPosition().y, demonRadius);
			}
			if (b.getUserData().equals("deletable")) {
				world.destroyBody(b);
			}
		}
	}
	
	/**
	 * Checks the source of user input and sends which direction
	 * to be moved to Player. Also lets Player know if a key is not being 
	 * pressed so that that direction's movement can be slowed.
	 */
	/*
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
	*/
	
	private void checkMovement() {
		boolean right = false, left = false, up = false, down = false;
		if (Gdx.input.isKeyPressed(Input.Keys.A)) {
			left = true;
		} else if (Gdx.input.isKeyPressed(Input.Keys.D)) {
			right = true;
		}
		if (Gdx.input.isKeyPressed(Input.Keys.W)) {
			up = true;
		} else if (Gdx.input.isKeyPressed(Input.Keys.S)) {
			down = true;
		}
		player.move(right, left, up, down);
	}
	
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
	public static float scale(final float val) {
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
	 * @param width window width.
	 * @param height window height.
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
		xWall.setUserData("wall");
		
		xWallBox = new PolygonShape();
		xWallBox.setAsBox(camera.viewportWidth, 0.0f);
		fixture = xWall.createFixture(xWallBox, 0.0f);
		fixture.setUserData(this);
		
		// Ceiling
		xWallDef.position.set(0, camera.viewportHeight);
		xWall = world.createBody(xWallDef);
		xWall.setUserData("wall");
		xWallBox.setAsBox(camera.viewportWidth, 0.0f);
		fixture = xWall.createFixture(xWallBox, 0.0f);
		fixture.setUserData(this);
		
		// Wall 1
		wallDef = new BodyDef();
		wallDef.position.set(0, 0);
		
		wall = world.createBody(wallDef);
		wall.setUserData("wall");
		
		wallBox = new PolygonShape();
		wallBox.setAsBox(0, camera.viewportHeight);
		fixture = wall.createFixture(wallBox, 0.0f);
		fixture.setUserData(this);
		
		// Wall 2
		wallDef.position.set(camera.viewportWidth, 0);
		wall = world.createBody(wallDef);
		wall.setUserData("wall");
		wallBox.setAsBox(0, camera.viewportHeight);
		fixture = wall.createFixture(wallBox, 0.0f);
		fixture.setUserData(this);
	}
	
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
	
	
	public void incrementKillCount() {
		player.incrementKillCount();
	}
	
	/**
	 * Returns the world instance.
	 * @return world.
	 */
	public static World getWorld() {
		return world;
	}
	
	/**
	 * Returns the size of Projectile's radius.
	 * @return projectile radius.
	 */
	public static float getProjRadius() {
		return projectileRadius;
	}
	
	/**
	 * Returns the size of the Player's radius.
	 * @return player radius.
	 */
	public static float getPlayRadius() {
		return playerRadius;
	}
	
	/**
	 * Returns the size of a Swarmer's radius.
	 * @return player radius.
	 */
	public static float getSwarmRadius() {
		return swarmerRadius;
	}
	
	/**
	 * Returns the size of a Spitter's radius.
	 * @return player radius.
	 */
	public static float getSpitterRadius() {
		return spitterRadius;
	}
	
	/**
	 * Returns the size of a Demon's radius.
	 * @return player radius.
	 */
	public static float getDemonRadius() {
		return demonRadius;
	}
	
	/**
	 * Returns the window's height in pixels.
	 * @return window height.
	 */
	public static int getWinHeight() {
		return windowHeight;
	}
	
	/**
	 * Sets the window's height in pixels.
	 * @param height window height.
	 */
	public static void setWinHeight(final int height) {
		windowHeight = height;
	}
	
	/**
	 * Returns the window's width in pixels.
	 * @return window width.
	 */
	public static int getWinWidth() {
		return windowWidth;
	}
	
	/**
	 * Sets the window's width in pixels.
	 * @param width window width.
	 */
	public static void setWinWidth(final int width) {
		windowWidth = width;
	}
	
	/**
	 * Returns the window's height in meters.
	 * @return window height.
	 */
	public static int getViewHeight() {
		return viewportHeight;
	}
	
	/**
	 * Returns the window's width in meters.
	 * @return window height.
	 */
	public static int getViewWidth() {
		return viewportWidth;
	}
	
	/**
	 * Certain assets should be disposed of manually before exiting the application.
	 */
	@Override
	public void dispose() {
		sr.dispose();
		world.dispose();
		eMan = null;
		//player = null;
		xWallBox.dispose();
		wallBox.dispose();
	}
	
	@Override
	public void pause() {
		state = GameState.PAUSE;
	}

	@Override
	public void show() {
	}

	@Override
	public void resume() {
		state = GameState.RUN;
	}
	
	public void dead() {
		state = GameState.DEAD;
	}

	@Override
	public void hide() {
	}
}
	