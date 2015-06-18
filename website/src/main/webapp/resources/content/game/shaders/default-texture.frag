precision mediump float;
varying vec2 vTextureCoord;
varying vec4 vColour;
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

	vec3 lightPosition = vec3(0.0, 0.0, 0.0);

	float attenuation = 60.0;


    vec3 worldPosition = vec3(vPosition.x, vPosition.y, vPosition.z);

	//float distance = distance(lightPosition, worldPosition);
	//float distance = lightPosition.y - worldPosition.y;

	// world position z appears to be the camera zoom value...
	// thus worldPosition is camera co-ordinates

	float distance = distance(vec3(lightPosition.x, lightPosition.y, lightPosition.z), vec3(worldPosition.x, worldPosition.y, worldPosition.z + 400.0));

    if (distance < 0.0)
    {
        distance *= -1.0;
    }

	// Iterate and add colour for lighting
    float baseMultiplier = 0.5;

	if (distance <= attenuation)
	{

        float light = 1.0 - (distance / attenuation);
		light *= 0.5;

        baseMultiplier += light;
	}

	// Apply multiplier to texel
	texel.r *= baseMultiplier;
	texel.g *= baseMultiplier;
	texel.b *= baseMultiplier;

	texel = clamp(texel, 0.0, 1.0);

	gl_FragColor = texel;
}
