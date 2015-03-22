projectSandbox.keyboard =
{
	W: false,
	S: false,
	A: false,
	D: false,
	
	E: false,
	
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
		switch(keyCode)
		{
			case 87:
				this.W = value;
				break;
			case 83:
				this.S = value;
				break;
			case 65:
				this.A = value;
				break;
			case 68:
				this.D = value;
				break;
			case 69:
				this.E = value;
				break;
			default:
				console.warn("Keyboard - unhandled keycode " + keyCode);
				break;
		}
	}
}