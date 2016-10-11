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
    };

    entity.title = "Spawn";
    entity.typeId = 902;
    entity.mapEditorEnabled = true;

    entity.inherits(Entity);

    entity.prototype.readBytesCreate = function(packet)
    {
        var red = packet.readInt() / 255.0;
        var green = packet.readInt() / 255.0;
        var blue = packet.readInt() / 255.0;

        this.setColour(red, green, blue, 0.6);
    };

    game.entities.util.SpawnMarker = entity;
}
