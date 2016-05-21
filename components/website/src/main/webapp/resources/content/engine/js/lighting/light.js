function Light(gl, shaderProgram, index)
{
    this.gl = gl;
    this.shaderProgram = shaderProgram;
    this.index = index;

    this.init();
    this.reset();
}

Light.prototype.init = function()
{
    var gl = this.gl;
    var shaderProgram = this.shaderProgram;
    var index = this.index;

    // Create mapping to area of shader memory to each field...
    this.shaderProgram.lights[index] = {};

    this.shaderProgram.lights[index].on = gl.getUniformLocation(
        shaderProgram,
        "uLights[" + index + "].on"
    );
    this.shaderProgram.lights[index].distance = gl.getUniformLocation(
            shaderProgram,
            "uLights[" + index + "].distance"
    );
    this.shaderProgram.lights[index].colour = gl.getUniformLocation(
            shaderProgram,
            "uLights[" + index + "].colour"
    );
    this.shaderProgram.lights[index].position = gl.getUniformLocation(
            shaderProgram,
            "uLights[" + index + "].position"
    );
    this.shaderProgram.lights[index].rotation = gl.getUniformLocation(
            shaderProgram,
            "uLights[" + index + "].rotation"
    );
    this.shaderProgram.lights[index].coneAngle = gl.getUniformLocation(
            shaderProgram,
            "uLights[" + index + "].coneAngle"
    );
    this.shaderProgram.lights[index].constantAttenuation = gl.getUniformLocation(
            shaderProgram,
            "uLights[" + index + "].constantAttenuation"
    );
    this.shaderProgram.lights[index].linearAttenuation = gl.getUniformLocation(
            shaderProgram,
            "uLights[" + index + "].linearAttenuation"
    );
    this.shaderProgram.lights[index].quadraticAttenuation = gl.getUniformLocation(
            shaderProgram,
            "uLights[" + index + "].quadraticAttenuation"
    );
}

Light.prototype.reset = function()
{
    // Setup default values
    this.setOn(false);
    this.setDistance(200.0);
    this.setColour( [1.0, 1.0, 1.0] );
    this.setPosition( [50.0 * this.index, 200.0, 0.0] );
    this.setRotation(0.0);
    this.setConeAngle(65.0);
    this.setConstantAttenuation(0.2);
    this.setLinearAttenuation(0.001);
    this.setQuadraticAttenuation(0.000001);
}

Light.prototype.setOn = function (value)
{
    this.on = value;
    this.gl.uniform1f(
        this.shaderProgram.lights[this.index].on,
        this.on ? 1.0 : 0.0
    );
}

Light.prototype.setDistance = function (value)
{
    this.distance = value;
    this.gl.uniform1f(
        this.shaderProgram.lights[this.index].distance,
        this.distance
    );
}

Light.prototype.setColour = function (value)
{
    this.colour = value;
    this.gl.uniform3fv(
        this.shaderProgram.lights[this.index].colour,
        this.colour
    );
}

Light.prototype.setPosition = function (value)
{
    this.position = value;
    this.gl.uniform3fv(
        this.shaderProgram.lights[this.index].position,
        this.position
    );
}

Light.prototype.setRotation = function (value)
{
    this.rotation = value;
    this.gl.uniform1f(
        this.shaderProgram.lights[this.index].rotation,
        this.rotation
    );
}

Light.prototype.setConeAngle = function (value)
{
    this.coneAngle = value;
    this.gl.uniform1f(
        this.shaderProgram.lights[this.index].coneAngle,
        this.coneAngle
    );
}

Light.prototype.setConstantAttenuation = function (value)
{
    this.constantAttenuation = value;
    this.gl.uniform1f(
        this.shaderProgram.lights[this.index].constantAttenuation,
        this.constantAttenuation
    );
}

Light.prototype.setLinearAttenuation = function (value)
{
    this.linearAttenuation = value;
    this.gl.uniform1f(
        this.shaderProgram.lights[this.index].linearAttenuation,
        this.linearAttenuation
    );
}

Light.prototype.setQuadraticAttenuation = function (value)
{
    this.quadraticAttenuation = value;
    this.gl.uniform1f(
        this.shaderProgram.lights[this.index].quadraticAttenuation,
        this.quadraticAttenuation
    );
}
