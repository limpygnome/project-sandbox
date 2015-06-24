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




	vec3 colour = vec3(1.0, 1.0, 1.0);
	vec3 lightVec = vec3(0.0, 0.0, 400.0);
	vec3 lightVecNormalized = lightVec - vPosition.xyz;//normalize(lightVec - vPosition.xyz);
	float l = dot(vNormals, lightVec);
	vec3 lightColour = vec3(1.0, 1.0, 1.0);

	if (l > 0.0)
	{
		float attenuation = 50
		float a = 1.0/(
			attenuation +
			attenuation*d +
			attenuation*d*d
		);0.0;
          		float d = 0.0;
		colour += l*a*lightColour;
	}



	//gl_FragColor = vec4(texel.rgb * vLighting, texel.a);
}
