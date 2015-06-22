attribute vec3 aVertexPosition;
attribute vec2 aTextureCoord;
attribute vec4 aColour;
attribute vec3 aNormals;

uniform mat4 uMVMatrix;
uniform mat4 uPMatrix;
uniform mat4 uNMatrix;

varying vec2 vTextureCoord;
varying vec4 vColour;
varying vec3 vLighting;

void main(void)
{
	gl_Position = uPMatrix * uMVMatrix * vec4(aVertexPosition, 1.0);

	vTextureCoord = aTextureCoord;
	vColour = aColour;






	highp vec3 ambientLight = vec3(0.6, 0.6, 0.6);
	highp vec3 directionalLightColor = vec3(0.2, 0.2, 0.5);
	highp vec3 directionalVector = vec3(0.85, 0.8, 0.75);

	highp vec4 transformedNormal = uNMatrix * vec4(aNormals, 1.0);



	vec3 colour = directionalLightColor;
	vec3 lightVec = vec3(0.0, 0.0, 0.0);
	float l = dot(aNormals, lightVec);
	vec3 lightColour = vec3(1.0, 1.0, 1.0);

	if (l > 0.0)
	{
		float attenuation = 200;
		float d = 0.0;
		float a = 1.0/(
			attenuation +
			attenuation*d +
			attenuation*d*d
		);
		colour += l*a*lightColour;
	}






	highp float directional = max(dot(transformedNormal.xyz, directionalVector), 0.0);
	vLighting = ambientLight + (colour * directional);
}
