function Entity(params)
{
    Primitive.call(this, params);
    
    // This default value should be kept in sync with the default on the server
    this.health = 0.0;
    this.maxHealth = 0.0;
    this.dead = true;
}

Entity.inherits(Primitive);

Entity.prototype.prelogic = function()
{
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

Entity.prototype.readBytesUpdate = function(packet)
{
    // Nothing by default
}