function YutamoC1()
{
    Entity.call(this,
        {
            model: "2d-rect",
            width: 45.0,
            height: 50.0
        }
    );

    this.setTexture("ships/yutamo-c1");

    this.trailLeft = new Trail(
        this,
        "flames/blue",
        16,
        16,
        0,
        1000,
        true,
        -18,
        -18,
        -18,
        -18
    );

    this.trailRight = new Trail(
        this,
        "flames/blue",
        16,
        16,
        0,
        1000,
        true,
        16,
        16,
        -18,
        -18
    );
}

YutamoC1.inherits(Entity);

YutamoC1.prototype.logic = function()
{
    // Update trail
    this.trailLeft.logic(this);
    this.trailRight.logic(this);
}

YutamoC1.prototype.eventDeath = function()
{
    game.effects.createExplosion(this.x, this.y, 64, 4000, -2, 2);
}
