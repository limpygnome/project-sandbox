function Destroyer()
{
    Entity.call(this,
        {
            model: "2d-rect",
            width: 167.0,
            height: 231.0
        }
    );

    this.setTexture("ships/destroyer");

    this.trailLeft = new Trail(
        this,
        "flames/blue",
        64,
        64,
        0,
        1000,
        true,
        -20,
        -20,
        -105,
        -105
    );

    this.trailRight = new Trail(
        this,
        "flames/blue",
        64,
        64,
        0,
        1000,
        true,
        20,
        20,
        -105,
        -105
    );
}

Destroyer.inherits(Entity);

Destroyer.prototype.logic = function()
{
    // Update trail
    this.trailLeft.logic(this);
    this.trailRight.logic(this);
}

Destroyer.prototype.eventDeath = function()
{
    game.effects.createExplosion(this.x, this.y, 64, 4000, -6, 6);
}
