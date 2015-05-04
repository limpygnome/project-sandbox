projectSandbox.keyboard =
{
	// Movement
	W: false,
	S: false,
	A: false,
	D: false,

	Q: false,
	E: false,

	F: false,
	
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
		var strKeyCode = String.fromCharCode(keyCode);

		switch(strKeyCode)
		{
			case "W":
				this.W = value;
				break;
			case "S":
				this.S = value;
				break;
			case "A":
				this.A = value;
				break;
			case "D":
				this.D = value;
				break;

			case "Q":
                this.Q = value;
				break;
			case "E":
                this.E = value;
				break;

            case "F":
                this.T = value;
                break;
				
			case "1":
				this.NUMBER1 = value;
				break;
			case "2":
				this.NUMBER2 = value;
				break;
			case "3":
				this.NUMBER3 = value;
				break;
			case "4":
				this.NUMBER4 = value;
				break;
			case "5":
				this.NUMBER5 = value;
				break;
			case "6":
				this.NUMBER6 = value;
				break;
			case "7":
				this.NUMBER7 = value;
				break;
			case "8":
				this.NUMBER8 = value;
				break;
			case "9":
				this.NUMBER9 = value;
				break;
			case "0":
				this.NUMBER0 = value;
				break;
				
			case " ":
				this.SPACEBAR = value;
				break;
			default:
				console.warn("Keyboard - unhandled keycode " + keyCode);
				break;
		}
	}
}