
game.entities.util.InvisibleMapEntity = function()
{
    Entity.call(this,
        {
            model: "2d-rect",
            width: 16.0,
            height: 16.0
        }
    );

    this.setTexture("error");
};

game.entities.util.InvisibleMapEntity.inherits(Entity);
