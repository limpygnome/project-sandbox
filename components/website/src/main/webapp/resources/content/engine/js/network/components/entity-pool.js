projectSandbox.network.entityPool = (function() {

    // Stores typeId,[array of instances]
    var disposedEntityPool = new Map();

    var get = function (typeId)
    {
        // Attempt to find existing entity in pool
        var disposedArray = disposedEntityPool.get(typeId);
        var entity;

        if (disposedArray == null || disposedArray.length == 0)
        {
            // Create new entity
            entity = internalCreate(typeId);
        }
        else
        {
            // Take entity from end of pool
            entity = disposedArray.pop();

            // Reset entity ready for new life
            if (entity.reset)
            {
                entity.reset();
            }
        }

        // Add to depth tree
        projectSandbox.rendering.depthTree.update(entity);

        return entity;
    };

    var internalCreate = function (typeId)
    {
        // TODO: we need game, not engine, to produce entities based on type, thus add hook to game to do this instead...
        var entity = game.entityFactory.create(typeId);

        if (entity != null)
        {
            // Set typeId for use in pool when entity disposed
            entity.typeId = typeId;

            console.debug("engine/network/entityPool - created new entity - type id: " + typeId);
        }

        return entity;
    };

    var dispose = function (entity)
    {
        if (entity != null)
        {
            // Fetch array for type from pool
            var disposedArray = disposedEntityPool.get(typeId);

            // Create array if it doesn't exist
            if (disposedArray == null)
            {
                disposedArray = new Array();

                var typeId = entity.typeId;
                disposedEntityPool.set(typeId, disposedArray);
            }

            // Append entity to end of array
            disposedArray.push(entity);

            // Remove from depth tree
            projectSandbox.rendering.depthTree.remove(entity);
        }
    };

    return {
        get         : get,
        dispose     : dispose
    };

})();
