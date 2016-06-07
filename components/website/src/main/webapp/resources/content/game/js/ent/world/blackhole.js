game.entities.world.Blackhole = function()
{
    Entity.call(this,
        {
            model: "2d-rect",
            width: 4096.0,
            height: 4096.0
        }
    );

    this.setTexture("error");
    this.z = -0.6;
};

game.entities.world.Blackhole.inherits(Entity);

