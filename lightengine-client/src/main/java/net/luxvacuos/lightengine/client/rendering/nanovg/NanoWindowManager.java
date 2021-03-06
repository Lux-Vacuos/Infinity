/*
 * This file is part of Light Engine
 * 
 * Copyright (C) 2016-2019 Lux Vacuos
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

package net.luxvacuos.lightengine.client.rendering.nanovg;

import static net.luxvacuos.lightengine.universal.core.subsystems.CoreSubsystem.REGISTRY;
import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_LEFT;
import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_MIDDLE;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.lwjgl.glfw.GLFW;

import net.luxvacuos.lightengine.client.core.ClientVariables;
import net.luxvacuos.lightengine.client.rendering.glfw.Window;
import net.luxvacuos.lightengine.client.rendering.nanovg.IWindow.WindowClose;
import net.luxvacuos.lightengine.client.rendering.nanovg.compositor.GLCompositor;
import net.luxvacuos.lightengine.client.rendering.nanovg.compositor.ICompositor;
import net.luxvacuos.lightengine.client.rendering.nanovg.themes.Theme;
import net.luxvacuos.lightengine.client.rendering.opengl.GPUProfiler;
import net.luxvacuos.lightengine.universal.core.TaskManager;
import net.luxvacuos.lightengine.universal.core.subsystems.CoreSubsystem;
import net.luxvacuos.lightengine.universal.util.registry.KeyCache;

public class NanoWindowManager implements IWindowManager {

	private List<IWindow> windows;
	private Window window;
	private ICompositor compositor;
	private int width, height;
	private IWindow focused;
	private IShell shell;
	private boolean compositorEnabled;

	public NanoWindowManager(Window win) {
		this.window = win;
		compositorEnabled = (boolean) REGISTRY
				.getRegistryItem(KeyCache.getKey("/Light Engine/Settings/WindowManager/compositor"));
		windows = new ArrayList<>();
		width = win.getWidth();
		height = win.getHeight();
		if (compositorEnabled)
			compositor = new GLCompositor(win, width, height);

		REGISTRY.register(KeyCache.getKey("/Light Engine/Settings/WindowManager/shellHeight"), 0);
		shell = new DummyShell();
	}

	@Override
	public void render(float delta) {
		if (compositorEnabled) {
			GPUProfiler.start("UI");
			GPUProfiler.start("Render Windows");
			List<IWindow> windows = new ArrayList<>(this.windows);
			for (IWindow window : windows) {
				if (window.getFBO() != null) {
					GPUProfiler.start(window.getTitle());
					window.render(delta, this);
					GPUProfiler.end();
				}
			}
			GPUProfiler.end();
			compositor.render(windows, delta);
			GPUProfiler.start("Render To Screen");
			window.beingNVGFrame();
			Theme.renderImage(this.window.getNVGID(), 0, 0, window.getWidth(), window.getHeight(),
					compositor.getFinal().image(), 1f);
			if (ClientVariables.debug) {
				Timers.renderDebugDisplay(5, 24, 200, 55);
				Theme.renderText(window.getNVGID(), "Light Engine " + " (" + ClientVariables.version + ")",
						"Roboto-Bold", NVG_ALIGN_LEFT | NVG_ALIGN_MIDDLE, 5, 12, 20,
						Theme.rgba(220, 220, 220, 255, Theme.colorA));
				Theme.renderText(window.getNVGID(), "UPS: " + CoreSubsystem.ups, "Roboto-Bold",
						NVG_ALIGN_LEFT | NVG_ALIGN_MIDDLE, 5, 95, 20, Theme.rgba(220, 220, 220, 255, Theme.colorA));
				Theme.renderText(window.getNVGID(), "Used RAM: " + Runtime.getRuntime().totalMemory() / 1024 + "MB ",
						"Roboto-Bold", NVG_ALIGN_LEFT | NVG_ALIGN_MIDDLE, 5, 110, 20,
						Theme.rgba(220, 220, 220, 255, Theme.colorA));

			}
			this.window.endNVGFrame();
			GPUProfiler.end();
			GPUProfiler.end();
		} else {
			GPUProfiler.start("UI");
			GPUProfiler.start("Render Windows");
			List<IWindow> windows = new ArrayList<>(this.windows);
			window.beingNVGFrame();
			for (IWindow window : windows) {
				GPUProfiler.start(window.getTitle());
				window.render(delta, this);
				GPUProfiler.end();
			}
			if (ClientVariables.debug) {
				Timers.renderDebugDisplay(5, 24, 200, 55);
				Theme.renderText(window.getNVGID(), "Light Engine " + " (" + ClientVariables.version + ")",
						"Roboto-Bold", NVG_ALIGN_LEFT | NVG_ALIGN_MIDDLE, 5, 12, 20,
						Theme.rgba(220, 220, 220, 255, Theme.colorA));
				Theme.renderText(window.getNVGID(), "UPS: " + CoreSubsystem.ups, "Roboto-Bold",
						NVG_ALIGN_LEFT | NVG_ALIGN_MIDDLE, 5, 95, 20, Theme.rgba(220, 220, 220, 255, Theme.colorA));
				Theme.renderText(window.getNVGID(), "Used RAM: " + Runtime.getRuntime().totalMemory() / 1024 + "MB ",
						"Roboto-Bold", NVG_ALIGN_LEFT | NVG_ALIGN_MIDDLE, 5, 110, 20,
						Theme.rgba(220, 220, 220, 255, Theme.colorA));

			}
			this.window.endNVGFrame();
			GPUProfiler.end();
			GPUProfiler.end();

		}
	}

	@Override
	public void update(float delta) {
		List<IWindow> tmp = new ArrayList<>();
		IWindow toTop = null;
		for (IWindow window : new ArrayList<>(this.windows)) {
			if (window.shouldClose()) {
				notifyClose(window);
				TaskManager.tm.addTaskRenderThread(() -> {
					window.dispose();
				});
				tmp.add(window);
				continue;
			}

			if (window.insideWindow() && !window.isBackground() && !window.isHidden() && !window.isMinimized()
					&& !window.isAnimating()
					&& (this.window.getMouseHandler().isButtonPressed(0)
							|| this.window.getMouseHandler().isButtonPressed(1))
					&& !focused.isDragging() && !focused.isResizing()) {
				toTop = window;
			}
		}
		windows.removeAll(tmp);
		tmp.clear();
		if (toTop != null) {
			bringToFront(toTop);
			shell.notifyWindow(WindowMessage.WM_SHELL_WINDOW_FOCUS, toTop);
		}
		tmp.addAll(windows);
		Collections.reverse(tmp);
		for (IWindow window : tmp) {
			if (window.insideWindow() && !window.isHidden() && !window.isMinimized() && !window.isAnimating()
					&& !focused.isDragging() && !focused.isResizing()) {
				focused = window;
				break;
			}
		}
		if (focused != null)
			focused.update(delta, this);
		for (IWindow window : tmp) {
			window.alwaysUpdate(delta, this);
		}
		tmp.clear();
		if (window.getKeyboardHandler().isKeyPressed(GLFW.GLFW_KEY_F1))
			ClientVariables.debug = !ClientVariables.debug;
	}

	@Override
	public void dispose() {
		if (compositorEnabled) {
			compositor.dispose();
		}
		for (IWindow window : windows) {
			window.dispose();
		}
		windows.clear();
	}

	public List<IWindow> getWindows() {
		return windows;
	}

	@Override
	public void addWindow(IWindow window) {
		TaskManager.tm.addTaskMainThread(() -> {
			window.init(this.window);
			window.update(0, this);
			window.alwaysUpdate(0, this);
			this.windows.add(window);
			this.focused = window;
			notifyAdd(window);
		});
	}

	@Override
	public void addWindow(int ord, IWindow window) {
		TaskManager.tm.addTaskMainThread(() -> {
			window.init(this.window);
			window.update(0, this);
			window.alwaysUpdate(0, this);
			this.windows.add(ord, window);
			this.focused = window;
			notifyAdd(window);
		});
	}

	@Override
	public void removeWindow(IWindow window) {
		window.notifyWindow(WindowMessage.WM_CLOSE, null);
	}

	@Override
	public void bringToFront(IWindow window) {
		if (windows.size() <= 1)
			return;
		IWindow top = windows.get(windows.size() - 1);
		if (top != window)
			if (!top.isAlwaysOnTop() && !top.isHidden()) {
				windows.remove(window);
				windows.add(window);
			}
	}

	@Override
	public boolean isOnTop(IWindow window) {
		return windows.get(windows.size() - 1) == window;
	}

	@Override
	public boolean existWindow(IWindow window) {
		return windows.contains(window);
	}

	@Override
	public void notifyClose(IWindow window) {
		this.shell.notifyWindow(WindowMessage.WM_SHELL_WINDOW_CLOSED, window);
	}

	@Override
	public void notifyAdd(IWindow window) {
		if (window.hasDecorations() && !window.isHidden())
			this.shell.notifyWindow(WindowMessage.WM_SHELL_WINDOW_CREATED, window);
	}

	@Override
	public void enableCompositor() {
		if (compositorEnabled)
			return;
		TaskManager.tm.addTaskRenderThread(() -> {
			compositor = new GLCompositor(window, width, height);
			compositorEnabled = true;
			REGISTRY.register(KeyCache.getKey("/Light Engine/Settings/WindowManager/compositor"), compositorEnabled);
		});
		notifyAllWindows(WindowMessage.WM_COMPOSITOR_ENABLED, null);
	}

	@Override
	public void disableCompositor() {
		if (!compositorEnabled)
			return;
		TaskManager.tm.addTaskRenderThread(() -> compositor.dispose());
		notifyAllWindows(WindowMessage.WM_COMPOSITOR_DISABLED, null);
		compositorEnabled = false;
		REGISTRY.register(KeyCache.getKey("/Light Engine/Settings/WindowManager/compositor"), compositorEnabled);
	}

	@Override
	public void resize(int width, int height) {
		this.width = width;
		this.height = height;
		notifyAllWindows(WindowMessage.WM_RESIZE, null);
		if (compositorEnabled)
			compositor.resize(width, height);
	}

	@Override
	public void closeAllWindows() {
		notifyAllWindows(WindowMessage.WM_CLOSE, WindowClose.DISPOSE);
	}

	@Override
	public void notifyAllWindows(int message, Object param) {
		for (IWindow window : windows) {
			window.notifyWindow(message, param);
		}
	}

	@Override
	public IWindow getWindowByClass(String clazz) {
		for (IWindow window : windows) {
			if (window.getClass().getSimpleName().equals(clazz))
				return window;
		}
		return null;
	}

	@Override
	public int getTotalWindows() {
		return windows.size();
	}

	@Override
	public void setShell(IShell shell) {
		this.shell = shell;
	}

	@Override
	public void toggleShell() {
		shell.toggleShell();
	}

	@Override
	public boolean isShellEnabled() {
		return shell.isEnabled();
	}

	@Override
	public IShell getShell() {
		return shell;
	}

}
