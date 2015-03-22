function IceCreamVan()
{
    Entity.call(this, 32, 64);
    
    // Set custom params for this ent
    this.texture = projectSandbox.textures.get("vehicles/ice-cream-van");
}

IceCreamVan.prototype = new Entity();

IceCreamVan.prototype.constructor = IceCreamVan;

IceCreamVan.prototype.logic = function()
{
	// TODO: create smoke particles behind van
}
