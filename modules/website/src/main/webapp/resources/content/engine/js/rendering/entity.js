/*
    Reuse & Pooling
    ===================>
    An entity will be reused by an entity pool, located in the engine. Thus if changes need to be reset, for a new life,
    implement the function "reset", which will be called before the entity is placed into the world for re-use.
*/

function Entity(params)
{
    Primitive.call(this, params);
    
    // This default value should be kept in sync with the default on the server
    this.health = 0.0;
    this.maxHealth = 0.0;
}

Entity.inherits(Primitive);

Entity.prototype.coreLogic = function()
{
    /*
        These values must be copied in logic cycle to avoid camera and entity updating at different times, which causes
        a jitter/shaking effect.
    */

    // Copy updated values across
    if (this.updatedX != null)
    {
        this.x = this.updatedX;
        this.updatedX = null;
    }

    if (this.updatedY != null)
    {
        this.y = this.updatedY;
        this.updatedY = null;
    }

    if (this.updatedRotation != null)
    {
        this.rotation = this.updatedRotation;
        this.updatedRotation = null;
    }
}

Entity.prototype.readBytesUpdate = function (packet)
{
    // Nothing by default
}