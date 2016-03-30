projectSandbox.network.entityPool = (function() {

    // Stores typeId,[array of instances]
    var disposedEntityPool = new Map();

    var get = function (typeId)
    {
        // Attempt to find existing entity in pool
        var disposedArray = disposedEntityPool.get(typeId);

        if (disposedArray == null || disposedArray.length == 0)
        {
            // Create new entity
            return internalCreate(typeId);
        }
        else
        {
            // Take entity from end of pool
            var entity = disposedArray.pop();

            // Reset entity ready for new life
            if (entity.reset)
            {
                entity.reset();
            }

            return entity;
        }
    };

    var internalCreate = function (typeId)
    {
        var entity;

        switch (typeId)
        {
            case 0:
                entity = new Entity();
                break;
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
            case 20:
                entity = new IceCreamVan();
                break;
            case 21:
                entity = new RocketCar();
                break;
            case 22:
                entity = new Bus();
                break;
            case 1201:
                entity = new HealthPickup();
                break;
            default:
                console.error("engine/network/entityPool - unhandled ent type " + typeId);
                break;
        }

        if (entity != null)
        {
            // Set typeId for use in pool when entity disposed
            entity.typeId = typeId;
        }

        return entity;
    };

    var dispose = function (entity)
    {
        // Fetch array for type from pool
        var disposedArray = disposedEntityPool.get(typeId);

        // Create array if it doesn't exist
        if (disposedArray == null)
        {
            disposedArray = new Array();
            disposedArray.push(entity);

            var typeId = entity.typeId;
            disposedEntityPool.set(typeId, disposedArray);
        }

        // Append entity to end of array
        disposedArray.push(entity);
    };

    return {
        get         : get,
        dispose     : dispose
    };

})();
