game.entities.world.Flare = function()
{
    Entity.call(this,
        {
            model: "2d-rect",
            width: 32.0,
            height: 32.0
        }
    );

    this.setTexture("error");
    this.z = -0.6;
};

game.entities.world.Flare.inherits(Entity);
