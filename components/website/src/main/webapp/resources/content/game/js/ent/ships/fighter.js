function Fighter()
{
    Entity.call(this,
        {
            title: "Fighter",
            model: "2d-rect",
            width: 45.0,
            height: 50.0
        }
    );

    this.setTexture("ships/fighter");

    this.trailLeft = new Trail(
        this,
        "flames/blue",
        16,
        16,
        0,
        800,
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
        800,
        true,
        16,
        16,
        -18,
        -18
    );
}

Fighter.typeId = 200;
Fighter.title = "Fighter";
Fighter.inherits(Entity);

Fighter.prototype.logic = function()
{
    // Update trail
    this.trailLeft.logic(this);
    this.trailRight.logic(this);
}

Fighter.prototype.eventDeath = function()
{
    game.effects.createExplosion(this.x, this.y, 64, 4000, -2, 2);
}
