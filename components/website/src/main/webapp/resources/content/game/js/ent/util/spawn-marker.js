/*
    A client-side only entity for representing spawn markers whilst in map-editor mode.
*/
{
    var entity = function()
    {
        Entity.call(this,
            {
                title: "Spawn Marker",
                model: "2d-rect",
                width: 32.0,
                height: 32.0
            }
        );

        this.setTexture("white");
        this.setColour(1.0, 0.0, 0.0, 0.8);
    };

    entity.title = "Spawn";
    entity.typeId = 902;
    entity.mapEditorEnabled = true;

    entity.inherits(Entity);

    game.entities.util.SpawnMarker = entity;
}
