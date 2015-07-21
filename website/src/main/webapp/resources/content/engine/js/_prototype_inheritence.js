Function.prototype.inherits = function(parent)
{
	this.prototype = Object.create(parent.prototype);
	this.prototype.constructor = this;
	this.prototype.parent = parent.prototype;

	return this;
}
