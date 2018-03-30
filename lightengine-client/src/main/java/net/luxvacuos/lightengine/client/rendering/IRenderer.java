/*
 * This file is part of Light Engine
 * 
 * Copyright (C) 2016-2018 Lux Vacuos
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

package net.luxvacuos.lightengine.client.rendering;

import net.luxvacuos.lightengine.client.network.ClientNetworkHandler;
import net.luxvacuos.lightengine.client.rendering.nanovg.IWindow;
import net.luxvacuos.lightengine.client.rendering.opengl.Frustum;
import net.luxvacuos.lightengine.client.rendering.opengl.IRenderPass;
import net.luxvacuos.lightengine.client.rendering.opengl.LightRenderer;

public interface IRenderer {

	public void init();

	public void render(ClientNetworkHandler nh, float delta);

	public void resize(int width, int height);

	public void dispose();

	public void setShadowPass(IRenderPass shadowPass);

	public void setDeferredPass(IRenderPass deferredPass);

	public void setForwardPass(IRenderPass forwardPass);

	public void setOcclusionPass(IRenderPass occlusionPass);

	public Frustum getFrustum();

	public LightRenderer getLightRenderer();

	public IWindow getWindow();
}