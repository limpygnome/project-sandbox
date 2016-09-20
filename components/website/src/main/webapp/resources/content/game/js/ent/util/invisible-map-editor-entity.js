game.entities.util.InvisibleMapEditorEntity = function()
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

game.entities.util.InvisibleMapEditorEntity.inherits(Entity);
