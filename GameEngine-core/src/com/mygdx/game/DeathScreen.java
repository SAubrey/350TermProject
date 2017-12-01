package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.utils.Align;

public class DeathScreen extends ScreenAdapter{
	
	public SpriteBatch batch;
	public BitmapFont font;
	final ScreenManager sM;
	private GameEngine gE;
	private OrthographicCamera camera;
	private Stage stage;
	private Table table;
	private Skin skin;
	private TextButtonStyle style;
	private TextButton play;
	private TextButton quit;
	
	/**  Y value in pixels.*/
	private int windowHeight;
	
	/**  X value in pixels.*/
	private int windowWidth;

	public DeathScreen(final ScreenManager screenManager, final GameEngine gE) {
		this.sM = screenManager;
		this.gE = gE;
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
		
		play = new TextButton("Restart", skin);
		play.setPosition(windowWidth/2 - 50, windowHeight/2 - 50, Align.center);
		table.add(play).minSize(150, 65);
		table.padBottom(-200);
		table.row();
		quit = new TextButton("Quit", skin);
		table.add(quit).minSize(150, 65).align(Align.center);
		addListeners();
	}
	
	private void addListeners() {
		play.addListener(new ChangeListener() {
			public void changed (ChangeEvent event, Actor play) {
				gE.dispose();
				gE = null;
				sM.setScreen(new MainMenuScreen(sM));
			}
		});
		
		quit.addListener(new ChangeListener() {
			public void changed (ChangeEvent event, Actor quit) {
				gE.dispose();
				Gdx.app.exit();
			}
		});
	}
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0.05f, 0.05f, 0.05f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		stage.act();
		stage.draw();
		camera.update();
		
		batch.setProjectionMatrix(camera.combined);

		batch.begin();
		font.getData().setScale(2);
		font.draw(batch, "YOU ARE DEAD", windowWidth/2 - 110, windowHeight/2 + 100);
		font.getData().setScale(1);
		font.draw(batch, "Score:  " + gE.player.getScore(), windowWidth/2 - 34, windowHeight/2 + 40); // nullptr here
		// // problem child
		batch.end();
	}
}