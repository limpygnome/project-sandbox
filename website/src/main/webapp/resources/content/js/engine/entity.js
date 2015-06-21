function Entity(params)
{
	Primitive.call(this, params);
	
	// This default value should be kept in sync with the default on the server
	this.health = 0.0;
	this.maxHealth = 0.0;
}

Entity.inherits(Primitive);

Entity.prototype.readBytes_update = function(data, dataView, id, offset)
{
	// Nothing by default
	
	return offset;
}