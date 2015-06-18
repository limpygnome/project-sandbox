attribute vec3 aVertexPosition;
attribute vec2 aTextureCoord;
attribute vec4 aColour;
uniform mat4 uMVMatrix;
uniform mat4 uPMatrix;
varying vec2 vTextureCoord;
varying vec4 vColour;
varying vec4 vPosition;

void main(void)
{
    vPosition = uMVMatrix * vec4(aVertexPosition, 1.0);
	gl_Position = uPMatrix * uMVMatrix * vec4(aVertexPosition, 1.0);

	vTextureCoord = aTextureCoord;
	vColour = aColour;
}
