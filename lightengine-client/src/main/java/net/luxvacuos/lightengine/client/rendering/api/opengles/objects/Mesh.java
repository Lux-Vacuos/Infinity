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

package net.luxvacuos.lightengine.client.rendering.api.opengles.objects;

import static org.lwjgl.opengles.GLES20.GL_STATIC_DRAW;
import static org.lwjgl.system.MemoryUtil.memAllocFloat;
import static org.lwjgl.system.MemoryUtil.memAllocInt;
import static org.lwjgl.system.MemoryUtil.memFree;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.assimp.AIFace;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIVector3D;

import net.luxvacuos.lightengine.universal.core.TaskManager;
import net.luxvacuos.lightengine.universal.resources.IDisposable;

public class Mesh implements IDisposable {

	private VAO mesh;
	private AIMesh aiMesh;
	private boolean doneLoading;

	public Mesh(AIMesh aiMesh) {
		this.aiMesh = aiMesh;

		List<Vector3f> pos = new ArrayList<>();
		List<Vector2f> tex = new ArrayList<>();
		List<Vector3f> nor = new ArrayList<>();
		List<Vector3f> tan = new ArrayList<>();
		for (int i = 0; i < aiMesh.mNumVertices(); i++) {
			AIVector3D position = aiMesh.mVertices().get(i);
			AIVector3D texcoord = null;
			if (aiMesh.mTextureCoords(0) != null)
				texcoord = aiMesh.mTextureCoords(0).get(i);
			AIVector3D normal = aiMesh.mNormals().get(i);
			AIVector3D tangent = aiMesh.mTangents().get(i);
			pos.add(new Vector3f(position.x(), position.y(), position.z()));
			if (aiMesh.mTextureCoords(0).get(i) != null)
				tex.add(new Vector2f(texcoord.x(), texcoord.y()));
			else
				tex.add(new Vector2f(0, 0));
			nor.add(new Vector3f(normal.x(), normal.y(), normal.z()));
			tan.add(new Vector3f(tangent.x(), tangent.y(), tangent.z()));
		}
		int faceCount = aiMesh.mNumFaces();
		int elementCount = faceCount * 3;
		IntBuffer elementArrayBufferData = memAllocInt(elementCount);
		AIFace.Buffer facesBuffer = aiMesh.mFaces();
		for (int i = 0; i < faceCount; ++i) {
			AIFace face = facesBuffer.get(i);
			if (face.mNumIndices() != 3)
				throw new IllegalStateException("AIFace.mNumIndices() != 3");
			elementArrayBufferData.put(face.mIndices());
		}
		elementArrayBufferData.flip();
		int[] ind = new int[elementCount];
		elementArrayBufferData.get(ind);
		memFree(elementArrayBufferData);
		TaskManager.addTask(() -> {
			mesh = VAO.create();
			mesh.bind();
			loadData(pos, tex, nor, tan);
			mesh.createIndexBuffer(ind, GL_STATIC_DRAW);
			mesh.unbind();
			doneLoading = true;
		});
	}

	@Override
	public void dispose() {
		mesh.dispose();
	}

	public AIMesh getAiMesh() {
		return aiMesh;
	}

	public VAO getMesh() {
		return mesh;
	}

	public boolean isDoneLoading() {
		return doneLoading;
	}

	private void loadData(List<Vector3f> positions, List<Vector2f> texcoords, List<Vector3f> normals,
			List<Vector3f> tangets) {
		FloatBuffer posB = memAllocFloat(positions.size() * 3);
		FloatBuffer texB = memAllocFloat(texcoords.size() * 2);
		FloatBuffer norB = memAllocFloat(normals.size() * 3);
		FloatBuffer tanB = memAllocFloat(tangets.size() * 3);
		for (int i = 0; i < positions.size(); i++) {
			posB.put(positions.get(i).x);
			posB.put(positions.get(i).y);
			posB.put(positions.get(i).z);
		}
		for (int i = 0; i < texcoords.size(); i++) {
			texB.put(texcoords.get(i).x);
			texB.put(texcoords.get(i).y);
		}
		for (int i = 0; i < normals.size(); i++) {
			norB.put(normals.get(i).x);
			norB.put(normals.get(i).y);
			norB.put(normals.get(i).z);
		}
		for (int i = 0; i < tangets.size(); i++) {
			tanB.put(tangets.get(i).x);
			tanB.put(tangets.get(i).y);
			tanB.put(tangets.get(i).z);
		}
		posB.flip();
		texB.flip();
		norB.flip();
		tanB.flip();
		float[] posA = new float[posB.capacity()], texA = new float[texB.capacity()], norA = new float[norB.capacity()],
				tanA = new float[tanB.capacity()];
		posB.get(posA);
		texB.get(texA);
		norB.get(norA);
		tanB.get(tanA);
		memFree(posB);
		memFree(texB);
		memFree(norB);
		memFree(tanB);
		mesh.createAttribute(0, posA, 3, GL_STATIC_DRAW);
		mesh.createAttribute(1, texA, 2, GL_STATIC_DRAW);
		mesh.createAttribute(2, norA, 3, GL_STATIC_DRAW);
		mesh.createAttribute(3, tanA, 3, GL_STATIC_DRAW);
	}

}