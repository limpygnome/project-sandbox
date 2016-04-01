function YutamoC1()
{
    Entity.call(this,
        {
            model: "2d-rect",
            width: 90.0,
            height: 100.0
        }
    );

    this.setTexture("ships/yutamo-c1");

    this.trail = new Trail(
        this,
        "flames/blue",
        64,
        64,
        60,
        1000,
        true,
        -2,
        2,
        -50,
        -70
    );
}

YutamoC1.inherits(Entity);

YutamoC1.prototype.logic = function()
{
    // Update trail
    this.trail.logic(this);
}