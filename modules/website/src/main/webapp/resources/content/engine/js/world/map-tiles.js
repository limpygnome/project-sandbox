projectSandbox.world.MapTiles = function() {

    projectSandbox.world.Map.call(this);

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
