function IceCreamVan()
{
    Entity.call(this,
        {
            title: "Ice Cream Truck",
            model: "3d-cube",
            width: 32.0,
            height: 64.0,
            depth: 10.0
        }
    );

    // Set custom params for this ent
    //this.setTexture("vehicles/ice-cream-van");
    this.setTexture("world/building");

    // Setup trail
    this.trail = new Trail(
        this,
        "error",
        32,
        32,
        100,
        2000,
        true,
        -2,
        2,
        -16,
        -16,
        true        // Compute lifespan
    );
}

IceCreamVan.typeId = 20;
IceCreamVan.title = "Ice Cream Van";
IceCreamVan.inherits(Entity);

IceCreamVan.prototype.logic = function()
{
    // Update trail
    this.trail.logic();
}

IceCreamVan.prototype.eventDeath = function()
{
    game.effects.createExplosion(this.x, this.y, 64, 4000, -2, 2);
}
