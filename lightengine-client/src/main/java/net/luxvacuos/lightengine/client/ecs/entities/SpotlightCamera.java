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

package net.luxvacuos.lightengine.client.ecs.entities;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import net.luxvacuos.lightengine.client.rendering.opengl.Renderer;
import net.luxvacuos.lightengine.client.util.Maths;

public class SpotlightCamera extends CameraEntity {

	private Vector3f direction = new Vector3f();
	private int size;
	private float radius;

	@Deprecated
	public SpotlightCamera(float radius, int width, int height) {
		super("spotlight");
		this.radius = width;
		this.radius = radius;
	}

	public SpotlightCamera(float radius, int size) {
		super("spotlight");
		this.radius = radius;
		this.size = size;
	}

	@Override
	public void init() {
		setProjectionMatrix(Renderer.createProjectionMatrix(size, size, radius, 0.1f, 100f));
		setViewMatrix(Maths.createViewMatrix(this));
		super.init();
	}

	@Override
	public void update(float delta) {
		super.update(delta);
		setViewMatrix(Maths.createViewMatrix(this));
		Matrix4f proj = getProjectionMatrix();
		Vector3f v = new Vector3f();
		v.x = (((2.0f * 8) / 16) - 1) / proj.m00();
		v.y = -(((2.0f * 8) / 16) - 1) / proj.m11();
		v.z = 1.0f;
		Matrix4f invertView = getViewMatrix().invert(new Matrix4f());
		direction.x = v.x * invertView.m00() + v.y * invertView.m10() + v.z * invertView.m20();
		direction.y = v.x * invertView.m01() + v.y * invertView.m11() + v.z * invertView.m21();
		direction.z = v.x * invertView.m02() + v.y * invertView.m12() + v.z * invertView.m22();
	}

	public Vector3f getDirection() {
		return direction.negate(new Vector3f());
	}

}
