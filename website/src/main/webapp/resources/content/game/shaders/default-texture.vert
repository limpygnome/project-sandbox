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
	highp vec3 directionalLightColor = vec3(0.5, 0.5, 0.75);
	highp vec3 directionalVector = vec3(0.85, 0.8, 0.75);

	highp vec4 transformedNormal = uNMatrix * vec4(aNormals, 1.0);

	highp float directional = max(dot(transformedNormal.xyz, directionalVector), 0.0);
	vLighting = ambientLight + (directionalLightColor * directional);
}
