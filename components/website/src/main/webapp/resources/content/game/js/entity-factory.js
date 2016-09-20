game.entityFactory = (function(){

    // typeId (int) -> entity prototype
    var typeMap = new Map();

    $(document).on("ready", function() {
        // Add types to map - this needs to be manually maintained for now...
        typeMap.set(0, Entity);

        // -- Living
        typeMap.set(1, Player);
        typeMap.set(500, Sentry);
        typeMap.set(510, Pedestrian);
        typeMap.set(600, Rocket);
        typeMap.set(610, game.entities.world.Flare);

        // -- Vehicles
        typeMap.set(20, IceCreamVan);
        typeMap.set(21, RocketCar);
        typeMap.set(22, Bus);

        // -- Ships
        typeMap.set(200, Fighter);
        typeMap.set(210, Destroyer);

        // -- Pickups
        typeMap.set(1201, HealthPickup);

        // -- Environment
        typeMap.set(4000, game.entities.world.Blackhole);

        // -- Utility
        typeMap.set(901, game.entities.util.InvisibleMapEditorEntity);
    });

    var create = function(typeId)
    {
        var instance = null;

        instance = new Entity();
        return instance;

        // Fetch type from map
        var type = typeMap.get(typeId);

        if (type != null)
        {
            // Create new instance
            instance = type();
        }
        else
        {
            console.error("game/entityFactory - unhandled entity type id: " + typeId);
        }

        return instance;
    };

    return {
        create  : create
    };

})();
