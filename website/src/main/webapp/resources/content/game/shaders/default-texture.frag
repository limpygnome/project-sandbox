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




	vec3 color = vec3(1.0, 1.0, 1.0);

	vec3 lightColour = vec3(1.0, 1.0, 1.0);
	vec3 lightPos = vec3(0.0, 0.0, 380.0);
	vec3 lightVec = normalize(lightPos - vPosition.xyz);
	float l = dot(vNormals, lightVec);

	float maxDistance = 100.0;
    float d = distance(vPosition.xyz, lightPos);

	if (l >= 0.0 && d <= maxDistance)
	{
		float constantAttenuation = 0.00;
		float linearAttenuation = 0.04;
		float quadraticAttenuation = 0.0;

		float attenuatedLight = 1.0/(
            constantAttenuation +
            linearAttenuation*d +
            quadraticAttenuation*d*d
		);
		//attenuatedLight = d * 100.0;


		//color += l * lightColour * stepped;
		color += l * lightColour * attenuatedLight;
	}

	gl_FragColor = clamp(vec4(texel.rgb * vLighting * color, texel.a), 0.0, 1.0);




	//gl_FragColor = vec4(texel.rgb * vLighting, texel.a);
}
