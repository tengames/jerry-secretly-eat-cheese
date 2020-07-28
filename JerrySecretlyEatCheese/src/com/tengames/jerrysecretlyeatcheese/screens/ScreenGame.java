/*
The MIT License

Copyright (c) 2014 kong <tengames.inc@gmail.com>

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/
package com.tengames.jerrysecretlyeatcheese.screens;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

import woodyx.basicapi.physics.BoxUtility;
import woodyx.basicapi.physics.ObjectModel;
import woodyx.basicapi.screen.Asset;
import woodyx.basicapi.screen.XScreen;
import woodyx.basicapi.sound.SoundManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Json;
import com.tengames.jerrysecretlyeatcheese.interfaces.GlobalVariables;
import com.tengames.jerrysecretlyeatcheese.main.Assets;
import com.tengames.jerrysecretlyeatcheese.main.JerrySecretlyEatCheese;
import com.tengames.jerrysecretlyeatcheese.objects.Canon;
import com.tengames.jerrysecretlyeatcheese.objects.CommonModelList;
import com.tengames.jerrysecretlyeatcheese.objects.DynamicButton;
import com.tengames.jerrysecretlyeatcheese.objects.DynamicDialog;
import com.tengames.jerrysecretlyeatcheese.objects.Grass;
import com.tengames.jerrysecretlyeatcheese.objects.IconModel;
import com.tengames.jerrysecretlyeatcheese.objects.Jerry;
import com.tengames.jerrysecretlyeatcheese.objects.StaticObject;

public class ScreenGame extends XScreen implements Screen, InputProcessor {
	private static int countObjects = 0;

	public static final byte STATE_NULL = 0;
	public static final byte STATE_WIN = 1;
	public static final byte STATE_LOOSE = 2;

	private byte numBullets[] = { 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8,
			8 };

	private JerrySecretlyEatCheese coreGame;
	private World world;
	@SuppressWarnings("unused")
	private ObjectModel ground;
	private Stage stage;
	private DynamicDialog dialog;
	private DynamicButton[] buttons;
	private ArrayList<StaticObject> objects;
	private ArrayList<Jerry> jerrys;
	private Canon canon;
	private Grass grass;
	private ParticleEffect paWin[];
	private BufferedReader reader;
	private String strJson;
	private float countHelp;
	private int numLevel, numBullet, numCake;
	private byte state;
	private boolean isSound;
//	private Skin skin;

	public ScreenGame(JerrySecretlyEatCheese coreGame, String strJson, int number) {
		super(800, 480);
		this.coreGame = coreGame;
		this.strJson = strJson;
		this.numLevel = number;
//		startDebugBox();
		initialize();
	}

	@Override
	public void initialize() {
		// create world
		world = new World(GlobalVariables.GRAVITY, true);

		// create stage
		stage = new Stage(800, 480, true);
		((OrthographicCamera) stage.getCamera()).setToOrtho(false, 800, 480);
		InputMultiplexer input = new InputMultiplexer(stage, this);
		// set input
		Gdx.input.setInputProcessor(input);
		Gdx.input.setCatchBackKey(true);

		// create smt
		initializeParams();
		createArrays();
		createModels();
		createUI();
		// check contact listener
		checkCollision();
	}

	private void createArrays() {
		// create array of terrans
		objects = new ArrayList<StaticObject>();
	}

	private void initializeParams() {
		// play music

		SoundManager.MUSIC_ENABLE = coreGame.androidListener.getSound();
		SoundManager.SOUND_ENABLE = coreGame.androidListener.getSound();

		isSound = !SoundManager.SOUND_ENABLE;

		SoundManager.playMusic(Assets.muBgGame, 0.5f, true);

		/* loading data */
//		try {
//			reader = new BufferedReader(new FileReader("/home/woodyx/workspace-gdx/JerrySecretlyEatCheeseAndroid/assets/data/jerryscretlyeatcheese.txt"));
//			reader = new BufferedReader(new FileReader("jerryscretlyeatcheese.txt"));
//		} catch (FileNotFoundException e) {}
		reader = coreGame.androidListener.getData();

		// create grass
		grass = new Grass(new Vector2(0, -30), new Vector2(800, 120), Assets.taGrass, "grass", "grass1", "grass2",
				"grass3");

		// initialize params
		state = STATE_NULL;
		jerrys = new ArrayList<Jerry>();
		numCake = 0;
		countHelp = 0;
		numBullet = numBullets[numLevel - 1];

		// trace
		if (numLevel < 10) {
			coreGame.androidListener.traceScene("0" + numLevel);
		} else {
			coreGame.androidListener.traceScene(numLevel + "");
		}

		// show admob
		if (numLevel % 5 == 0)
			coreGame.androidListener.showIntertitial();
	}

	private void createModels() {
		// create ground
		ground = new ObjectModel(world, ObjectModel.STATIC, ObjectModel.POLYGON, new Vector2(800, -200), new Vector2(),
				0, new Vector2(), 0, 100, 0.5f, 0.1f, GlobalVariables.CATEGORY_SCENERY, GlobalVariables.MASK_SCENERY,
				"ground");

		// create models
		if (strJson != null)
			createJsonModels();
	}

	private void createUI() {
		// load skin
//		skin = new Skin(Gdx.files.internal("drawable/objects/uiskin.json"), Assets.taSkin);

		// creat buttons
		buttons = new DynamicButton[6];

		// label stage
		buttons[0] = new DynamicButton(("Level: " + numLevel), new Vector2(10, 440), 0.5f);

		// label bullets
		buttons[1] = new DynamicButton(("Times: " + canon.getBullet()), new Vector2(160, 440), 0.7f);

		// label cheese
		buttons[2] = new DynamicButton(("Cheese: " + numCake), new Vector2(350, 440), 0.9f);

		// button menu
		buttons[3] = new DynamicButton(Assets.taObjects.findRegion("ic-menu"), new Vector2(680, 420), 0.5f, 1.1f);
		buttons[3].getButton().addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
//				 play sound
				SoundManager.playSound(Assets.soClick);
				// back to screen stage
				coreGame.setScreen(new ScreenStage(coreGame));
			}
		});

		// button replay
		buttons[4] = new DynamicButton(Assets.taObjects.findRegion("ic-replay"), new Vector2(740, 420), 0.5f, 1.3f);
		buttons[4].getButton().addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				// play sound
				SoundManager.playSound(Assets.soClick);
				// replay
				coreGame.setScreen(new ScreenGame(coreGame, strJson, numLevel));
			}
		});

		// button sound
		buttons[5] = new DynamicButton(Assets.taObjects.findRegion("bt-soundon"),
				Assets.taObjects.findRegion("bt-soundoff"), new Vector2(740, 20), 0.6f, 1.5f);
		buttons[5].getButton().setChecked(isSound);

		buttons[5].getButton().addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				// play sound
				SoundManager.playSound(Assets.soClick);
				// turn off sound and music
				SoundManager.MUSIC_ENABLE = !SoundManager.MUSIC_ENABLE;
				if (!SoundManager.MUSIC_ENABLE) {
					SoundManager.pauseMusic(Assets.muBgGame);
				} else {
					SoundManager.playMusic(Assets.muBgGame, 0.5f, true);
				}
				SoundManager.SOUND_ENABLE = !SoundManager.SOUND_ENABLE;
				// save sound
				coreGame.androidListener.setSound(SoundManager.SOUND_ENABLE);
			}
		});

		// back to screen scenario
		/*
		 * TextButton btBack = new TextButton("Back", skin); btBack.setPosition(20,
		 * 400); btBack.addListener(new ChangeListener() {
		 * 
		 * @Override public void changed(ChangeEvent event, Actor actor) {
		 * coreGame.setScreen(new ScreenScenario(coreGame)); } });
		 */

		// add to stage
//		 stage.addActor(btBack);
		for (DynamicButton button : buttons) {
			if (button != null)
				stage.addActor(button);
		}

	}

	/* generate map */
	private void createJsonModels() {
		Json json = new Json();
		CommonModelList jsList = new CommonModelList();
		jsList = json.fromJson(CommonModelList.class, strJson);
		// generate map
		for (int i = 0; i < jsList.getSize(); i++) {
			switch (jsList.getModel(i).getType()) {
			/* create objects */
			// case tom
			case IconModel.TOM:
				StaticObject tom = new StaticObject(world, jsList.getModel(i).getType(),
						jsList.getModel(i).getPosition(), jsList.getModel(i).getSize(),
						jsList.getModel(i).getRotation(), ("tom" + countObjects));
				objects.add(tom);
				countObjects++;
				break;
			// case cake
			case IconModel.CAKE:
				numCake++;
				StaticObject cake = new StaticObject(world, jsList.getModel(i).getType(),
						jsList.getModel(i).getPosition(), jsList.getModel(i).getSize(),
						jsList.getModel(i).getRotation(), ("cake" + countObjects));
				objects.add(cake);
				countObjects++;
				break;
			// case brick
			case IconModel.BRICK:
				StaticObject brick = new StaticObject(world, jsList.getModel(i).getType(),
						jsList.getModel(i).getPosition(), jsList.getModel(i).getSize(),
						jsList.getModel(i).getRotation(), "brick");
				objects.add(brick);
				break;
			// case canon
			case IconModel.CANON:
				if (canon == null)
					canon = new Canon(Assets.taObjects.findRegion("canon"),
							new Vector2(jsList.getModel(i).getPosition().x - 250, jsList.getModel(i).getPosition().y),
							jsList.getModel(i).getSize(), jsList.getModel(i).getPosition(), numBullet);
				break;
			default:
				break;
			}
		}

		// free models
		jsList.dispose();
		jsList = null;
	}

	private void export(int number) {
		String line = null, strExport = "MAP: " + (number + 1);
		try {
			while ((line = reader.readLine()) != null) {
				if (line.equals(strExport)) {
					// export strJson
					strJson = reader.readLine();
					break;
				}
			}
			// close reader
			reader.close();
			// set new Screen
			coreGame.setScreen(new ScreenGame(coreGame, strJson, (number + 1)));
		} catch (IOException e) {
		}
	}

	private void checkFinish(float deltaTime) {
		if (canon.getBullet() <= 0 && jerrys.get(numBullet - 1).getFall()) {
			// check cake
			if (numCake > 0) {
				if (state == STATE_NULL) {
					createDialog(STATE_LOOSE);
					state = STATE_LOOSE;
				}
			} else {
				if (state == STATE_NULL) {
					createDialog(STATE_WIN);
					state = STATE_WIN;
				}
			}
		} else {
			if (numCake <= 0) {
				if (state == STATE_NULL) {
					createDialog(STATE_WIN);
					state = STATE_WIN;
				}
			}
		}
	}

	private void createDialog(byte type) {
		// pause music
		SoundManager.pauseMusic(Assets.muBgGame);

		// change input processor
		Gdx.input.setInputProcessor(stage);

		// create effects
		if (type == STATE_WIN) {
			// play sound
			SoundManager.playSound(Assets.soWin);
			// create effect
			paWin = new ParticleEffect[4];
			for (int i = 0; i < 4; i++) {
				paWin[i] = Asset.loadParticleEffect("effects/commet.p", Assets.taParticles);
			}
			paWin[0].setPosition(213, 386);
			paWin[1].setPosition(587, 386);
			paWin[2].setPosition(213, 94);
			paWin[3].setPosition(587, 94);
		} else {
			// play sound
			SoundManager.playSound(Assets.soFail);
		}

		Image imgDark = new Image(Assets.taObjects.findRegion("dark"));
		imgDark.setPosition(0, 0);
		imgDark.setSize(800, 480);

		WindowStyle windowStyle = new WindowStyle();
		windowStyle.titleFont = Assets.fNumber;
		dialog = new DynamicDialog(windowStyle, Assets.taObjects.findRegion("dialog"), new Vector2(374, 292),
				new Vector2(400, 900), new Vector2(400, 240), (numBullets[numLevel - 1] - canon.getBullet()),
				numBullets[numLevel - 1], type);

		if (type == STATE_WIN) {
			// save data
			coreGame.androidListener.setValue(numLevel - 1, dialog.getValue());

			// unlock new stage
			if (coreGame.androidListener.getValue(numLevel) == 0)
				coreGame.androidListener.setValue(numLevel, 1);

			int boomUsed = numBullets[numLevel - 1] - canon.getBullet();
			if (boomUsed <= 4) {
				// save hscore
				coreGame.androidListener.saveHscore(100);
			} else if (boomUsed > 4 && boomUsed <= 6) {
				// save hscore
				coreGame.androidListener.saveHscore(50);
			} else {
				// save hscore
				coreGame.androidListener.saveHscore(10);
			}

		}

		dialog.getBtMenu().addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
//				 play sound
				SoundManager.playSound(Assets.soClick);
				// back to screen stage
				coreGame.setScreen(new ScreenStage(coreGame));
			}
		});

		dialog.getBtReplay().addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				// play sound
				SoundManager.playSound(Assets.soClick);
				// replay
				coreGame.setScreen(new ScreenGame(coreGame, strJson, numLevel));
			}
		});

		if (dialog.getBtNext() != null) {
			dialog.getBtNext().addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					// play sound
					SoundManager.playSound(Assets.soClick);
					if (numLevel == 30) {
						// return stage menu
						coreGame.setScreen(new ScreenStage(coreGame));
					} else {
						// next level
						coreGame.androidListener.setValue(numLevel, 1);
						export(numLevel);
					}
				}
			});
		}

		// add to stage
		stage.addActor(imgDark);
		stage.addActor(dialog);
	}

	private void createJerry() {
		// play sound
		SoundManager.playSound(Assets.soShoot);
		// create new jerry
		Jerry jerry = new Jerry(world, Assets.taObjects.findRegion("jerry"), canon.getShootPoint(),
				new Vector2(63 * canon.getRateScale(), 63 * canon.getRateScale()), canon.getPow(), canon.getAngle(),
				countObjects);
		jerrys.add(jerry);
		countObjects++;
		// decrease bullet
		canon.decreaseBullet();
		// set text
		buttons[1].setText("Times: " + canon.getBullet());
	}

	@Override
	public void update(float deltaTime) {
		updateWorld(deltaTime);
		checkFinish(deltaTime);
		updateObjects(deltaTime);
		updateStage(deltaTime);
	}

	private void updateWorld(float deltaTime) {
		world.step(deltaTime, 8, 3);
	}

	private void updateObjects(float deltaTime) {
		// update effects
		if (state == STATE_WIN) {
			if (paWin != null) {
				for (ParticleEffect paEff : paWin) {
					paEff.update(deltaTime);
				}
			}
		}

		// update jerrys
		if (!jerrys.isEmpty()) {
			for (Jerry jerry : jerrys) {
				if (jerry != null) {
					// update
					jerry.update(deltaTime);
					// check die
					if (jerry.getY() < -100)
						jerry.setFall();
					// check finish
					if (jerry.getCanHide()) {
						// check finish: fail
						if (state == STATE_NULL) {
							createDialog(STATE_LOOSE);
							state = STATE_LOOSE;
						}
					}
				}
			}
		}

		// update objects
		if (!objects.isEmpty()) {
			for (int i = 0; i < objects.size(); i++) {
				if (objects.get(i) != null) {
					objects.get(i).update(deltaTime);
					// descrease cake
					if (objects.get(i).getCanSetScore()) {
						// play sound
						SoundManager.playSound(Assets.soEat);
						// decrease butter
						numCake--;
						// set text
						buttons[2].setText("Cheese: " + numCake);
						objects.get(i).lockSetScore();
					}
					// remove dead cheese
					if (objects.get(i).getCanRemove()) {
						objects.remove(i);
					}
				}
			}
		}

		// update help
		if (numLevel == 1) {
			if (countHelp >= 0)
				countHelp += deltaTime;
			if (countHelp >= 10)
				countHelp = -1;
		}

		// update canon
		canon.update(deltaTime);

		// update grass
		grass.update(deltaTime);
	}

	private void updateStage(float deltaTime) {
		// update buttons
		for (DynamicButton button : buttons) {
			button.update(deltaTime);
		}
		// update dialog
		if (dialog != null) {
			dialog.update(deltaTime);
		}
	}

	@Override
	public void draw() {
		renderBackGround();
		renderObjects();
		renderStage();
	}

	private void renderBackGround() {
		bgDrawable(true);
		batch.draw(Assets.txBgGame, 0, 0, 800, 480);
		bgDrawable(false);
	}

	private void renderObjects() {
		objDrawable(true);
		renderHelp();
		renderStaticObjects();
		renderJerrys();
		renderCanon();
		renderGrass();
		renderEffects();
		objDrawable(false);
	}

	private void renderStaticObjects() {
		// render objects
		if (!objects.isEmpty()) {
			for (StaticObject object : objects) {
				object.draw(batch);
			}
		}
	}

	private void renderCanon() {
		canon.render(batch);
	}

	private void renderJerrys() {
		if (!jerrys.isEmpty()) {
			for (Jerry jerry : jerrys) {
				if (jerry != null) {
					jerry.render(batch);
				}
			}
		}
	}

	private void renderHelp() {
		// render help
		if (numLevel == 1) {
			if (countHelp != -1) {
				batch.draw(Assets.taObjects.findRegion("help-1"), 80, 220);
				batch.draw(Assets.taObjects.findRegion("help-2"), 210, 30);
			}
		}
	}

	private void renderEffects() {
		// render effects
		if (state == STATE_WIN) {
			if (paWin != null) {
				for (ParticleEffect paEff : paWin) {
					paEff.draw(batch);
				}
			}
		}
	}

	private void renderGrass() {
		grass.render(batch);
	}

	private void renderStage() {
		stage.draw();
	}

	@Override
	public void render(float deltaTime) {
		clearScreen(deltaTime);
		clearWorld();
		update(deltaTime);
		draw();
//		renderDebug(world);
	}

	private void clearWorld() {
		world.clearForces();
	}

	private void checkCollision() {
		world.setContactListener(new ContactListener() {

			@Override
			public void preSolve(Contact contact, Manifold oldManifold) {
			}

			@Override
			public void postSolve(Contact contact, ContactImpulse impulse) {
			}

			@Override
			public void endContact(Contact contact) {
			}

			@Override
			public void beginContact(Contact contact) {
				// jerry contact
				for (Jerry jerry : jerrys) {
					if (!jerry.getDie()) {
						for (StaticObject object : objects) {
							if (!object.getDie()) {
								if (BoxUtility.detectCollision(contact, jerry.getModel(), object.getModel())) {
									switch (object.getType()) {
									case IconModel.CAKE:
										if (!jerry.getDisable()) {
											// decrease cheese
											object.openSetScore();
											object.setDie();
										}
										break;
									case IconModel.TOM:
										jerry.setDie();
										break;
									default:
										break;
									}
								}
							}
						}
					}
				}
			}
		});
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void show() {
		// play music
		SoundManager.playMusic(Assets.muBgGame, 0.5f, true);
	}

	@Override
	public void hide() {
		// pause music, sound
		SoundManager.pauseMusic(Assets.muBgGame);
		switch (state) {
		case STATE_LOOSE:
			SoundManager.stopSound(Assets.soFail);
			break;
		case STATE_WIN:
			SoundManager.stopSound(Assets.soWin);
			break;
		default:
			break;
		}
	}

	@Override
	public void pause() {
		// pause music, sound
		SoundManager.pauseMusic(Assets.muBgGame);
		switch (state) {
		case STATE_LOOSE:
			SoundManager.stopSound(Assets.soFail);
			break;
		case STATE_WIN:
			SoundManager.stopSound(Assets.soWin);
			break;
		default:
			break;
		}
	}

	@Override
	public void resume() {
		// play music
		SoundManager.playMusic(Assets.muBgGame, 0.5f, true);
	}

	@Override
	public void dispose() {
		world.dispose();
		stage.dispose();
	}

	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		if (keycode == Keys.BACK) {
			// play sound
			SoundManager.playSound(Assets.soClick);
			// back to stage
			coreGame.setScreen(new ScreenStage(coreGame));
		}
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	Vector3 touchPoint = new Vector3();

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		camera.unproject(touchPoint.set(screenX, screenY, 0));
		// touch canon
		canon.getTouch(touchPoint.x, touchPoint.y);
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		camera.unproject(touchPoint.set(screenX, screenY, 0));
		// set shoot
		if (canon.getPrepare() && canon.getCanShoot() && (canon.getBullet() - 1 >= 0)) {
			createJerry();
			canon.setPrepare(false);
		}
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		camera.unproject(touchPoint.set(screenX, screenY, 0));
		// dragg canon
		canon.dragg(touchPoint.x, touchPoint.y);
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}
}
