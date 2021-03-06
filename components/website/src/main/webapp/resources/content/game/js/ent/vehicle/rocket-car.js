function RocketCar()
{
    Entity.call(this,
        {
            title: "Rocket Car",
            model: "3d-cube",
            width: 24.0,
            height: 36.0,
            depth: 8.0
        }
    );

    // Set custom params for this ent
    //this.setTexture("vehicles/ice-cream-van");
    this.setTexture("world/building");

    // Setup trail
    this.trail = new Trail(
        this,
        "error",
        24,
        24,
        100,
        1000,
        true,
        -2,
        2,
        -16,
        -16,
        true        // Compute lifespan
    );
}

RocketCar.typeId = 21;
RocketCar.title = "Rocket Car";
RocketCar.mapEditorEnabled = true;

RocketCar.inherits(Entity);

RocketCar.prototype.logic = function()
{
    // Update trail
    this.trail.logic();
}

RocketCar.prototype.eventDeath = function()
{
    game.effects.createExplosion(this.x, this.y, 64, 4000, -4, 4);
}
