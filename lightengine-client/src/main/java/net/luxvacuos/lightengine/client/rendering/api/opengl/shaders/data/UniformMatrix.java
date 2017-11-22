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

package net.luxvacuos.lightengine.client.rendering.api.opengl.shaders.data;

import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;

import org.joml.Matrix4f;

public class UniformMatrix extends Uniform {

	public UniformMatrix(String name) {
		super(name);
	}

	public void loadMatrix(Matrix4f matrix) {
		float[] fm = new float[16];
		matrix.get(fm);
		glUniformMatrix4fv(super.getLocation(), false, fm);
	}

}