game.ui.map =
{
    // Multiplier for scaling items on radar
    markerMultiplier: 100.0,

    // The container for the map and cached size
    containerMap: null,
    containerMapWidth: 0,
    containerMapHeight: 0,

    // The container for radar and cached size
    containerRadar: null,
    containerRadarWidth: 0,
    containerRadarHeight: 0,

    // The size of the map
    mapWidth: null,
    mapHeight: null,


    reset: function()
    {
        // Fetch map container, remove markers and cache size
        this.containerMap = $("#ps-map");

        if (this.containerMap)
        {
            this.containerMap.children().remove();
            this.containerMapWidth = this.containerMap.width();
            this.containerMapHeight = this.containerMap.height();
        }

        // Fetch radar container, remove markers, cache size
        this.containerRadar = $("#ps-map-radar");

        if (this.containerRadar)
        {
            this.containerRadar.children().remove();
            this.containerRadarWidth = this.containerRadar.width();
            this.containerRadarHeight = this.containerRadar.height();
        }

        // Reset map size
        this.mapWidth = null;
        this.mapHeight = null;
    },

    logic: function()
    {
        if (this.mapWidth != null && this.mapHeight != null)
        {
            // Update map
            if (this.containerMap != null)
            {
                // Reset markers
                this.markersReset(this.containerMap);

                // Render all entities available
                for (var kv of projectSandbox.entities)
                {
                    this.markerUpdate(this.containerMap, kv[1]);
                }

                // Purge old
                this.markersPurgeOld(this.containerMap);
            }

            // Update radar
            if (this.containerRadar != null)
            {
                //
            }
        }
        else if (projectSandbox.map != null)
        {
            this.mapWidth = projectSandbox.map.getWidth();
            this.mapHeight = projectSandbox.map.getHeight();

            console.debug("game / ui / map - loaded map");
        }
    },

    markersReset: function (container)
    {
        // Add class to all markers for removal
        container.children().addClass("remove");
    },

    markersPurgeOld: function (container)
    {
        // Remove markers with class for removal
        container.find(".remove").remove();
    },

    markerUpdate: function (container, entity)
    {
        var marker = this.markerFetchOrCreate(container, entity);

        // Calculate position as unit vector and multiply by container size
        var posX = (entity.x / this.mapWidth) * this.containerMapWidth;
        var posY = (entity.y / this.mapHeight) * this.containerMapHeight;

        // Convert to container position
        marker.css("left", posX);
        marker.css("bottom", posY);
        marker.css("width", (entity.width / this.mapWidth) * this.containerMapWidth * this.markerMultiplier);
        marker.css("height", (entity.height / this.mapHeight) * this.containerMapHeight * this.markerMultiplier);
        marker.removeClass("remove");
    },

    markerFetchOrCreate: function (container, entity)
    {
        var result = $(".marker_" + entity.id);

        if (result.size() == 0)
        {
            // Fetch special radar classes
            var specialClasses = entity.getRadarClasses ? entity.getRadarClasses() : null;

            // Add item
            container.append("<span class='marker_" + entity.id + (specialClasses ? " " + specialClasses : "") + "'></span>");
        }

        return result;
    }

};
