projectSandbox.world.MapOpen = function() {};

projectSandbox.world.MapOpen.inherits(projectSandbox.world.Map);

projectSandbox.world.MapOpen.prototype.setup = function()
{
    console.debug("engine/world/map-open - limits: {width: " + this.limitWidth + ", height: " + this.limitHeight + "}, background: " + this.background);
}

projectSandbox.world.MapTiles.prototype.render = function(gl, shaderProgram, modelView, perspective)
{
}
