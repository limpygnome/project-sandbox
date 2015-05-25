precision mediump float;
varying vec2 vTextureCoord;
varying vec4 vColour;
uniform sampler2D uSampler;

void main(void)
{
	vec4 texel = texture2D(uSampler, vec2(vTextureCoord.s, vTextureCoord.t));
	texel *= vColour;
	
	if (texel.a < 0.05)
	{
		discard;
	}

	gl_FragColor = texel;
}
