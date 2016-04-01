function YutamoC1()
{
    Entity.call(this,
        {
            model: "2d-rect",
            width: 45.0,
            height: 50.0
        }
    );

    this.setTexture("ships/yutamo-c1");

    this.trail = new Trail(
        this,
        "flames/blue",
        32,
        32,
        0,
        1000,
        true,
        -2,
        2,
        -25,
        -35
    );
}

YutamoC1.inherits(Entity);

YutamoC1.prototype.logic = function()
{
    // Update trail
    this.trail.logic(this);
}