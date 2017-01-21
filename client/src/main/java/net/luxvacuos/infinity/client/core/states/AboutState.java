/*
 * This file is part of Infinity
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

package net.luxvacuos.infinity.client.core.states;

import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_MIDDLE;
import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_RIGHT;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;

import net.luxvacuos.infinity.client.core.ClientInternalSubsystem;
import net.luxvacuos.infinity.client.core.ClientVariables;
import net.luxvacuos.infinity.client.core.CoreInfo;
import net.luxvacuos.infinity.client.rendering.api.glfw.Window;
import net.luxvacuos.infinity.client.rendering.api.opengl.Renderer;
import net.luxvacuos.infinity.client.ui.UIButton;
import net.luxvacuos.infinity.client.ui.UIImage;
import net.luxvacuos.infinity.client.ui.UIText;
import net.luxvacuos.infinity.client.ui.UIWindow;
import net.luxvacuos.infinity.universal.core.AbstractInfinity;

/**
 * About State, this shows all the information about Infinity and the system where
 * is running.
 * 
 * @author danirod
 */
public class AboutState extends AbstractFadeState {

	private UIWindow uiWindow;
	private UIImage infinityLogo;

	public AboutState() {
		super(StateNames.ABOUT);
	}

	@Override
	public void init() {
		Window window = ClientInternalSubsystem.getInstance().getGameWindow();
		uiWindow = new UIWindow(20, window.getHeight() - 20, window.getWidth() - 40, window.getHeight() - 40, "About");
		UIButton backButton = new UIButton(window.getWidth() / 2f - 100, 40, 200, 40, "Back");
		backButton.setOnButtonPress((button, delta) -> {
			this.switchTo(StateNames.MAIN_MENU);
		});

		infinityLogo = new UIImage(uiWindow.getWidth() / 2 - 200, -40, 400, 200,
				ClientInternalSubsystem.getInstance().getGameWindow().getResourceLoader().loadNVGTexture("Infinity-Logo"));
		uiWindow.addChildren(infinityLogo);

		UIText versionL = new UIText("Version", 30, -300);
		versionL.setFont("Roboto-Bold");
		UIText versionR = new UIText(" (" + ClientVariables.version + ")", uiWindow.getWidth() - 30, -300);
		versionR.setAlign(NVG_ALIGN_RIGHT | NVG_ALIGN_MIDDLE);

		UIText osL = new UIText("Operative System", 30, -330);
		osL.setFont("Roboto-Bold");
		UIText osR = new UIText(CoreInfo.OS, uiWindow.getWidth() - 30, -330);
		osR.setAlign(NVG_ALIGN_RIGHT | NVG_ALIGN_MIDDLE);

		UIText lwjglL = new UIText("LWJGL Version", 30, -360);
		lwjglL.setFont("Roboto-Bold");
		UIText lwjglR = new UIText(CoreInfo.LWJGLVer, uiWindow.getWidth() - 30, -360);
		lwjglR.setAlign(NVG_ALIGN_RIGHT | NVG_ALIGN_MIDDLE);

		UIText glfwL = new UIText("GLFW Version", 30, -390);
		glfwL.setFont("Roboto-Bold");
		UIText glfwR = new UIText(CoreInfo.GLFWVer, uiWindow.getWidth() - 30, -390);
		glfwR.setAlign(NVG_ALIGN_RIGHT | NVG_ALIGN_MIDDLE);

		UIText openglL = new UIText("OpenGL Version", 30, -420);
		openglL.setFont("Roboto-Bold");
		UIText openglR = new UIText(CoreInfo.OpenGLVer, uiWindow.getWidth() - 30, -420);
		openglR.setAlign(NVG_ALIGN_RIGHT | NVG_ALIGN_MIDDLE);

		UIText vkL = new UIText("Vulkan Version", 30, -450);
		vkL.setFont("Roboto-Bold");
		UIText vkR = new UIText(CoreInfo.VkVersion, uiWindow.getWidth() - 30, -450);
		vkR.setAlign(NVG_ALIGN_RIGHT | NVG_ALIGN_MIDDLE);

		UIText vendorL = new UIText("Vendor", 30, -480);
		vendorL.setFont("Roboto-Bold");
		UIText vendorR = new UIText(CoreInfo.Vendor, uiWindow.getWidth() - 30, -480);
		vendorR.setAlign(NVG_ALIGN_RIGHT | NVG_ALIGN_MIDDLE);

		UIText rendererL = new UIText("Renderer", 30, -510);
		rendererL.setFont("Roboto-Bold");
		UIText rendererR = new UIText(CoreInfo.Renderer, uiWindow.getWidth() - 30, -510);
		rendererR.setAlign(NVG_ALIGN_RIGHT | NVG_ALIGN_MIDDLE);

		backButton.setPositionRelativeToRoot(false);
		uiWindow.addChildren(backButton);
		uiWindow.addChildren(versionL);
		uiWindow.addChildren(versionR);
		uiWindow.addChildren(osL);
		uiWindow.addChildren(osR);
		uiWindow.addChildren(lwjglL);
		uiWindow.addChildren(lwjglR);
		uiWindow.addChildren(glfwL);
		uiWindow.addChildren(glfwR);
		uiWindow.addChildren(openglL);
		uiWindow.addChildren(openglR);
		uiWindow.addChildren(vkL);
		uiWindow.addChildren(vkR);
		uiWindow.addChildren(vendorL);
		uiWindow.addChildren(vendorR);
		uiWindow.addChildren(rendererL);
		uiWindow.addChildren(rendererR);
	}

	@Override
	public void start() {
		uiWindow.setFadeAlpha(0);
	}

	@Override
	public void end() {
		uiWindow.setFadeAlpha(1);
	}

	@Override
	public void render(AbstractInfinity voxel, float delta) {
		Window window = ClientInternalSubsystem.getInstance().getGameWindow();
		Renderer.clearBuffer(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		Renderer.clearColors(1, 1, 1, 1);
		window.beingNVGFrame();
		uiWindow.render(window.getID());
		window.endNVGFrame();
	}

	@Override
	public void update(AbstractInfinity voxel, float delta) {
		uiWindow.update(delta);
		super.update(voxel, delta);
	}

	@Override
	protected boolean fadeIn(float delta) {
		return this.uiWindow.fadeIn(4, delta);
	}

	@Override
	protected boolean fadeOut(float delta) {
		return this.uiWindow.fadeOut(4, delta);
	}

}
