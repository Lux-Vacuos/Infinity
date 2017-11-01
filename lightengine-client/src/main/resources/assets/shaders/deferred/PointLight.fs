//
// This file is part of Light Engine
// 
// Copyright (C) 2016-2017 Lux Vacuos
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



##include struct Light

in vec2 textureCoords;

out vec4 out_Color;

uniform vec3 cameraPosition;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 biasMatrix;

uniform Light lights[18];
uniform int totalLights;

uniform sampler2D gDiffuse;
uniform sampler2D gPosition;
uniform sampler2D gNormal;
uniform sampler2D gMask;
uniform sampler2D gPBR; // R = roughness, B = metallic
uniform sampler2D composite0;

##include variable pi

##include function DistributionGGX

##include function GeometrySchlickGGX

##include function GeometrySmith

##include function fresnelSchlickRoughness

vec3 calcLight(Light light, vec3 position, vec3 diffuse, vec3 L, vec3 N, vec3 V, vec3 kD, vec3 F, float roughness) {
    vec3 H = normalize(V + L);
    float distance = length(light.position - position);
	float attenuation = 1.0 / (distance * distance);
	vec3 radiance = light.color * attenuation;   
			
    float NDF = DistributionGGX(N, H, roughness);        
	float G = GeometrySmith(N, V, L, roughness);      			
    vec3 nominator = NDF * G * F;
    float denominator = totalLights * max(dot(V, N), 0.0) * max(dot(L, N), 0.0) + 0.001; 
	vec3 brdf = nominator / denominator;
			
    float NdotL = max(dot(N, L), 0.0);                
	return (kD * diffuse / PI + brdf) * radiance * NdotL; 
}

void main(){
    vec4 composite = texture(composite0, textureCoords);
	vec4 mask = texture(gMask, textureCoords);
	if(mask.a != 1) {
		vec4 diffuse = texture(gDiffuse, textureCoords);
		vec2 pbr = texture(gPBR, textureCoords).rg;
    	vec3 position = texture(gPosition, textureCoords).rgb;
    	vec3 normal = texture(gNormal, textureCoords).rgb;

		vec3 N = normalize(normal);
	    vec3 V = normalize(cameraPosition - position);

		float roughness = pbr.r;
		float metallic = pbr.g;

	    vec3 F0 = vec3(0.04);
	    F0 = mix(F0, diffuse.rgb, metallic);
	    vec3 F = fresnelSchlickRoughness(max(dot(N, V), 0.0), F0, roughness);

	    vec3 kS = F;
	    vec3 kD = vec3(1.0) - kS;
	    kD *= 1.0 - metallic;	  
	
    	vec3 Lo = vec3(0.0);
		for(int i = 0; i < totalLights; i++) {
			vec3 L = normalize(lights[i].position - position);
			if(lights[i].type == 0) 
				Lo += calcLight(lights[i], position, diffuse.rgb, L, N, V, kD, F, roughness);
			else if(lights[i].type == 1) {
				float theta = dot(L, normalize(-lights[i].direction));
				float epsilon = lights[i].inRadius - lights[i].radius;
				float intensity = clamp((theta - lights[i].radius) / epsilon, 0.0, 1.0);    
				if(intensity > 0.0) {
        			float shadow = 1.0;
					if(lights[i].shadowEnabled == 1)	{
						vec4 posLight = lights[i].shadowViewMatrix * vec4(position, 1.0);
						vec4 shadowCoord = biasMatrix * (lights[i].shadowProjectionMatrix * posLight);
						shadow = texture(lights[i].shadowMap, (shadowCoord.xyz/shadowCoord.w), 0);
					}
					Lo += calcLight(lights[i], position, diffuse.rgb, L, N, V, kD, F, roughness) * intensity * shadow;
				}
			}
    	}
    	vec3 color = Lo;
    	composite.rgb += color;

	}
	out_Color = composite;
}