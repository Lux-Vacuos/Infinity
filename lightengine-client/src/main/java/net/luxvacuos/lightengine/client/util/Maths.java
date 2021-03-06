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

package net.luxvacuos.lightengine.client.util;

import java.util.Random;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.joml.Vector4i;

import net.luxvacuos.lightengine.client.ecs.entities.CameraEntity;

public class Maths extends net.luxvacuos.lightengine.universal.util.Maths {

	public static Matrix4f createTransformationMatrix(Vector3f translation, float rx, float ry, float rz, float scale) {
		return createTransformationMatrix(translation, rx, ry, rz, scale, scale, scale);
	}

	public static Matrix4f createTransformationMatrix(Vector3f translation, float rx, float ry, float rz, float scaleX,
			float scaleY, float scaleZ) {
		Matrix4f matrix = new Matrix4f();
		matrix.identity();
		matrix.translate(translation);
		matrix.rotate((float) Math.toRadians(rx), new Vector3f(1, 0, 0));
		matrix.rotate((float) Math.toRadians(ry), new Vector3f(0, -1, 0)); // For some reason it rotates
																			// counter-clockwise
		matrix.rotate((float) Math.toRadians(rz), new Vector3f(0, 0, 1));
		matrix.scale(scaleX, scaleY, scaleZ);
		return matrix;
	}

	public static Matrix4f createViewMatrix(CameraEntity camera) {
		Matrix4f viewMatrix = new Matrix4f();
		viewMatrix.identity();
		createViewMatrixRot(camera.getRotation().x(), camera.getRotation().y(), camera.getRotation().z(), viewMatrix);
		createViewMatrixPos(camera.getPosition(), viewMatrix);
		return viewMatrix;
	}

	public static Matrix4f createViewMatrixPos(Vector3f pos, Matrix4f viewMatrix) {
		if (viewMatrix == null) {
			viewMatrix = new Matrix4f();
			viewMatrix.identity();
		}
		viewMatrix.translate(pos.negate(new Vector3f()));
		return viewMatrix;
	}

	public static Matrix4f createViewMatrixRot(float pitch, float yaw, float roll, Matrix4f viewMatrix) {
		if (viewMatrix == null) {
			viewMatrix = new Matrix4f();
			viewMatrix.identity();
		}
		viewMatrix.rotate((float) Math.toRadians(pitch), new Vector3f(1, 0, 0));
		viewMatrix.rotate((float) Math.toRadians(yaw), new Vector3f(0, 1, 0));
		viewMatrix.rotate((float) Math.toRadians(roll), new Vector3f(0, 0, 1));
		return viewMatrix;
	}

	public static Matrix4f orthoSymmetric(float width, float height, float zNear, float zFar, boolean zZeroToOne) {
		return new Matrix4f().setOrthoSymmetric(width, height, zNear, zFar, zZeroToOne);

	}

	public static float clamp(float d, float min, float max) {
		return Math.max(min, Math.min(max, d));
	}

	public static float clamp(float d, float min) {
		return Math.max(min, d);
	}

	public static int clampInt(int d, int min) {
		return Math.max(min, d);
	}

	public static float min(float d, float min) {
		return Math.min(d, min);
	}

	public static int minInt(int d, int min) {
		return Math.min(d, min);
	}

	public static int randInt(int min, int max) {
		Random rand = new Random();
		int randomNum = rand.nextInt((max - min) + 1) + min;
		return randomNum;
	}

	public static Vector2f convertTo2F(Vector3f pos, Matrix4f projection, Matrix4f viewMatrix, int width, int height) {
		return project(pos, projection, viewMatrix, new Vector4i(0, 0, width, height));
	}

	public static Vector2f project(Vector3f pos, Matrix4f projection, Matrix4f view, Vector4i viewport) {
		Matrix4f dest = projection.mul(view, new Matrix4f());
		Vector4f winCoordsDest = new Vector4f();
		dest.project(pos, new int[] { viewport.x(), viewport.y(), viewport.z(), viewport.w() }, winCoordsDest);
		return new Vector2f(winCoordsDest.x, winCoordsDest.y);
	}

	public static float randFloat() {
		Random rand = new Random();
		float randomNum = (rand.nextFloat() - 0.5f) / 16;
		return randomNum;
	}

	public static boolean getRandomBoolean(float chanceOfTrue) {
		return new Random().nextInt(100) < chanceOfTrue;
	}

	public static float dti(float val) {
		return (float) Math.abs(val - Math.round(val));
	}

}
