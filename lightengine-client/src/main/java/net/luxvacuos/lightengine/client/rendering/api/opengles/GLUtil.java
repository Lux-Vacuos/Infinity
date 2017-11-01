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

package net.luxvacuos.lightengine.client.rendering.api.opengles;

import static org.lwjgl.opengles.GLES20.glGetInteger;
import static org.lwjgl.opengles.GLES20.glGetString;

public class GLUtil {

	public static int GL_MAX_TEXTURE_SIZE;

	private GLUtil() {
	}

	public static void init() {
		GL_MAX_TEXTURE_SIZE = glGetInteger(GL_MAX_TEXTURE_SIZE);
		if (GL_MAX_TEXTURE_SIZE == 0)
			GL_MAX_TEXTURE_SIZE = 4096; // Dirty fix because it keeps returning 0
	}

	public static String getString(int name) {
		String res = "null";
		try {
			res = glGetString(name);
		} catch (Exception e) {
		}
		return res;
	}

}