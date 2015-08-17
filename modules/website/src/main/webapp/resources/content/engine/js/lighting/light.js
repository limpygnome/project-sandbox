function Light(gl, shaderProgram, index)
{
    this.gl = gl;
    this.shaderProgram = shaderProgram;
    this.index = index;

    this.reset();
}

Light.prototype.reset = function()
{
    // Setup default values
    this.on = false;
    this.distance = 200.0;
    this.colour = [1.0, 1.0, 1.0];
    this.position = [0.0, 0.0, 0.0];
    this.rotation = 0.0;
    this.coneAngle = 65.0;
    this.constantAttenuation = 0.2;
    this.linearAttenuation = 0.001;
    this.quadraticAttenuation = 0.000001;
}

Light.prototype.setOn = function (value)
{
    this.on = value;
    this.gl.uniform1f(this.shaderProgram.lights[index].on, this.on ? 1.0 : 0.0);
}

Light.prototype.setDistance = function (value)
{
    this.distance = value;
    this.gl.uniform1f(this.shaderProgram.lights[index].distance, this.distance);
}

Light.prototype.setColour = function (value)
{
    this.colour = colour;
    this.gl.uniform3fv(this.shaderProgram.lights[index].colour, this.colour);
}

Light.prototype.setPosition = function (value)
{
    this.position = value;
    this.gl.uniform3fv(this.shaderProgram.lights[index].position, this.position);
}

Light.prototype.setRotation = function (value)
{
    this.rotation = value;
    this.gl.uniform1f(this.shaderProgram.lights[index].rotation, this.rotation);
}

Light.prototype.setConeAngle = function (value)
{
    this.coneAngle = coneAngle;
    this.gl.uniform1f(this.shaderProgram.lights[index].coneAngle, this.coneAngle);
}
