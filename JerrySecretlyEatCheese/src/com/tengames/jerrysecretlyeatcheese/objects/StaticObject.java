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
package com.tengames.jerrysecretlyeatcheese.objects;

import woodyx.basicapi.accessor.SpriteAccessor;
import woodyx.basicapi.physics.ObjectModel;
import woodyx.basicapi.sprite.ObjectSprite;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.TweenPaths;
import aurelienribon.tweenengine.equations.Elastic;
import aurelienribon.tweenengine.equations.Linear;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.tengames.jerrysecretlyeatcheese.interfaces.GlobalVariables;
import com.tengames.jerrysecretlyeatcheese.main.Assets;

public class StaticObject extends ObjectSprite {
	private ObjectModel mdObject;
	private TweenManager tween;
	private String name;
	private byte type;
	private boolean isDie, isProcDie, canRemove, canSetScore;

	public StaticObject(World world, byte type, Vector2 position, Vector2 size, float angle, String user) {
		super(position.x, position.y);
		this.type = type;
		this.isDie = false;
		this.isProcDie = false;
		this.canRemove = false;
		this.canSetScore = false;

		// set texture region for sprites
		switch (type) {
		case IconModel.TOM:
			name = "tom";
			break;
		case IconModel.BRICK:
			name = "brick";
			break;
		case IconModel.CAKE:
			name = "cake";
			// create tween
			tween = new TweenManager();
			Tween.to(this, SpriteAccessor.SCALE_XY, 1f + MathUtils.random(2)).ease(Linear.INOUT).target(0.8f, 0.8f)
					.path(TweenPaths.catmullRom).repeatYoyo(-1, 0).start(tween);
			break;
		default:
			break;
		}

		this.setRegion(Assets.taObjects.findRegion(name));
		this.setSize(size.x, size.y);
		this.setOriginCenter(this);

		// create models
		switch (type) {
		// basic shape
		case IconModel.BRICK:
			// create butter
			mdObject = new ObjectModel(world, ObjectModel.STATIC, ObjectModel.POLYGON,
					new Vector2(this.getWidth(), this.getHeight()), new Vector2(), 0, position, angle, 1, 0.3f, 0.1f,
					GlobalVariables.CATEGORY_SCENERY, GlobalVariables.MASK_SCENERY, user);
			break;

		case IconModel.TOM:
			mdObject = new ObjectModel(world, ObjectModel.STATIC, ObjectModel.CIRCLE,
					new Vector2(this.getWidth(), this.getHeight()), new Vector2(), this.getWidth() / 2, position, angle,
					3, 0.1f, 0.1f, GlobalVariables.CATEGORY_SCENERY, GlobalVariables.MASK_SCENERY, user);
			break;

		case IconModel.CAKE:
			mdObject = new ObjectModel(world, ObjectModel.STATIC, ObjectModel.CIRCLE,
					new Vector2(this.getWidth(), this.getHeight()), new Vector2(), this.getWidth() / 2, position, angle,
					3, 0.1f, 0.1f, GlobalVariables.CATEGORY_SCENERY, GlobalVariables.MASK_SCENERY, user);
			mdObject.getBody().getFixtureList().get(0).setSensor(true);
			break;

		default:
			break;
		}
	}

	public void update(float deltaTime) {
		// update tween
		if (tween != null)
			tween.update(deltaTime);
		// update follow
		if (!isDie)
			this.updateFollowModel(mdObject);
		// check die
		if (isDie && !isProcDie) {
			// deactive body
			if (mdObject.getBody().isActive())
				mdObject.getBody().setActive(false);
			// create new tween
			tween = new TweenManager();
			Tween.to(this, SpriteAccessor.SCALE_XY, 1f).ease(Elastic.IN).target(0, 0).setCallback(new TweenCallback() {
				@Override
				public void onEvent(int arg0, BaseTween<?> arg1) {
					canRemove = true;
				}
			}).start(tween);
			// turn off flag
			isProcDie = true;
		}
	}

	public ObjectModel getModel() {
		return this.mdObject;
	}

	public int getType() {
		return this.type;
	}

	public void setDie() {
		if (!isDie) {
			// play sound
//			SoundManager.playSound(Assets.soEat);
			isDie = true;
		}
	}

	public boolean getCanRemove() {
		return this.canRemove;
	}

	public boolean getDie() {
		return this.isDie;
	}

	public boolean getCanSetScore() {
		return canSetScore;
	}

	public void openSetScore() {
		if (!this.canSetScore)
			this.canSetScore = true;
	}

	public void lockSetScore() {
		if (this.canSetScore)
			this.canSetScore = false;
	}
}
