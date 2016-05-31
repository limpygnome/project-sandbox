game.entities.world.Blackhole = function()
{
    Entity.call(this,
        {
            model: "2d-rect",
            width: 2048.0,
            height: 2048.0
        }
    );

    this.setTexture("error");
};

game.entities.world.Blackhole.inherits(Entity);

