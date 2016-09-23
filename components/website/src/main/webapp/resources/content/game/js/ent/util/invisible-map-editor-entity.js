game.entities.util.InvisibleMapEditorEntity = function()
{
    Entity.call(this,
        {
            title: "Invisible Map Editor",
            model: "2d-rect",
            width: 16.0,
            height: 16.0
        }
    );

    this.setTexture("error");
};

game.entities.util.InvisibleMapEditorEntity.typeId = 901;
game.entities.util.InvisibleMapEditorEntity.title = "Invisible Editor";
game.entities.util.InvisibleMapEditorEntity.inherits(Entity);
