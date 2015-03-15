function Player()
{
    Entity.call(this, 10, 16);
    
    // Set custom params for this ent
    this.texture = projectSandbox.textures.get("players/default");
    this.running = false;
}

Player.prototype = new Entity();

Player.prototype.constructor = Player;

Player.prototype.logic = function()
{
    var keyDown = projectSandbox.keyboard.W || projectSandbox.keyboard.S;
    
    if (keyDown && !this.running)
    {
        this.texture = projectSandbox.textures.get("players/default_running");
    }
    else if (!keyDown && this.running)
    {
        this.texture = projectSandbox.textures.get("players/default");
    }
    
    // Update running state
    this.running = keyDown;
}
