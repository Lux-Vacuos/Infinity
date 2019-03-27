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

package net.luxvacuos.lightengine.universal.core.subsystems;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.google.gson.Gson;

import net.luxvacuos.igl.Logger;
import net.luxvacuos.lightengine.universal.resources.ResourceDefinition;
import net.luxvacuos.lightengine.universal.resources.ResourceType;
import net.luxvacuos.lightengine.universal.resources.SimpleResource;

public class ResManager extends Subsystem {

	private static Map<String, SimpleResource> globalResources = new HashMap<>();
	private static Gson gson = new Gson();

	@Override
	public void dispose() {
		globalResources.clear();
	}

	public static void loadResourceDefinition(String file) {
		Logger.log("Loading Resource Definition: " + file);
		var fileInput = SimpleResource.class.getClassLoader().getResourceAsStream(file);
		try (var reader = new InputStreamReader(fileInput)) {
			var rd = gson.fromJson(reader, ResourceDefinition.class);
			addResourceDefinition(rd);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void addResourceDefinition(ResourceDefinition rd) {
		globalResources.putAll(rd.getResources());
		if (rd.hasDefinitions())
			for (String rdd : rd.getDefinitions())
				loadResourceDefinition(rdd);
	}

	public static Optional<SimpleResource> getResource(String key) {
		var sr = globalResources.get(key);
		if (sr == null)
			Logger.warn("Resource for key '" + key + "' not found.");
		return Optional.ofNullable(sr);
	}

	public static Optional<SimpleResource> getResourceOfType(String key, ResourceType type) {
		var sr = globalResources.get(key);
		if (sr == null)
			Logger.warn("Resource for key '" + key + "' not found.");
		else if (sr.getResourceType() != type) {
			Logger.warn("Resource for key '" + key + "' mismatch type, found '" + sr.getResourceType()
					+ "' and requested '" + type + "'");
			return Optional.ofNullable(sr);
		}
		return Optional.ofNullable(sr);
	}

}
