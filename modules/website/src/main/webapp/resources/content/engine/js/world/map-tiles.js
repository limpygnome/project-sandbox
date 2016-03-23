projectSandbox.world.MapTiles = function() {

    projectSandbox.world.Map.call(this);


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
