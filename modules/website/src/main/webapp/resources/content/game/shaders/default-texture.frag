precision mediump float;

struct Light
{
    float on;
    float distance;
    vec3 colour;
    vec3 position;
    float rotation;
    float coneAngle;
    float constantAttenuation;
    float linearAttenuation;
    float quadraticAttenuation;
};


varying vec2 vTextureCoord;
varying vec4 vColour;
varying vec3 vAmbientLighting;
varying vec3 vNormals;
varying vec4 vPosition;
uniform sampler2D uSampler;
varying vec3 vCameraPosition;
varying vec4 vWorldVertex;

uniform Light uLights[512];

void main(void)
{
	vec4 texel = texture2D(uSampler, vec2(vTextureCoord.s, vTextureCoord.t));
	texel *= vColour;

	// Check if to discard texel due to alpha
	if (texel.a < 0.05)
	{
		discard;
	}

	// Light properties
	bool lightOn = (uLights[0].on > 0.5);
	float lightDistance = uLights[0].distance;
	vec3 lightColour = uLights[0].colour;
	vec3 lightPos = vec3(200.0, 200.0, 30.0);
	float lightRotation = radians(0.0);
	float lightConeAngle = radians(65.0);

	// Light properties - attenuation
	float constantAttenuation = 0.2;
	float linearAttenuation = 0.001;
	float quadraticAttenuation = 0.000001;


    // Compute world position of light
    vec3 lightVec = normalize(lightPos - vWorldVertex.xyz);

    // Compute direction of light
	vec3 lightDir = vec3(sin(lightRotation), cos(lightRotation), 0.0);

    // Compute current angle of light from source
    float angle = acos(dot(-lightVec, lightDir));

    // Compute distance between this fragment and light source
    float distance = distance(vWorldVertex.xyz, lightPos);

    // Check similarity between light and normal of light
    vec3 normal = normalize(vNormals);
    float l = dot(normal, lightVec);

    // The colour of the texel from the light - can be used between multiple lights for additive colour
	vec3 additiveLightColour = vec3(1.0, 1.0, 1.0);

	if (lightOn && angle < lightConeAngle && l >= 0.0 && distance <= lightDistance)
	{
		float attenuatedLight = 1.0 / (
            constantAttenuation +
            linearAttenuation*distance +
            quadraticAttenuation*distance*distance
		);
        additiveLightColour += l * lightColour * attenuatedLight;
	}

	gl_FragColor = clamp(vec4(texel.rgb * vAmbientLighting * additiveLightColour, texel.a), 0.0, 1.0);
}
