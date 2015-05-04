projectSandbox.keyboard =
{
	keys: new Map(),
	
	hook: function()
	{
		var self = this;
		window.onkeydown = function(event)
		{
			self.handlerDown(event);
		};
		window.onkeyup = function(event)
		{
			self.handlerUp(event);
		};
	},
	
	handlerDown: function(event)
	{
		var keyCode = event.keyCode;
		this.setKeyCode(keyCode, true);
	},
	
	handlerUp: function(event)
	{
		var keyCode = event.keyCode;
		this.setKeyCode(keyCode, false);
	},
	
	setKeyCode: function(keyCode, value)
	{
		var strKeyCode = String.fromCharCode(keyCode);
		this.keys.set(strKeyCode, value);
	},

	isKeyDown: function(keyCode)
	{
	    return this.keys.get(keyCode) && true;
	}
}
