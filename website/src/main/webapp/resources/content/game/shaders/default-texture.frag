precision mediump float;
varying vec2 vTextureCoord;
varying vec4 vColour;
varying vec3 vLighting;
varying vec3 vNormals;
varying vec4 vPosition;
uniform sampler2D uSampler;

void main(void)
{
	vec4 texel = texture2D(uSampler, vec2(vTextureCoord.s, vTextureCoord.t));
	texel *= vColour;

	// Check if to discard texel due to alpha
	if (texel.a < 0.05)
	{
		discard;
	}




	// Fixed light
//	vec3 color = vec3(1.0, 1.0, 1.0);
//
//	vec3 lightColour = vec3(1.0, 1.0, 1.0);
//	vec3 lightPos = vec3(0.0, 0.0, 370.0);
//	vec3 lightVec = normalize(lightPos - vPosition.xyz);
//	float l = dot(vNormals, lightVec);
//
//	float maxDistance = 100.0;
//    float d = distance(vPosition.xyz, lightPos);
//
//	if (l >= 0.0 && d <= maxDistance)
//	{
//		float constantAttenuation = 0.2;
//		float linearAttenuation = 0.01;
//		float quadraticAttenuation = 0.000001;
//
//		float attenuatedLight = 1.0/(
//            constantAttenuation +
//            linearAttenuation*d +
//            quadraticAttenuation*d*d
//		);
//			color += l * lightColour * attenuatedLight;
//	}
//
//	gl_FragColor = clamp(vec4(texel.rgb * vLighting * color, texel.a), 0.0, 1.0);


	// Cone light
	// help from http://shdr.bkcore.com/#1/lVLPS8MwFP5XHjml0s1O52VDQRRPCh7Ei5ORtWmXkSYlSadu9H83TZqtK4p4SAJ534/3Pt4eZTKtSyqMRrM3VCmaMs2kgDUr1hXkXBIzX4gtUV9MFLCl6SXkz1IzY0G2YEuSZVASJnC0EPuFAA/ilm8CEK7dJ74aJzG4Kxkn0fwANvKxhYPFCalKwtmO4lOF0dG2R3SYF6IKaoJH0skPPRz0ntkBu44GTp3K6LRzL9AeFwUQUXBqySSVGmfS4FHXezwwiDzTs9LayDy/7biKZIwIjS9Cf+GwHHv9mz4jaksuWICCLx8UKe4kl8oPPMWT/sAxTIJq016Ua/oHP7B+4Dco/nUliDGKrWpDfbzVcSdqwXKbrV0KM4VSZpS/MvrxRCz+c1iulNz4wA71f+1a3luxXgtu0n5pYANnw8bsj0sjiByCaNB7jBwYzfyrz3W9I0LQZZvIeKNR8w0=
	// -- original url: https://www.reddit.com/r/opengl/comments/39y1fn/any_idea_why_this_shader_does_not_produce_a/

	// Light properties
	float maxDistance = 400.0;
	vec3 lightColour = vec3(1.0, 1.0, 1.0);
	vec3 lightPos = vec3(0.0, 0.0, 370.0);
	vec3 lightVec = normalize(lightPos - vPosition.xyz);
	float lightAngle = radians(0.0);
	float lightConeAngle = radians(65.0);

	// Calculations for light
	vec3 lightTarget = vec3(sin(lightAngle), cos(lightAngle), 370.0);
	vec3 lightDir = normalize(lightTarget - lightPos);
	float angle = acos(dot(-lightVec, lightDir));
	float l = dot(vNormals, lightVec);
    float d = distance(vPosition.xyz, lightPos);

    // The colour of the texel from the light - can be used between multiple lights for additive colour
	vec3 color = vec3(1.0, 1.0, 1.0);

	if (angle < lightConeAngle && l >= 0.0 && d <= maxDistance)
	{
		float constantAttenuation = 0.2;
		float linearAttenuation = 0.01;
		float quadraticAttenuation = 0.000001;

		float attenuatedLight = 1.0/(
            constantAttenuation +
            linearAttenuation*d +
            quadraticAttenuation*d*d
		);
			color += l * lightColour * attenuatedLight;
	}

	gl_FragColor = clamp(vec4(texel.rgb * vLighting * color, texel.a), 0.0, 1.0);


	//gl_FragColor = vec4(texel.rgb * vLighting, texel.a);
}
