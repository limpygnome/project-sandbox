projectSandbox.world.MapOpen = function()
{
};

projectSandbox.world.MapOpen.inherits(projectSandbox.world.Map);

projectSandbox.world.MapOpen.prototype.getWidth = function()
{
    return this.limitWidth;
}

projectSandbox.world.MapOpen.prototype.getHeight = function()
{
    return this.limitHeight;
}

projectSandbox.world.MapOpen.prototype.setup = function()
{
    // Set flag to state map is ready
    this.flagIsSetup = true;

    console.debug("engine/world/map-open - limits: {width: " + this.limitWidth + ", height: " + this.limitHeight + "}, background: " + this.background);
};

projectSandbox.world.MapOpen.prototype.render = function(gl, shaderProgram, modelView, perspective)
{
    // Render background
    if (this.background != null)
    {
        this.renderBackground(gl, shaderProgram, modelView, perspective);
    }
};

projectSandbox.world.MapOpen.prototype.renderBackground = function(gl, shaderProgram, modelView, perspective)
{
    if (this.background != null && projectSandbox.camera.limits != null)
    {
        // Create params for primitive
        var renderWidth = projectSandbox.camera.limits[0] * 2.0;
        var renderHeight = projectSandbox.camera.limits[1] * 2.0;

        var params = {
            width: renderWidth,
            height: renderHeight
        };

        // Create primitive
        this.backgroundPrimitive = new Primitive(params);

        this.backgroundPrimitive.setTexture(this.background);
        this.backgroundPrimitive.x = projectSandbox.camera.x;
        this.backgroundPrimitive.y = projectSandbox.camera.y;
        this.backgroundPrimitive.z = -1.0;

        // Render primitive
        this.backgroundPrimitive.render(gl, shaderProgram, modelView, perspective);
    }
};
