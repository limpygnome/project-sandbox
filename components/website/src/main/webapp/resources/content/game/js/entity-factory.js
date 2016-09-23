game.entityFactory = (function(){

    // typeId (int) -> entity prototype
    var typeMap = new Map();

    $(document).on("ready", function() {
        // Add types to map - this needs to be manually maintained for now...
        add(0, Entity);

        // -- Living
        add(Player);
        add(Sentry);
        add(Pedestrian);
        add(Rocket);
        add(game.entities.world.Flare);

        // -- Vehicles
        add(IceCreamVan);
        add(RocketCar);
        add(Bus);

        // -- Ships
        add(Fighter);
        add(Destroyer);

        // -- Pickups
        add(HealthPickup);

        // -- Environment
        add(game.entities.world.Blackhole);

        // -- Utility
        add(game.entities.util.InvisibleMapEditorEntity);
    });

    var add = function(type)
    {
        typeMap.set(type.typeId, type);
    };

    var create = function(typeId)
    {
        var instance = null;

        // Fetch type from map
        var type = typeMap.get(typeId);

        if (type != null)
        {
            // Create new instance
            instance = new type();
        }
        else
        {
            console.error("game/entityFactory - unhandled entity type id: " + typeId);
        }

        return instance;
    };

    return {
        create  : create,
        typeMap : typeMap
    };

})();
