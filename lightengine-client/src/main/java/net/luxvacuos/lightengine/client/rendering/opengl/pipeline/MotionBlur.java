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

package net.luxvacuos.lightengine.client.rendering.opengl.pipeline;

import static org.lwjgl.opengl.GL11C.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL13C.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13C.GL_TEXTURE1;

import net.luxvacuos.lightengine.client.network.IRenderingData;
import net.luxvacuos.lightengine.client.rendering.opengl.RendererData;
import net.luxvacuos.lightengine.client.rendering.opengl.objects.Texture;
import net.luxvacuos.lightengine.client.rendering.opengl.pipeline.shaders.MotionBlurShader;
import net.luxvacuos.lightengine.client.rendering.opengl.v2.PostProcesPass;
import net.luxvacuos.lightengine.client.rendering.opengl.v2.PostProcessPipeline;

public class MotionBlur extends PostProcesPass<MotionBlurShader> {

	public MotionBlur() {
		super("MotionBlur");
	}

	@Override
	protected MotionBlurShader setupShader() {
		return new MotionBlurShader(name);
	}

	@Override
	protected void setupShaderData(RendererData rnd, IRenderingData rd, MotionBlurShader shader) {
		shader.loadMotionBlurData(rd.getCamera(), rnd.previousViewMatrix, rnd.previousCameraPosition);
	}

	@Override
	protected void setupTextures(RendererData rnd, PostProcessPipeline pp, Texture[] auxTex) {
		super.activateTexture(GL_TEXTURE0, GL_TEXTURE_2D, auxTex[0].getTexture());
		super.activateTexture(GL_TEXTURE1, GL_TEXTURE_2D, pp.getDepthTex().getTexture());
	}

}
