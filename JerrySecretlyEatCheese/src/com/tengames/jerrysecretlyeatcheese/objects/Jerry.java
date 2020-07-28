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

import woodyx.basicapi.physics.ObjectModel;
import woodyx.basicapi.screen.Asset;
import woodyx.basicapi.sprite.ObjectSprite;

import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.tengames.jerrysecretlyeatcheese.interfaces.GlobalVariables;
import com.tengames.jerrysecretlyeatcheese.main.Assets;

public class Jerry extends ObjectSprite {
	private ObjectModel model;
	private ParticleEffect paEffect;
	private float countHide;
	private boolean isDie, isFall, isProcDie, isProcHide, isDisable;

	public Jerry(World world, TextureRegion textureRegion, Vector2 position, Vector2 size, float pow, float angle,
			int index) {
		super(textureRegion, position.x, position.y, size.x, size.y);
		// initialize variables
		isDie = false;
		isFall = false;
		isProcDie = false;
		isProcHide = false;
		isDisable = false;
		countHide = 0;

		this.setOriginCenter(this);

		// create model
		model = new ObjectModel(world, ObjectModel.DYNAMIC, ObjectModel.CIRCLE,
				new Vector2(this.getWidth(), this.getHeight()), new Vector2(), this.getWidth() / 2, position, angle, 3,
				0.1f, 0.1f, GlobalVariables.CATEGORY_SCENERY, GlobalVariables.MASK_SCENERY, ("jerry" + index));
		model.getBody().setBullet(true);
		model.getBody().setLinearVelocity(new Vector2(pow * MathUtils.cos(angle), pow * MathUtils.sin(angle)));

		// creat fly effect
		paEffect = Asset.loadParticleEffect("effects/commet.p", Assets.taParticles);
		paEffect.setPosition(size.x / 2, size.y / 2);
		paEffect.start();

	}

	public void update(float deltaTime) {
		// update follow
		this.updateFollowModel(model);

		// update effect
		paEffect.setPosition((this.getX() + this.getWidth() / 2), (this.getY() + this.getHeight() / 2));
		paEffect.update(deltaTime);

		// check die
		if (isDie) {
			if (countHide >= 0)
				countHide += deltaTime;
			if (countHide >= 0.3f) {
				countHide = -1;
			}
			if (!isProcDie) {
				// set sensor
				model.getBody().getFixtureList().get(0).setSensor(true);
				// disable
				isDisable = true;
				isProcDie = true;
			}
		}

		// check fall
		if (isFall && !isProcDie) {
			// deactive model
			if (model.getBody().isActive())
				model.getBody().setActive(false);
			isProcDie = true;
		}

		// check hide
		if ((countHide == -1) && !isProcHide) {
			// deactive model
			if (model.getBody().isActive())
				model.getBody().setActive(false);
			// set new effect: explosion
			paEffect = Asset.loadParticleEffect("effects/firework.p", Assets.taParticles);
			paEffect.start();
			// turn on flag
			isProcHide = true;
		}
	}

	public void render(SpriteBatch batch) {
		if (!paEffect.isComplete())
			paEffect.draw(batch);
		if (countHide != -1)
			this.draw(batch);
	}

	public ObjectModel getModel() {
		return model;
	}

	public void setDie() {
		if (!isDie) {

			isDie = true;
		}
	}

	public void setFall() {
		if (!isFall)
			isFall = true;
	}

	public boolean getFall() {
		return isFall;
	}

	public boolean getCanHide() {
		if (countHide == -1)
			return true;
		return false;
	}

	public boolean getDie() {
		return this.isDie;
	}

	public boolean getDisable() {
		return this.isDisable;
	}

}
