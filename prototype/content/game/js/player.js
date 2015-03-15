function Player()
{
    Entity.call(this, 16, 16);
    
    // Set custom params for this ent
    this.texture = projectSandbox.textures.get("players/default");
}

Player.prototype = new Entity();

Player.prototype.constructor = Player;
