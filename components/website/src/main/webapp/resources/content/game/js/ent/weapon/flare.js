game.entities.world.Flare = function()
{
    Entity.call(this,
        {
            title: "Flare",
            model: "2d-rect",
            width: 32.0,
            height: 32.0
        }
    );

    this.setTexture("error");
    this.z = -0.6;
};

game.entities.world.Flare.typeId = 610;
game.entities.world.Flare.title = "Flare";
game.entities.world.Flare.inherits(Entity);
