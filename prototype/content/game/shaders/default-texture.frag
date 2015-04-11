precision mediump float;
varying vec2 vTextureCoord;
varying vec4 vColour;
uniform sampler2D uSampler;

void main(void)
{
	vec4 texel = texture2D(uSampler, vec2(vTextureCoord.s, vTextureCoord.t));
	texel *= vColour;

	gl_FragColor = texel;
}
