precision mediump float;
varying vec2 vTextureCoord;
varying vec4 vColour;
varying vec3 vLighting;
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

	gl_FragColor = vec4(texel.rgb * vLighting, texel.a);
}
