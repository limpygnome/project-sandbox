attribute vec3 aVertexPosition;
attribute vec2 aTextureCoord;
attribute vec4 aColour;
attribute vec3 aNormals;
attribute vec3 aCameraPosition;

uniform mat4 uMVMatrix;
uniform mat4 uPMatrix;
uniform mat4 uNMatrix;
uniform mat4 uniformCameraViewMatrix;

varying vec2 vTextureCoord;
varying vec4 vColour;
varying vec3 vAmbientLighting;
varying vec3 vNormals;
varying vec4 vPosition;
varying vec3 vCameraPosition;

varying vec4 vWorldVertex;

void main(void)
{
	// Calculate position of vertex
	vWorldVertex = uMVMatrix * vec4(aVertexPosition, 1.0);
	vec4 viewVertex = uniformCameraViewMatrix * vWorldVertex;
	gl_Position = uPMatrix * viewVertex;

	// Transfer properties for fragment shader
	vTextureCoord = aTextureCoord;
	vColour = aColour;
	vNormals = aNormals;
	vPosition = gl_Position;
	vCameraPosition = aCameraPosition;

	// Ambient light settings
	highp vec3 ambientLight = vec3(0.4, 0.4, 0.4);
	highp vec3 directionalLightColor = vec3(0.2, 0.2, 0.5);
	highp vec3 directionalVector = vec3(0.85, 0.8, 0.75);

	// Calculate ambient light
	highp vec4 transformedNormal = uNMatrix * vec4(aNormals, 1.0);
	highp float directional = max(dot(transformedNormal.xyz, directionalVector), 0.0);
	vAmbientLighting = ambientLight + (directionalLightColor * directional);
}
