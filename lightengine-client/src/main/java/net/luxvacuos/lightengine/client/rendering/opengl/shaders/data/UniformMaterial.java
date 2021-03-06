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

package net.luxvacuos.lightengine.client.rendering.opengl.shaders.data;

import static org.lwjgl.opengl.GL20C.glUniform1f;
import static org.lwjgl.opengl.GL20C.glUniform1i;
import static org.lwjgl.opengl.GL20C.glUniform4f;

import net.luxvacuos.lightengine.client.rendering.opengl.objects.Material;

public class UniformMaterial extends UniformArray {

	private Material currentValue;

	public UniformMaterial(String matName) {
		super(matName + ".diffuse", matName + ".emissive", matName + ".roughness", matName + ".metallic",
				matName + ".diffuseTex", matName + ".normalTex", matName + ".roughnessTex", matName + ".metallicTex");
	}

	public void loadMaterial(Material value) {
		if (!used || !currentValue.equals(value)) {
			glUniform4f(super.getLocation()[0], value.getDiffuse().x(), value.getDiffuse().y(), value.getDiffuse().z(),
					value.getDiffuse().w());
			glUniform4f(super.getLocation()[1], value.getEmissive().x(), value.getEmissive().y(),
					value.getEmissive().z(), value.getEmissive().w());
			glUniform1f(super.getLocation()[2], value.getRoughness());
			glUniform1f(super.getLocation()[3], value.getMetallic());
			glUniform1i(super.getLocation()[4], 0);
			glUniform1i(super.getLocation()[5], 1);
			glUniform1i(super.getLocation()[6], 2);
			glUniform1i(super.getLocation()[7], 3);
			used = true;
			currentValue = value;
		}
	}

}
