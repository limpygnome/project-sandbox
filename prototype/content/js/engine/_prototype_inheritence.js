Function.prototype.inherits = function(parent)
{
	if (parent.constructor == Function)
	{
		this.prototype = new parent;
		this.prototype.constructor = this;
		this.prototype.parent = parent.prototype;
	}
	else
	{
		this.prototype = parent;
		this.prototype.constructor = this;
		this.prototype.parent = parent;
	}
	return this;
}
