/*
 * This file is part of Light Engine
 * 
 * Copyright (C) 2016-2017 Lux Vacuos
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package net.luxvacuos.lightengine.client.core.subsystems;

import static net.luxvacuos.lightengine.universal.core.subsystems.CoreSubsystem.REGISTRY;
import static org.lwjgl.assimp.Assimp.aiGetVersionMajor;
import static org.lwjgl.assimp.Assimp.aiGetVersionMinor;
import static org.lwjgl.assimp.Assimp.aiGetVersionRevision;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.opengl.GL11.GL_BACK;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_RENDERER;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_VENDOR;
import static org.lwjgl.opengl.GL11.GL_VERSION;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glCullFace;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glGetString;
import static org.lwjgl.opengl.GL20.GL_SHADING_LANGUAGE_VERSION;
import static org.lwjgl.opengl.GL32.GL_TEXTURE_CUBE_MAP_SEAMLESS;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;

import net.luxvacuos.igl.Logger;
import net.luxvacuos.lightengine.client.core.ClientVariables;
import net.luxvacuos.lightengine.client.core.states.SplashScreenState;
import net.luxvacuos.lightengine.client.rendering.glfw.Icon;
import net.luxvacuos.lightengine.client.rendering.glfw.PixelBufferHandle;
import net.luxvacuos.lightengine.client.rendering.glfw.Window;
import net.luxvacuos.lightengine.client.rendering.glfw.WindowHandle;
import net.luxvacuos.lightengine.client.rendering.glfw.WindowManager;
import net.luxvacuos.lightengine.client.rendering.nanovg.IWindowManager;
import net.luxvacuos.lightengine.client.rendering.nanovg.NanoWindowManager;
import net.luxvacuos.lightengine.client.rendering.nanovg.Timers;
import net.luxvacuos.lightengine.client.rendering.nanovg.themes.NanoTheme;
import net.luxvacuos.lightengine.client.rendering.nanovg.themes.ThemeManager;
import net.luxvacuos.lightengine.client.rendering.opengl.GLUtil;
import net.luxvacuos.lightengine.client.rendering.opengl.Renderer;
import net.luxvacuos.lightengine.client.rendering.opengl.objects.CachedAssets;
import net.luxvacuos.lightengine.client.rendering.opengl.objects.DefaultData;
import net.luxvacuos.lightengine.client.rendering.opengl.shaders.ShaderIncludes;
import net.luxvacuos.lightengine.client.resources.ResourceLoader;
import net.luxvacuos.lightengine.client.ui.Font;
import net.luxvacuos.lightengine.universal.core.GlobalVariables;
import net.luxvacuos.lightengine.universal.core.TaskManager;
import net.luxvacuos.lightengine.universal.core.states.StateMachine;
import net.luxvacuos.lightengine.universal.core.subsystems.EventSubsystem;
import net.luxvacuos.lightengine.universal.core.subsystems.ISubsystem;
import net.luxvacuos.lightengine.universal.util.registry.Key;

public class GraphicalSubsystem implements ISubsystem {

	private static IWindowManager windowManager;
	private static Window window;

	private static Font robotoRegular, robotoBold, poppinsRegular, poppinsLight, poppinsMedium, poppinsBold,
			poppinsSemiBold, entypo;

	@Override
	public void init() {
		REGISTRY.register(new Key("/Light Engine/Display/width"), ClientVariables.WIDTH);
		REGISTRY.register(new Key("/Light Engine/Display/height"), ClientVariables.HEIGHT);
		
		GLFW.glfwSetErrorCallback(GLFWErrorCallback.createPrint(System.err));

		if (!glfwInit())
			throw new IllegalStateException("Unable to initialize GLFW");

		Icon[] icons = new Icon[] { new Icon("icon32"), new Icon("icon64") };
		WindowHandle handle = WindowManager.generateHandle(
				(int) REGISTRY.getRegistryItem(new Key("/Light Engine/Display/width")),
				(int) REGISTRY.getRegistryItem(new Key("/Light Engine/Display/height")), GlobalVariables.PROJECT);
		handle.isVisible(false).setIcon(icons).setCursor("arrow").useDebugContext(GlobalVariables.debug);
		PixelBufferHandle pb = new PixelBufferHandle();
		pb.setSrgbCapable(1);
		handle.setPixelBuffer(pb);
		long gameWindowID = WindowManager.createWindow(handle,
				(boolean) REGISTRY.getRegistryItem(new Key("/Light Engine/Settings/Graphics/vsync")));

		window = WindowManager.getWindow(gameWindowID);
		window.setOnRefresh(() -> {
			Renderer.clearColors(0, 0, 0, 1);
			Renderer.clearBuffer(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			GraphicalSubsystem.getWindowManager().update(window.getDelta());
			GraphicalSubsystem.getWindowManager().render(window.getDelta());
		});
		GLUtil.init();
		initGL();

		REGISTRY.register(new Key("/Light Engine/System/lwjgl"), Version.getVersion());
		REGISTRY.register(new Key("/Light Engine/System/glfw"), GLFW.glfwGetVersionString());
		REGISTRY.register(new Key("/Light Engine/System/opengl"), glGetString(GL_VERSION));
		REGISTRY.register(new Key("/Light Engine/System/glsl"), glGetString(GL_SHADING_LANGUAGE_VERSION));
		REGISTRY.register(new Key("/Light Engine/System/vendor"), glGetString(GL_VENDOR));
		REGISTRY.register(new Key("/Light Engine/System/renderer"), glGetString(GL_RENDERER));
		REGISTRY.register(new Key("/Light Engine/System/assimp"),
				aiGetVersionMajor() + "." + aiGetVersionMinor() + "." + aiGetVersionRevision());
		REGISTRY.register(new Key("/Light Engine/System/vk"), "Not Available");

		ThemeManager.addTheme(new NanoTheme());
		ThemeManager.setTheme((String) REGISTRY.getRegistryItem(new Key("/Light Engine/Settings/WindowManager/theme")));

		setWindowManager(new NanoWindowManager(window));

		Timers.initDebugDisplay();
		ResourceLoader loader = window.getResourceLoader();
		robotoRegular = loader.loadNVGFont("Roboto-Regular", "Roboto-Regular");
		robotoBold = loader.loadNVGFont("Roboto-Bold", "Roboto-Bold");
		poppinsRegular = loader.loadNVGFont("Poppins-Regular", "Poppins-Regular");
		poppinsLight = loader.loadNVGFont("Poppins-Light", "Poppins-Light");
		poppinsMedium = loader.loadNVGFont("Poppins-Medium", "Poppins-Medium");
		poppinsBold = loader.loadNVGFont("Poppins-Bold", "Poppins-Bold");
		poppinsSemiBold = loader.loadNVGFont("Poppins-SemiBold", "Poppins-SemiBold");
		entypo = loader.loadNVGFont("Entypo", "Entypo", 40);

		TaskManager.addTaskAsync(() -> ShaderIncludes.processIncludeFile("common.isl"));
		TaskManager.addTaskAsync(() -> ShaderIncludes.processIncludeFile("lighting.isl"));
		TaskManager.addTaskAsync(() -> ShaderIncludes.processIncludeFile("materials.isl"));
		TaskManager.addTaskAsync(() -> ShaderIncludes.processIncludeFile("global.isl"));
		TaskManager.addTaskAsync(() -> DefaultData.init(loader));
		StateMachine.registerState(new SplashScreenState());
		window.setVisible(true);
	}

	private void initGL() {
		glEnable(GL_DEPTH_TEST);
		glEnable(GL_CULL_FACE);
		glCullFace(GL_BACK);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glEnable(GL_TEXTURE_CUBE_MAP_SEAMLESS);

	}

	@Override
	public void restart() {
	}

	@Override
	public void update(float delta) {
	}

	@Override
	public void render(float delta) {
		if (!window.isIconified()) {
			if (window.wasResized()) {
				REGISTRY.register(new Key("/Light Engine/Display/width"), window.getWidth());
				REGISTRY.register(new Key("/Light Engine/Display/height"), window.getHeight());
				EventSubsystem.triggerEvent("lightengine.renderer.resize");
				windowManager.reloadCompositor();
			}
			CachedAssets.update(delta);
			Renderer.clearColors(0, 0, 0, 1);
			Renderer.clearBuffer(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			GraphicalSubsystem.getWindowManager().update(delta);
			GraphicalSubsystem.getWindowManager().render(delta);
		}
		WindowManager.update();
	}

	@Override
	public void dispose() {
		robotoRegular.dispose();
		robotoBold.dispose();
		poppinsRegular.dispose();
		poppinsLight.dispose();
		poppinsMedium.dispose();
		poppinsBold.dispose();
		poppinsSemiBold.dispose();
		entypo.dispose();
		DefaultData.dispose();
		Renderer.cleanUp();
		CachedAssets.dispose();
		windowManager.dispose();
		WindowManager.closeAllDisplays();
		GLFW.glfwTerminate();
	}

	public static void setWindowManager(IWindowManager iwm) {
		if (windowManager != null)
			windowManager.dispose();
		windowManager = iwm;
		Logger.log("Window Manager: " + iwm.getClass().getSimpleName());
	}

	public static IWindowManager getWindowManager() {
		return windowManager;
	}

	public static Window getMainWindow() {
		return window;
	}

}
