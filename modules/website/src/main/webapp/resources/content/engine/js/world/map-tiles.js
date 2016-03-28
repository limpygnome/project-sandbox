projectSandbox.world.MapTiles = function() {

    projectSandbox.world.Map.call(this);

    // Each type has {texture, height}
    this.types = [];

    // Each tile is a short indicating the type
    this.tiles = [];

    this.getWidth = function()
    {
        return this.width * this.tileSize;
    };

    this.getHeight = function()
    {
        return this.height * this.tileSize;
    };


};

projectSandbox.world.MapTiles.inherits(projectSandbox.world.Map);
