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

package net.luxvacuos.lightengine.client.rendering.api.nanovg.shaders;

import net.luxvacuos.igl.vector.Matrix4d;
import net.luxvacuos.lightengine.client.core.ClientVariables;
import net.luxvacuos.lightengine.client.ecs.entities.CameraEntity;
import net.luxvacuos.lightengine.client.rendering.api.opengl.shaders.ShaderProgram;
import net.luxvacuos.lightengine.client.rendering.api.opengl.shaders.data.Attribute;
import net.luxvacuos.lightengine.client.rendering.api.opengl.shaders.data.UniformMatrix;
import net.luxvacuos.lightengine.client.rendering.api.opengl.shaders.data.UniformSampler;

public class Window3DShader extends ShaderProgram {

	private UniformSampler image = new UniformSampler("image");
	private UniformMatrix transformationMatrix = new UniformMatrix("transformationMatrix");
	private UniformMatrix projectionMatrix = new UniformMatrix("projectionMatrix");
	private UniformMatrix viewMatrix = new UniformMatrix("viewMatrix");

	public Window3DShader() {
		super("wm/" + ClientVariables.VERTEX_WINDOW3D, "wm/" + ClientVariables.FRAGMENT_WINDOW3D,
				new Attribute(0, "position"));
		super.storeAllUniformLocations(transformationMatrix, projectionMatrix, viewMatrix, image);
		connectTextureUnits();
	}

	private void connectTextureUnits() {
		super.start();
		image.loadTexUnit(0);
		super.stop();
	}

	public void loadTransformationMatrix(Matrix4d matrix) {
		transformationMatrix.loadMatrix(matrix);
	}

	public void loadViewMatrix(CameraEntity camera) {
		viewMatrix.loadMatrix(camera.getViewMatrix());
	}

	public void loadProjectionMatrix(Matrix4d projection) {
		projectionMatrix.loadMatrix(projection);
	}

}