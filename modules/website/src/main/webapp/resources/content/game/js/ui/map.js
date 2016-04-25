game.ui.map =
{
    // Multiplier for scaling items on radar
    markerMultiplier: 100.0,

    // The container for the map
    container: null,

    // The size of the container
    containerWidth: 0,
    containerHeight: 0,

    // The size of the map
    mapWidth: null,
    mapHeight: null,


    reset: function()
    {
        // Fetch map container and cache size
        this.container = $("#ps-map");
        this.containerWidth = this.container.width();
        this.containerHeight = this.container.height();

        // Reset map size
        this.mapWidth = null;
        this.mapHeight = null;

        // Fetch map marker
        this.marker = $("#ps-map .marker");
    },

    logic: function()
    {
        if (this.mapWidth != null && this.mapHeight != null)
        {
            // Reset markers
            this.markersReset();

            // Render all entities available
            for (var kv of projectSandbox.entities)
            {
                this.markerUpdate(kv[1]);
            }

            // Purge old
            this.markersPurgeOld();
        }
        else if (projectSandbox.map != null)
        {
            this.mapWidth = projectSandbox.map.getWidth();
            this.mapHeight = projectSandbox.map.getHeight();

            console.debug("game / ui / map - loaded map");
        }
    },

    markersReset: function()
    {
        // Add class to all markers for removal
        $("#ps-map span").addClass("remove");
    },

    markersPurgeOld: function()
    {
        // Remove markers with class for removal
        $("#ps-map span.remove").remove();
    },

    markerUpdate: function (entity)
    {
        var marker = this.markerFetchOrCreate(entity);

        // Calculate position as unit vector and multiply by container size
        var posX = (entity.x / this.mapWidth) * this.containerWidth;
        var posY = (entity.y / this.mapHeight) * this.containerHeight;

        // Convert to container position
        marker.css("left", posX);
        marker.css("bottom", posY);
        marker.css("width", (entity.width / this.mapWidth) * this.containerWidth * this.markerMultiplier);
        marker.css("height", (entity.height / this.mapHeight) * this.containerHeight * this.markerMultiplier);
        marker.removeClass("remove");
    },

    markerFetchOrCreate: function (entity)
    {
        var result = $("#marker_" + entity.id);

        if (result.size() == 0)
        {
            // Fetch special radar classes
            var specialClasses = entity.getRadarClasses ? entity.getRadarClasses() : null;

            // Add item
            $("#ps-map").append("<span id='marker_" + entity.id + "'" + (specialClasses ? "class='" + specialClasses + "'" : "") + "></span>");
        }

        return result;
    }

};
