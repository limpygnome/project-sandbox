projectSandbox.keyboard =
{
	// Movement
	W: false,
	S: false,
	A: false,
	D: false,
	
	// Special
	E: false,
	SPACEBAR: false,
	
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
				
			case 49:
				this.NUMBER1 = value;
				break;
			case 50:
				this.NUMBER2 = value;
				break;
			case 51:
				this.NUMBER3 = value;
				break;
			case 52:
				this.NUMBER4 = value;
				break;
			case 53:
				this.NUMBER5 = value;
				break;
			case 54:
				this.NUMBER6 = value;
				break;
			case 55:
				this.NUMBER7 = value;
				break;
			case 56:
				this.NUMBER8 = value;
				break;
			case 57:
				this.NUMBER9 = value;
				break;
			case 48:
				this.NUMBER0 = value;
				break;
				
			case 32:
				this.SPACEBAR = value;
				break;
			default:
				console.warn("Keyboard - unhandled keycode " + keyCode);
				break;
		}
	}
}