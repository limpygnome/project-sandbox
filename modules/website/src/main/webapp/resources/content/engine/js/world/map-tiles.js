projectSandbox.world.MapTiles = function() {

    // Each tile is a short indicating the type
    var types = [];

    // Each tile is a short indicating the type
    var tiles = [];

    var getWidth = function()
    {
        return this.width * this.mapTileSize;
    };

    var getHeight = function()
    {
        return this.height * this.mapTileSize;
    };

};

projectSandbox.world.MapTiles.inherits(projectSandbox.world.Map);
