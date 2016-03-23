projectSandbox.world.MapTiles = function() {

    projectSandbox.world.Map.call(this);


    this.getWidth = function()
    {
        return this.width * this.mapTileSize;
    };

    this.getHeight = function()
    {
        return this.height * this.mapTileSize;
    };


};

projectSandbox.world.MapTiles.inherits(projectSandbox.world.Map);
