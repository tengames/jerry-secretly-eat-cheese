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
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.equations.Sine;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class Grass {
	private TextureAtlas atlas;
	private Sprite grassSprite;
	private Sprite grassPlotSprite1;
	private Sprite grassPlotSprite2;
	private Sprite grassPlotSprite3;

	private TweenManager tweenManager;

	/**
	 * Grass Class
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param textureAtlas
	 * @param item
	 * @param item1
	 * @param item2
	 * @param item3
	 */
	public Grass(Vector2 postion, Vector2 size, TextureAtlas textureAtlas, String item, String item1, String item2,
			String item3) {
		tweenManager = new TweenManager();

		atlas = textureAtlas;

		grassSprite = atlas.createSprite(item);
		grassSprite.setSize(size.x, size.y);
		grassSprite.setPosition(postion.x, postion.y);

		grassPlotSprite1 = atlas.createSprite(item1);
		grassPlotSprite1.setSize(size.x, size.y);
		grassPlotSprite1.setPosition(postion.x, postion.y);

		grassPlotSprite2 = atlas.createSprite(item2);
		grassPlotSprite2.setSize(size.x, size.y);
		grassPlotSprite2.setPosition(postion.x, postion.y);

		grassPlotSprite3 = atlas.createSprite(item3);
		grassPlotSprite3.setSize(size.x, size.y);
		grassPlotSprite3.setPosition(postion.x, postion.y);

		Tween.call(windCallback).start(tweenManager);
	}

	public void update(float deltaTime) {
		tweenManager.update(deltaTime);
	}

	public void render(SpriteBatch batch) {
		grassPlotSprite1.draw(batch);
		grassPlotSprite2.draw(batch);
		grassPlotSprite3.draw(batch);
	}

	public void dipose() {
		tweenManager.killAll();
	}

	/**
	 * wind effect
	 */
	private final TweenCallback windCallback = new TweenCallback() {
		@Override
		public void onEvent(int type, BaseTween<?> source) {
			float d = MathUtils.random() * 0.5f + 0.5f;
			float t = -0.125f * grassPlotSprite1.getHeight();
			float tt = -0.0625f * grassSprite.getHeight();

			Timeline.createParallel()
					.push(Tween.to(grassPlotSprite1, SpriteAccessor.SKEW_X2X3, d).target(t, t).ease(Sine.INOUT)
							.repeatYoyo(1, 0).setCallback(windCallback))
					.push(Tween.to(grassSprite, SpriteAccessor.SKEW_X2X3, d).target(tt, tt).ease(Sine.INOUT)
							.delay(d / 4).repeatYoyo(1, 0))
					.push(Tween.to(grassPlotSprite2, SpriteAccessor.SKEW_X2X3, d).target(t, t).ease(Sine.INOUT)
							.delay(d / 3).repeatYoyo(1, 0))
					.push(Tween.to(grassPlotSprite3, SpriteAccessor.SKEW_X2X3, d).target(t, t).ease(Sine.INOUT)
							.delay(d / 3 * 2).repeatYoyo(1, 0))
					.start(tweenManager);
		}
	};
}
