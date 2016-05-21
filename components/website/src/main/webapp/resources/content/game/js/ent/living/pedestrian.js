function Pedestrian()
{
    Entity.call(this,
        {
            model: "2d-rect",
            width: 16.0,
            height: 20.0
        }
    );
    
    // Set custom params for this ent
    this.setTexture("players/default");
    
    this.running = false;
    
    this.trail = new Trail(
        this,
        "error",
        16,
        16,
        400,
        500,
        true,
        -2,
        2,
        -2,
        2
    );
}

Pedestrian.inherits(Entity);

Pedestrian.prototype.logic = function()
{
    // Update trail
    this.trail.logic(this);
    
    // Update running state
    var moved = this.trail.moved;
    
    if (moved && !this.running)
    {
        this.setTexture("players/default_running");
    }
    else if (!moved && this.running)
    {
        this.setTexture("players/default");
    }
    
    // Update running state
    this.running = moved;
}
