game.ui.map =
{
    // Multiplier for scaling items on map
    mapMarkerMultiplier: 100.0,

    // Maximum distance for radar items
    radarDistance: 4096.0,
    radarDistanceHalf: null,

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
        // Setup radar distance half
        this.radarDistanceHalf = this.radarDistance / 2.0;

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
            // Reset markers
            this.markersReset(this.containerMap);
            this.markersReset(this.containerRadar);

            // Render all entities available
            var entity;
            for (var kv of projectSandbox.entities)
            {
                entity = kv[1];

                if (!entity.dead)
                {
                    this.markerUpdateMap(entity);
                    this.markerUpdateRadar(entity);
                }
            }

            // Purge old
            this.markersPurgeOld(this.containerMap);
            this.markersPurgeOld(this.containerRadar);
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

    markerUpdateMap: function (entity)
    {
        var marker = this.markerFetchOrCreate(this.containerMap, entity);

        // Calculate position as unit vector and multiply by container size
        var posX = (entity.x / this.mapWidth) * this.containerMapWidth;
        var posY = (entity.y / this.mapHeight) * this.containerMapHeight;

        // Calculate size relative to size of entity
        var width = (entity.width / this.mapWidth) * this.containerMapWidth * this.mapMarkerMultiplier;
        var height = (entity.height / this.mapHeight) * this.containerMapHeight * this.mapMarkerMultiplier;

        this.markerUpdate(marker, posX, posY, width, height);
    },

    markerUpdateRadar: function (entity)
    {
        // Check entity within range
        // -- Use camera since cheaper
        var distance = projectSandbox.utils.distance(projectSandbox.camera.x, projectSandbox.camera.y, entity.x, entity.y);

        if (distance < this.radarDistance)
        {
            // Compute size
            var width = 32;
            var height = 32;

            var playerEntity = projectSandbox.getPlayerEntity();

            // Offset from center of distance, convert to unit vector
            var posX = (entity.x - playerEntity.x + this.radarDistanceHalf) / this.radarDistance;
            var posY = (entity.y - playerEntity.y + this.radarDistanceHalf) / this.radarDistance;

            // Multiply by container size
            posX *= this.containerRadarWidth;
            posY *= this.containerRadarHeight;

            // Subtract marker size
            posX -= width / 2.0;
            posY -= height / 2.0;

            // Fetch marker and update
            var marker = this.markerFetchOrCreate(this.containerRadar, entity);
            this.markerUpdate(marker, posX, posY, width, height);
        }
    },

    markerUpdate: function (marker, x, y, width, height)
    {
        marker.css("left", x);
        marker.css("bottom", y);
        marker.css("width", width);
        marker.css("height", height);
        marker.removeClass("remove");
    },

    markerFetchOrCreate: function (container, entity)
    {
        var result = container.find(".marker_" + entity.id);

        if (result.size() == 0)
        {
            // Fetch special radar classes
            var specialClasses = entity.getRadarClasses ? entity.getRadarClasses() : "";

            // Add class for self
            if (entity.id == projectSandbox.playerEntityId)
            {
                specialClasses += " self";
            }

            // Add item
            container.append("<span class='marker_" + entity.id + " " + specialClasses + "'></span>");
        }

        return result;
    }

};
