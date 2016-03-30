function YutamoC1()
{
    Entity.call(this,
        {
            model: "2d-rect",
            width: 179.0,
            height: 202.0
        }
    );

    this.setTexture("ships/yutamo-c1");
}

YutamoC1.inherits(Entity);
