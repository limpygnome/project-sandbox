function Trail(primitive, textureName, width, height, delay, lifespan, fade, offsetXMin, offsetXMax, offsetYMin, offsetYMax, computeLifespan)
{
    this.primitive = primitive;
    this.prevx = primitive.x;
    this.prevy = primitive.y;
    
    this.textureName = textureName;
    this.width = width;
    this.height = height;
    this.delay = delay;
    this.lifespan = lifespan;
    
    this.offsetXMin = offsetXMin;
    this.offsetXMax = offsetXMax;
    this.offsetYMin = offsetYMin;
    this.offsetYMax = offsetYMax;
    
    this.lastEffect = -1;
    this.computeLifespan = computeLifespan;
}

Trail.prototype.logic = function()
{
    this.moved = this.primitive.x != this.prevx || this.primitive.y != this.prevy;
    this.prevx = this.primitive.x;
    this.prevy = this.primitive.y;
    
    // Only create trail if we move
    if (this.moved && (!this.computeLifespan || (this.primitive.health < this.primitive.maxHealth) ))
    {
        var currTime = projectSandbox.currentTime;
        
        if (currTime - this.lastEffect > this.delay)
        {
            // Compute lifespan
            var lifespan;

            if (this.computeLifespan)
            {
                lifespan = this.lifespan * (1.0 - (this.primitive.health / this.primitive.maxHealth));
            }
            else
            {
                lifespan = this.lifespan;
            }

            // Create effect
            var randX = projectSandbox.utils.rand(this.offsetXMin, this.offsetXMax);
            var randY = projectSandbox.utils.rand(this.offsetYMin, this.offsetYMax);
            
            // Rotate offset
            var rotatedV = projectSandbox.utils.vectorRotate(0.0, 0.0, randX, randY, -this.primitive.rotation);
            
            randX = rotatedV[0];
            randY = rotatedV[1];
            
            var effect = new Effect(this.textureName, this.width, this.height, this.primitive.x + randX, this.primitive.y + randY, -0.5, lifespan, true);
            effect.rotation = this.primitive.rotation;

            projectSandbox.addEffect(effect);

            // Update last time created
            this.lastEffect = currTime;
        }
    }

}
