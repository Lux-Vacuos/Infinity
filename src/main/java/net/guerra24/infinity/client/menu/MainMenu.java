/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Guerra24
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.guerra24.infinity.client.menu;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import net.guerra24.infinity.client.core.InfinityVariables;
import net.guerra24.infinity.client.graphics.MenuRendering;
import net.guerra24.infinity.client.resources.GameResources;
import net.guerra24.infinity.client.resources.models.FontType;
import net.guerra24.infinity.client.resources.models.GUIText;
import net.guerra24.infinity.universal.util.vector.Vector2f;

public class MainMenu {

	private Button playButton;
	private Button exitButton;
	private Button optionsButton;
	private List<GUIText> texts;

	private float xScale, yScale;

	public MainMenu(GameResources gm) {
		FontType font = gm.getTextHandler().getFont();
		float width = InfinityVariables.WIDTH;
		float height = InfinityVariables.HEIGHT;
		yScale = height / 720f;
		xScale = width / 1280f;
		playButton = new Button(new Vector2f(177 * xScale, 532 * yScale), new Vector2f(215, 80));
		exitButton = new Button(new Vector2f(177 * xScale, 224 * yScale), new Vector2f(215, 80));
		optionsButton = new Button(new Vector2f(177 * xScale, 376 * yScale), new Vector2f(215, 80));

		texts = new ArrayList<GUIText>();
		GUIText textVersion = new GUIText("Infinity Engine " + InfinityVariables.version + " " + InfinityVariables.state
				+ " Build " + InfinityVariables.build, 1, font, new Vector2f(0.002f, 0.97f), 1, false);
		textVersion.setColour(0.79f, 0.79f, 0.79f);
		texts.add(textVersion);
		GUIText textMAC = new GUIText("Infinity is running on OSX, some things did not work well", 1, font,
				new Vector2f(0.002f, 0.002f), 1, false);
		textMAC.setColour(1, 0, 0);
		if (InfinityVariables.runningOnMac)
			texts.add(textMAC);
	}

	public void render() {
		MenuRendering.renderButton(null, "Play", "Roboto-Bold", 170 * xScale, 112 * yScale, 215 * xScale, 80 * yScale,
				MenuRendering.rgba(255, 255, 255, 255, MenuRendering.colorA), playButton.insideButton());
		MenuRendering.renderButton(null, "Options", "Roboto-Bold", 170 * xScale, 270 * yScale, 215 * xScale,
				80 * yScale, MenuRendering.rgba(255, 255, 255, 255, MenuRendering.colorA),
				optionsButton.insideButton());
		MenuRendering.renderButton(null, "Exit", "Roboto-Bold", 170 * xScale, 425 * yScale, 215 * xScale, 80 * yScale,
				MenuRendering.rgba(255, 255, 255, 255, MenuRendering.colorA), exitButton.insideButton());
	}

	public void load(GameResources gm) {
		gm.getTextHandler().switchTo(texts);
	}

	public Button getPlayButton() {
		return playButton;
	}

	public Button getExitButton() {
		return exitButton;
	}

	public Button getOptionsButton() {
		return optionsButton;
	}

}
