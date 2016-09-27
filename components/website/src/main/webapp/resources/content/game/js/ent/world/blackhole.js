{
    var entity = function()
    {
        Entity.call(this,
            {
                title: "Blackhole",
                model: "2d-rect",
                width: 4096.0,
                height: 4096.0
            }
        );

        this.setTexture("error");
        this.z = -0.6;
    };

    entity.typeId = 4000;
    entity.title = "Blackhole";
    entity.mapEditorEnabled = true;

    entity.inherits(Entity);

    game.entities.world.Blackhole = entity;
}
