function Rocket()
{
    Entity.call(this,
        {
            model: "2d-rect",
            width: 9.0,
            height: 12.0
        }
    );

    this.setTexture("error");

    // Setup trail
    this.trail = new Trail(
        this,
        "error",
        16,
        16,
        100,
        5000,
        true,
        -2,
        2,
        -16,
        -16
    );
}

Rocket.inherits(Entity);

Rocket.prototype.logic = function()
{
    // Update trail
    this.trail.logic();

//    console.debug(this.x + "," + this.y);
}

Rocket.prototype.eventDeath = function()
{
    game.effects.createExplosion(this.x, this.y, 256, 8000, -2.0, 2.0);
}

Rocket.prototype.getRadarClasses = function()
{
    return "danger";
}
