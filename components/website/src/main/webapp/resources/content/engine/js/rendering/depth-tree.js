projectSandbox.rendering.depthTree = function()
{
    var primitives = [];

    var update = function(primitive)
    {
        // Compute depth of primitive
        var depth = computeDepth(primitive);

        // Check if depth has changed
        var insertOnly = (primitive.depthTree == null);
        if (insertOnly || primitive.depthTree < depth)
        {
            // Cache depth on primitive
            primitive.depthTree = depth;

            // Update
            insertAndRemove(primitive, false, insertOnly);
        }
    };

    var render = function(gl, shaderProgram, modelView, perspective)
    {
        var primitive;
        for (var i = 0; i < primitives.length; i++)
        {
            primitive = primitives[i];

            if (projectSandbox.frustrum.intersects(primitive))
            {
                primitive.render(gl, shaderProgram, modelView, perspective);
            }
        }
    };

    var remove = function(primitive)
    {
        insertAndRemove(primitive, true);
    };

    /*
        This will remove a primitive, if it exists, from a pre-existing position; whilst also inserting the primitive
        in the correct place. If the removeOnly param is true, the primitive is removed only.
    */
    var insertAndRemove = function(primitive, removeOnly, insertOnly)
    {
        // Flags
        var inserted = removeOnly;  // Set straight to true if remove-only i.e. no need to insert
        var removed = insertOnly;   // Same for removal if inserting only

        var primitiveAtIndex;
        for (var i = 0; (i == 0 || i <= primitives.length) && (!inserted || !removed); i++)
        {
            primitiveAtIndex = primitives[i]; // May throw exception in some browsers due to being out of range, fix if so...

            if (!removed && primitiveAtIndex != null && primitiveAtIndex == primitive)
            {
                // Remove current primitive and reset depthTree flag
                primitives.splice(i, 1);
                primitiveAtIndex.depthTree = null;

                removed = true;
            }
            else if (!inserted && (primitiveAtIndex == null || primitiveAtIndex.depthTree > primitive.depthTree))
            {
                // Insert primitive at current index
                primitives.splice(i, 0, primitive);
                inserted = true;
            }
        }

        if (!inserted)
        {
            console.warn("engine/rendering/depth-tree - failed to insert primitive - " + primitive);
        }
    };

    /*
        Computes depth of primitive.

        Currently using z only, since this is a top-down game.
        TODO: support 3d for maps with towers etc
    */
    var computeDepth = function(primitive)
    {
        return primitive.z;
    };

    var reset = function()
    {
        primitives = [];
    };

    var getTotalPrimitives = function()
    {
        return primitives.length;
    };

    return {
        update              : update,
        remove              : remove,
        render              : render,
        reset               : reset,
        getTotalPrimitives  : getTotalPrimitives
    };

}();
