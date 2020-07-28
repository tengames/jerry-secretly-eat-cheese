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
import woodyx.basicapi.screen.Asset;
import woodyx.basicapi.sound.SoundManager;
import woodyx.basicapi.sprite.ObjectSprite;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.equations.Elastic;
import aurelienribon.tweenengine.equations.Linear;
import aurelienribon.tweenengine.equations.Quart;

import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.tengames.jerrysecretlyeatcheese.main.Assets;

public class MenuPresentation {
	private TweenManager tweenPrimary, tweenSecondary;
	private ParticleEffect paShoot;
	private ObjectSprite spPriamry, spSecondary, spLogo;
	private boolean isComplete;

	public MenuPresentation(Vector2 position, Vector2 target, float delay, int type) {
		this.isComplete = false;
		tweenPrimary = new TweenManager();

		switch (type) {
		// canon
		case 1:
			spPriamry = new ObjectSprite(Assets.taObjects.findRegion("canon"), position.x, position.y);
			spSecondary = new ObjectSprite(Assets.taObjects.findRegion("wheel"), position.x - 20, position.y - 15);
			spPriamry.setOrigin(27, spPriamry.getHeight() / 2);
			spSecondary.setOriginCenter(spSecondary);

			// create tween
			tweenSecondary = new TweenManager();

			Tween.to(spPriamry, SpriteAccessor.POS_XY, 3f).ease(Linear.INOUT).target(target.x, target.y)
					.start(tweenPrimary);

			Timeline.createParallel()
					.push(Tween.to(spSecondary, SpriteAccessor.ROTATION, 3f).ease(Linear.INOUT).target(-360))
					.push(Tween.to(spSecondary, SpriteAccessor.POS_XY, 3f).ease(Linear.INOUT).target(target.x - 20,
							target.y - 15))
					.setCallback(new TweenCallback() {
						@Override
						public void onEvent(int arg0, BaseTween<?> arg1) {
							// rotate canon
							Tween.to(spPriamry, SpriteAccessor.ROTATION, 1f).ease(Linear.INOUT).target(60)
									.setCallback(new TweenCallback() {
										@Override
										public void onEvent(int arg0, BaseTween<?> arg1) {
											// play sound
											SoundManager.playSound(Assets.soShoot);
											// create logo
											createLogo();
											// create effect
											paShoot = Asset.loadParticleEffect("effects/firework.p",
													Assets.taParticles);
											paShoot.setPosition(170, 210);
											paShoot.start();
											// complete
											isComplete = true;
										}
									}).start(tweenPrimary);
						}
					}).start(tweenSecondary);
			break;

		case 2:
			// jerry
			spPriamry = new ObjectSprite(Assets.taObjects.findRegion("jerry-menu"), position.x, position.y);

			Tween.to(spPriamry, SpriteAccessor.POS_XY, 1f).delay(delay).ease(Quart.OUT).target(target.x, target.y)
					.start(tweenPrimary);
			break;

		case 3:
			// tom
			spPriamry = new ObjectSprite(Assets.taObjects.findRegion("tom-menu"), position.x, position.y);

			Tween.to(spPriamry, SpriteAccessor.POS_XY, 1f).delay(delay).ease(Linear.INOUT).target(target.x, target.y)
					.start(tweenPrimary);
			break;

		default:
			break;
		}
	}

	private void createLogo() {
		spLogo = new ObjectSprite(Assets.taObjects.findRegion("text-logo"),
				(800 - Assets.taObjects.findRegion("text-logo").getRegionWidth()) / 2, 350);
		spLogo.setOriginCenter(spLogo);
		Timeline.createParallel().pushPause(1f).push(Tween.set(spLogo, SpriteAccessor.OPACITY).target(0))
				.push(Tween.set(spLogo, SpriteAccessor.SCALE_XY).target(0, 0)).beginParallel()
				.push(Tween.to(spLogo, SpriteAccessor.OPACITY, 2f).target(1).ease(Elastic.INOUT))
				.push(Tween.to(spLogo, SpriteAccessor.SCALE_XY, 2f).target(1.2f, 1.2f).ease(Elastic.INOUT)).end()
				.push(Tween.set(spLogo, SpriteAccessor.SCALE_XY).target(1, 1))
				.push(Tween.to(spLogo, SpriteAccessor.SCALE_XY, 2f).target(1.2f, 1.2f).ease(Elastic.INOUT)
						.repeatYoyo(1000, 0))
				.start(tweenPrimary);
	}

	public void update(float deltaTime) {
		tweenPrimary.update(deltaTime);
		if (tweenSecondary != null)
			tweenSecondary.update(deltaTime);
		if (paShoot != null) {
			if (paShoot.isComplete())
				paShoot.start();
			paShoot.update(deltaTime);
		}
	}

	public void render(SpriteBatch batch) {
		if (paShoot != null)
			paShoot.draw(batch);
		spPriamry.draw(batch);
		if (spSecondary != null)
			spSecondary.draw(batch);
		if (spLogo != null)
			spLogo.draw(batch);
	}

	public boolean getComplete() {
		return isComplete;
	}

	public void lockComplete() {
		if (isComplete)
			this.isComplete = false;
	}
}
