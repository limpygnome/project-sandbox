projectSandbox.world.Map = function() {

    // Indicates if the map is setup
    this.flagIsSetup = false;

    // The z-level at which to render the map
    this.renderZ = -1.0;

};

projectSandbox.world.Map.prototype.isSetup = function()
{
    return this.flagIsSetup;
};
