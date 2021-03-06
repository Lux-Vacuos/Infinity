//
// This file is part of Light Engine
//
// Copyright (C) 2016-2019 Lux Vacuos
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.
//
//

layout(location = 0) in vec3 position;
layout(location = 1) in vec2 textureCoords;

out vec3 passPosition;
out vec2 passTextureCoords;

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform float time;

#define PI 3.14159265359

const float A = 0.1; // amplitude
const float L = 8;   // wavelength
const float w = 2 * PI / L;
const float Q = 1;
const float tiling = 1.0;

void main() {
	vec4 worldPosition = transformationMatrix * vec4(position, 1.0);

	vec3 P0 = worldPosition.xyz;
	vec2 D = vec2(1, 0.5);
	float dotD = dot(P0.xz, D);
	float C = cos(w * dotD + time / 4);
	float S = sin(w * dotD + time / 4);

	vec3 P = vec3(P0.x + Q * A * C * D.x, A * S + worldPosition.y, P0.z + Q * A * C * D.y);
	worldPosition.xyz = P;

	passPosition = worldPosition.xyz;
	vec4 positionRelativeToCam = viewMatrix * worldPosition;
	gl_Position = projectionMatrix * positionRelativeToCam;
	passTextureCoords = passPosition.xz * tiling;
}