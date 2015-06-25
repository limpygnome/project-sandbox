attribute vec3 aVertexPosition;
attribute vec2 aTextureCoord;
attribute vec4 aColour;
attribute vec3 aNormals;

uniform mat4 uMVMatrix;
uniform mat4 uPMatrix;
uniform mat4 uNMatrix;

varying vec2 vTextureCoord;
varying vec4 vColour;
varying vec3 vAmbientLighting;
varying vec3 vNormals;
varying vec4 vPosition;
varying vec4 vWorldPosition;

void main(void)
{
	// Transfer attributes for fragment shader
	gl_Position = uPMatrix * uMVMatrix * vec4(aVertexPosition, 1.0);
	vWorldPosition = uMVMatrix * vec4(aVertexPosition, 1.0);

	vTextureCoord = aTextureCoord;
	vColour = aColour;
	vNormals = aNormals;
	vPosition = gl_Position;

	// Ambient light settings
	highp vec3 ambientLight = vec3(0.4, 0.4, 0.4);
	highp vec3 directionalLightColor = vec3(0.2, 0.2, 0.5);
	highp vec3 directionalVector = vec3(0.85, 0.8, 0.75);

	// Calculate ambient light
	highp vec4 transformedNormal = uNMatrix * vec4(aNormals, 1.0);
	highp float directional = max(dot(transformedNormal.xyz, directionalVector), 0.0);
	vAmbientLighting = ambientLight + (directionalLightColor * directional);
}
