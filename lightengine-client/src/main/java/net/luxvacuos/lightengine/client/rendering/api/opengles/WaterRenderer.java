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

import static org.lwjgl.opengles.GLES20.GL_BLEND;
import static org.lwjgl.opengles.GLES20.GL_TEXTURE0;
import static org.lwjgl.opengles.GLES20.GL_TEXTURE1;
import static org.lwjgl.opengles.GLES20.GL_TEXTURE2;
import static org.lwjgl.opengles.GLES20.GL_TEXTURE3;
import static org.lwjgl.opengles.GLES20.GL_TEXTURE4;
import static org.lwjgl.opengles.GLES20.GL_TEXTURE_2D;
import static org.lwjgl.opengles.GLES20.GL_TEXTURE_CUBE_MAP;
import static org.lwjgl.opengles.GLES20.GL_TRIANGLES;
import static org.lwjgl.opengles.GLES20.GL_UNSIGNED_INT;
import static org.lwjgl.opengles.GLES20.glActiveTexture;
import static org.lwjgl.opengles.GLES20.glBindTexture;
import static org.lwjgl.opengles.GLES20.glDisable;
import static org.lwjgl.opengles.GLES20.glDisableVertexAttribArray;
import static org.lwjgl.opengles.GLES20.glDrawElements;
import static org.lwjgl.opengles.GLES20.glEnable;
import static org.lwjgl.opengles.GLES20.glEnableVertexAttribArray;
import static org.lwjgl.opengles.GLES30.glBindVertexArray;

import java.util.List;

import org.joml.Vector3f;

import net.luxvacuos.lightengine.client.ecs.entities.CameraEntity;
import net.luxvacuos.lightengine.client.rendering.api.opengles.objects.CachedAssets;
import net.luxvacuos.lightengine.client.rendering.api.opengles.objects.CubeMapTexture;
import net.luxvacuos.lightengine.client.rendering.api.opengles.objects.RawModel;
import net.luxvacuos.lightengine.client.rendering.api.opengles.objects.Texture;
import net.luxvacuos.lightengine.client.rendering.api.opengles.objects.WaterTile;
import net.luxvacuos.lightengine.client.rendering.api.opengles.shaders.WaterShader;
import net.luxvacuos.lightengine.client.resources.ResourceLoader;
import net.luxvacuos.lightengine.client.util.Maths;
import net.luxvacuos.lightengine.universal.resources.IDisposable;

public class WaterRenderer implements IDisposable {

	private RawModel quad;
	private WaterShader shader;
	private Texture dudv, foamMask;;

	public WaterRenderer(ResourceLoader loader) {
		shader = new WaterShader();
		setUpVAO(loader);
		dudv = CachedAssets.loadTextureMisc("textures/waterDUDV.png");
		foamMask = CachedAssets.loadTextureMisc("textures/foamMask.png");
	}

	public void render(List<WaterTile> water, CameraEntity camera, CubeMapTexture reflection, int refraction, int depth,
			float time, Frustum frustum) {
		if (water == null)
			return;
		if (water.isEmpty())
			return;
		prepareRender(camera, reflection, refraction, depth, time);
		for (WaterTile tile : water) {
			float halfTileSize = WaterTile.TILE_SIZE * 2;
			if (!frustum.cubeInFrustum(tile.getX() + halfTileSize, tile.getY(), tile.getZ() - halfTileSize,
					halfTileSize))
				continue;
			shader.loadTransformationMatrix(Maths.createTransformationMatrix(
					new Vector3f(tile.getX(), tile.getY(), tile.getZ()), 0, 0, 0, WaterTile.TILE_SIZE));
			glDrawElements(GL_TRIANGLES, quad.getVertexCount(), GL_UNSIGNED_INT, 0);
		}
		unbind();
	}

	private void prepareRender(CameraEntity camera, CubeMapTexture reflection, int refraction, int depth, float time) {
		glEnable(GL_BLEND);
		shader.start();
		shader.loadViewMatrix(camera);
		shader.loadProjectionMatrix(camera.getProjectionMatrix());
		shader.loadCameraPosition(camera.getPosition());
		shader.loadTime(time);
		glBindVertexArray(quad.getVaoID());
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_CUBE_MAP, reflection.getID());
		glActiveTexture(GL_TEXTURE1);
		glBindTexture(GL_TEXTURE_2D, refraction);
		glActiveTexture(GL_TEXTURE2);
		glBindTexture(GL_TEXTURE_2D, dudv.getID());
		glActiveTexture(GL_TEXTURE3);
		glBindTexture(GL_TEXTURE_2D, depth);
		glActiveTexture(GL_TEXTURE4);
		glBindTexture(GL_TEXTURE_2D, foamMask.getID());
	}

	private void unbind() {
		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		glBindVertexArray(0);
		shader.stop();
		glDisable(GL_BLEND);
	}

	@Override
	public void dispose() {
		shader.dispose();
		dudv.dispose();
		foamMask.dispose();
	}

	private void setUpVAO(ResourceLoader loader) {
		quad = loader.loadObjModel("water");
	}

}