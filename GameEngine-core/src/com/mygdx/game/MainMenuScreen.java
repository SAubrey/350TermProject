package com.mygdx.game; 

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;

/**
 * 
 * @author Sean Aubrey, Gabriel Fountain, Brandon Conn
 */
public class MainMenuScreen extends ScreenAdapter {

	/**   */
	private SpriteBatch batch;
	
	/**   */
	private BitmapFont font;
	
	/**   */
	private final ScreenManager sM;
	
	/**   */
	private OrthographicCamera camera;
	
	/**   */
	private Stage stage;
	
	/**   */
	private Table table;
	
	/**   */
	private Skin skin;
	
	/**   */
	private TextButtonStyle style;
	
	/**   */
	private TextButton play;
	
	/**   */
	private TextButton quit;
	
	/**  Y value in pixels.*/
	private int windowHeight;
	
	/**  X value in pixels.*/
	private int windowWidth;

	/**
	 * 
	 * @param screenManager
	 */
	public MainMenuScreen(final ScreenManager screenManager) {
		this.sM = screenManager;
		windowHeight = Gdx.graphics.getHeight();
		windowWidth = Gdx.graphics.getWidth();
		
		batch = new SpriteBatch();
		font =  new BitmapFont();

		camera = new OrthographicCamera();
		camera.setToOrtho(false, windowWidth, windowHeight);
		stage = new Stage();
		Gdx.input.setInputProcessor(stage);
		table = new Table();
		table.setFillParent(true);
		stage.addActor(table);
		
		Pixmap pixmap = new Pixmap(1, 1, Format.RGBA8888);
		pixmap.setColor(Color.WHITE);
		pixmap.fill();
		
		skin = new Skin();
		skin.add("white", new Texture(pixmap));
		skin.add("default", font);
		
		style = new TextButtonStyle();
		style.up = skin.newDrawable("white", Color.DARK_GRAY);
		style.down = skin.newDrawable("white", Color.LIGHT_GRAY);
		style.checked = skin.newDrawable("white", Color.DARK_GRAY);
		style.over = skin.newDrawable("white", Color.GRAY);
		style.font = skin.getFont("default");
		skin.add("default", style);
		
		play = new TextButton("Play", skin);
		play.setPosition(windowWidth / 2 - 50, windowHeight / 2 - 50, Align.center);
		table.add(play).minSize(150, 65);
		table.padBottom(-200);
		table.row();
		quit = new TextButton("Quit", skin);
		table.add(quit).minSize(150, 65).align(Align.center);
		addListeners();
	}
	
	/**
	 * 
	 */
	private void addListeners() {
		play.addListener(new ChangeListener() {
			public void changed(final ChangeEvent event, final Actor play) {
				sM.setScreen(new GameEngine(sM));
				dispose();
			}
		});
		
		quit.addListener(new ChangeListener() {
			public void changed(final ChangeEvent event, final Actor quit) {
				dispose();
				Gdx.app.exit();
			}
		});
	}

	/**
	 * 
	 * @param delta
	 */
	@Override
	public void render(final float delta) {
		Gdx.gl.glClearColor(0.05f, 0.05f, 0.05f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		stage.act();
		stage.draw();
		camera.update();
		
		batch.setProjectionMatrix(camera.combined);

		batch.begin();
		font.getData().setScale(2);
		font.draw(batch, "SENTIENT BALL PIT", windowWidth / 2 - 139, windowHeight / 2 + 100);
		font.getData().setScale(1);
		font.draw(batch, "Movement:  WASD\nShoot:  Left Mouse\nSpace:  Shotgun\nPause:  Esc",
				windowWidth / 2 - 60, windowHeight / 2 - 400);
		batch.end();
	}

	/**
	 * 
	 */
	@Override
	public void hide() {
	}

	/**
	 * 
	 */
	@Override
	public void dispose() {
		stage.dispose();
		skin.dispose();
		//super.dispose();
	}
}
