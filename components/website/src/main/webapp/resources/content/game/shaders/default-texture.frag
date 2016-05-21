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
varying vec4 vWorldVertex;

uniform Light uLights[1];

void main(void)
{
    vec4 texel = texture2D(uSampler, vec2(vTextureCoord.s, vTextureCoord.t));
    texel *= vColour;

    // Check if to discard texel due to alpha
    if (texel.a < 0.05)
    {
        discard;
    }

    // The colour of the texel from the light - can be used between multiple lights for additive colour
    vec3 additiveLightColour = vec3(1.0, 1.0, 1.0);

    Light light;
    for (int index = 0; index < 1; index++)
    {
        light = uLights[index];

        if (light.on > 0.5)
        {
            // Light properties
            float lightDistance = light.distance;
            vec3 lightColour = light.colour;
            vec3 lightPos = light.position;
            float lightRotation = light.rotation;
            float lightConeAngle = radians(light.coneAngle);

            // Light properties - attenuation
            float constantAttenuation = light.constantAttenuation;
            float linearAttenuation = light.linearAttenuation;
            float quadraticAttenuation = light.quadraticAttenuation;


            // Compute world position of light
            vec3 lightVec = normalize(lightPos - vWorldVertex.xyz);

            // Compute direction of light
            /////////////////////precompute
            vec3 lightDir = vec3(sin(lightRotation), cos(lightRotation), 0.0);

            // Compute current angle of light from source
            float angle = acos(dot(-lightVec, lightDir));

            // Compute distance between this fragment and light source
            float distance = distance(vWorldVertex.xyz, lightPos);

            // Check similarity between light and normal of light
            //////////////////Might not help but could do these inside if block
            vec3 normal = normalize(vNormals);
            float l = dot(normal, lightVec);

            if (angle < lightConeAngle && l >= 0.0 && distance <= lightDistance)
            {
                float attenuatedLight = 1.0 / (
                    constantAttenuation +
                    linearAttenuation*distance +
                    quadraticAttenuation*distance*distance
                );
                additiveLightColour += l * lightColour * attenuatedLight;
            }
        }
    }

    gl_FragColor = clamp(vec4(texel.rgb * vAmbientLighting * additiveLightColour, texel.a), 0.0, 1.0);
}
