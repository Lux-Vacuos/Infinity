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

import org.lwjgl.nanovg.NVGLUFramebuffer;

import net.luxvacuos.lightengine.client.rendering.glfw.Window;
import net.luxvacuos.lightengine.client.rendering.nanovg.themes.Theme.BackgroundStyle;
import net.luxvacuos.lightengine.client.ui.ITitleBar;

public interface IWindow {

	public enum WindowClose {
		DISPOSE, DO_NOTHING
	};

	public void init(Window window);

	public void initApp();

	public void renderApp();

	public void updateApp(float delta);

	public void alwaysUpdateApp(float delta);

	public void disposeApp();

	public void render(float delta, IWindowManager nanoWindowManager);

	public void update(float delta, IWindowManager nanoWindowManager);

	public void alwaysUpdate(float delta, IWindowManager nanoWindowManager);

	public void dispose();

	public void closeWindow();

	public boolean insideWindow();

	public void setDraggable(boolean draggable);

	public void setDecorations(boolean decorations);

	public void setResizable(boolean resizable);

	public void setCloseButton(boolean closeButton);

	public void setBackgroundStyle(BackgroundStyle backgroundStyle);

	public void setWindowClose(WindowClose windowClose);

	public void setBackgroundColor(float r, float g, float b, float a);

	public void setBackgroundColor(String hex);

	public void setHidden(boolean hidden);

	public void setAsBackground(boolean background);

	public void setAlwaysOnTop(boolean alwaysOnTop);

	public void setBlurBehind(boolean blur);

	public void setMinWidth(int width);

	public void setMinHeight(int height);

	public void extendFrame(int t, int b, int r, int l);

	public void setAnimationState(AnimationState animationState);
	
	public void setTransparentInput(boolean transparentInput);

	public void toggleMinimize();

	public void toggleTitleBar();

	public void toggleMaximize();

	public BackgroundStyle getBackgroundStyle();

	public int getWidth();

	public int getHeight();

	public int getX();

	public int getY();

	public int getFX();

	public int getFY();

	public int getFH();

	public int getFW();

	public String getTitle();

	public NVGLUFramebuffer getFBO();

	public AnimationState getAnimationState();

	public boolean hasDecorations();

	public boolean isResizable();

	public boolean isBackground();

	public boolean isDraggable();

	public boolean isDragging();

	public boolean isResizing();

	public boolean isMinimized();

	public boolean isHidden();

	public boolean isMaximized();

	public boolean isCompositor();
	
	public boolean isAnimating();

	public ITitleBar getTitleBar();

	public boolean shouldClose();

	public boolean hasBlurBehind();

	public boolean isAlwaysOnTop();
	
	public boolean hasTransparentInput();

	public void notifyWindow(int message, Object param);

	public void processWindowMessage(int message, Object param);

}
