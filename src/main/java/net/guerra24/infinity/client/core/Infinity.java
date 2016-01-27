/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2016 Guerra24
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

package net.guerra24.infinity.client.core;

import static org.lwjgl.opengl.GL11.GL_RENDERER;
import static org.lwjgl.opengl.GL11.GL_VENDOR;
import static org.lwjgl.opengl.GL11.GL_VERSION;
import static org.lwjgl.opengl.GL11.glGetString;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFW;

import net.guerra24.infinity.client.bootstrap.Bootstrap;
import net.guerra24.infinity.client.graphics.nanovg.Timers;
import net.guerra24.infinity.client.input.Mouse;
import net.guerra24.infinity.client.resources.GameResources;
import net.guerra24.infinity.client.util.Logger;

/**
 * The Kernel, Game Engine Core
 * 
 * @author Guerra24 <pablo230699@hotmail.com>
 * @category Kernel
 */
public class Infinity {

	/**
	 * Game Threads
	 */
	/**
	 * Game Data
	 */
	private GameResources gameResources;

	/**
	 * Constructor of the Kernel, Initializes the Game and starts the loop
	 */
	public Infinity() {
		mainLoop();
	}

	public Infinity(String test) {
		Logger.log("Running infinity in test mode");
	}

	/**
	 * PreInit phase, initialize the display and runs the API PreInit
	 * 
	 */
	public void preInit() {
		Logger.log("Loading");
		gameResources = GameResources.instance();
		Logger.log("Infinity Version: " + InfinityVariables.version);
		Logger.log("Build: " + InfinityVariables.build);
		Logger.log("Running on: " + Bootstrap.getPlatform());
		Logger.log("LWJGL Version: " + Version.getVersion());
		Logger.log("GLFW Version: " + GLFW.glfwGetVersionString());
		Logger.log("OpenGL Version: " + glGetString(GL_VERSION));
		Logger.log("Vendor: " + glGetString(GL_VENDOR));
		Logger.log("Renderer: " + glGetString(GL_RENDERER));
		gameResources.getDisplay().setVisible();

		if (Bootstrap.getPlatform() == Bootstrap.Platform.MACOSX) {
			InfinityVariables.runningOnMac = true;
		}
	}

	/**
	 * Init phase, initialize the game data (models,textures,music,etc) and runs
	 * the API Init
	 */
	public void init() {
		gameResources.init(this);
		gameResources.loadResources();
		Logger.log("Initializing Threads");
		gameResources.getRenderer().prepare();

	}

	/**
	 * PostInit phase, starts music and runs the API PostInit
	 */
	public void postInit() {
		gameResources.getSoundSystem().stop("menu1");
		gameResources.getSoundSystem().stop("menu2");
		if (gameResources.getRand().nextBoolean())
			gameResources.getSoundSystem().play("menu1");
		else
			gameResources.getSoundSystem().play("menu2");
		Mouse.setHidden(true);
		Timers.initDebugDisplay();
	}

	/**
	 * Infinity Main Loop
	 * 
	 */
	public void mainLoop() {
		preInit();
		init();
		postInit();
		float delta = 0;
		float accumulator = 0f;
		float interval = 1f / InfinityVariables.UPS;
		float alpha = 0;
		while (gameResources.getGlobalStates().loop) {
			Timers.startCPUTimer();
			if (gameResources.getDisplay().getTimeCount() > 1f) {
				CoreInfo.ups = CoreInfo.upsCount;
				CoreInfo.upsCount = 0;
				gameResources.getDisplay().setTimeCount(gameResources.getDisplay().getTimeCount() - 1);
			}
			delta = gameResources.getDisplay().getDelta();
			accumulator += delta;
			while (accumulator >= interval) {
				update(interval);
				accumulator -= interval;
			}
			alpha = accumulator / interval;
			Timers.stopCPUTimer();
			Timers.startGPUTimer();
			render(alpha);
			Timers.stopGPUTimer();
			Timers.update();
			gameResources.getDisplay().updateDisplay(InfinityVariables.FPS);
		}
		dispose();
	}

	/**
	 * Handles all render calls
	 * 
	 * @param delta
	 *            Delta value from Render Thread
	 */
	private void render(float delta) {
		gameResources.getGlobalStates().doRender(this, delta);
	}

	/**
	 * Handles all update calls
	 * 
	 * @param delta
	 *            Delta value from Update Thread
	 */
	private void update(float delta) {
		CoreInfo.upsCount++;
		gameResources.getGlobalStates().doUpdate(this, delta);
	}

	/**
	 * Disposes all game data
	 */
	public void dispose() {
		Logger.log("Closing Game");
		gameResources.cleanUp();
		gameResources.getDisplay().closeDisplay();
	}

	public GameResources getGameResources() {
		return gameResources;
	}

}