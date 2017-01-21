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

package net.luxvacuos.infinity.client.world.entities;

import net.luxvacuos.igl.vector.Matrix4d;
import net.luxvacuos.igl.vector.Vector2d;
import net.luxvacuos.infinity.client.resources.DRay;
import net.luxvacuos.infinity.client.util.Maths;

public class SunCamera extends Camera {

	private Vector2d center;

	private Matrix4d[] proj;

	public SunCamera(Matrix4d[] proj) {
		this.proj = proj;
		this.projectionMatrix = proj[0];
		center = new Vector2d(2048, 2048);
		dRay = new DRay(projectionMatrix, Maths.createViewMatrix(this), center, 0, 0);
		this.viewMatrix = Maths.createViewMatrix(this);
	}

	public void updateShadowRay(boolean inverted) {

		if (inverted)
			dRay = new DRay(projectionMatrix, Maths.createViewMatrixPos(this.getPosition(), Maths
					.createViewMatrixRot(getRotation().getX() + 180, getRotation().getY(), getRotation().getZ(), null)),
					center, 4096, 4096);
		else
			dRay = new DRay(projectionMatrix, Maths.createViewMatrix(this), center, 4096, 4096);
		viewMatrix = Maths.createViewMatrix(this);
	}

	public void switchProjectionMatrix(int id) {
		this.projectionMatrix = this.proj[id];
	}

	public Matrix4d[] getProj() {
		return proj;
	}

	public DRay getDRay() {
		return dRay;
	}

}
