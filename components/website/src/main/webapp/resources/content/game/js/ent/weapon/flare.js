game.entities.world.Flare = function()
{
    Entity.call(this,
        {
            model: "2d-rect",
            width: 16.0,
            height: 16.0
        }
    );

    this.setTexture("error");
    this.z = -0.6;
};

game.entities.world.Flare.inherits(Entity);
