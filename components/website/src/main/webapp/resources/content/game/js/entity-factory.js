game.entityFactory = {

    create : function (typeId)
    {
        var entity;

        switch (typeId)
        {
            case 0:
                entity = new Entity();
                break;

            // Living
            case 1:
                entity = new Player();
                break;
            case 500:
                entity = new Sentry();
                break;
            case 510:
                entity = new Pedestrian();
                break;
            case 600:
                entity = new Rocket();
                break;
            case 610:
                entity = new game.entities.world.Flare();
                break;

            // Vehicles
            case 20:
                entity = new IceCreamVan();
                break;
            case 21:
                entity = new RocketCar();
                break;
            case 22:
                entity = new Bus();
                break;

            // Ships
            case 200:
                entity = new Fighter();
                break;
            case 210:
                entity = new Destroyer();
                break;

            // Pickups
            case 1201:
                entity = new HealthPickup();
                break;

            // Environment
            case 4000:
                entity = new game.entities.world.Blackhole();
                break;

            // Utility
            case 901:
                entity = new game.entities.util.InvisibleMapEntity();
                break;

            default:
                entity = null;
                console.error("engine/network/entityPool - unhandled entity type id: " + typeId);
                break;
        }

        return entity;
    }

};
