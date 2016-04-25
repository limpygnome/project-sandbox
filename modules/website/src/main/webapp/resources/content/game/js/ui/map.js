game.ui.map =
{
    // The container for the map
    container: null,

    // The element used to show the position of the entity on the map
    marker: null,


    reset: function()
    {
        // Cache map size
        // TODO: conisider if map reset calls ui reset

        // Fetch map container
        container = $("#ps-map");

        // Fetch map marker
        marker = $("#ps-map .marker");
    }

    logic: function()
    {
        // Get position of player's entity
        var playerEntityId = projectSandbox.playerEntityId;
        var entity = projectSandbox.entities.get(plyEntId);

        // Get total size of map
        var mapWidth = projectSandbox.map.getWidth();
        var mapHeight = projectSandbox.map.getWidth();

        // Get total size of container
        var containerWidth = this.container.width();
        var containerHeight = this.container.height();

        // Calculate position as unit vector and multiply by container size
        var posX = (entity.x / mapWidth) * containerWidth;
        var posY = (entity.y / mapHeight) * containerHeight;

        // Convert to container position
        // TODO: factor size of marker
    }

};
