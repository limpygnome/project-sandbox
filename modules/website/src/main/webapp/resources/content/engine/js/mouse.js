projectSandbox.mouse =
{
	// Indicates if a button is down
	left: false,
	right: false,
	
	// Indicates cursor position in canvas
	x: 0,
	y: 0,
	
	// Indicates scroll-wheel delta
	scrollDelta: 0,
	
	hook: function()
	{
		var self = this;

		// TODO: fix hook events not being invoked
		// Hook mouse down/up/movement
		projectSandbox.canvas.onmousedown = function(event)
		{
			self.handleMouseDown(event);
			return false;
		};
		projectSandbox.canvas.onmouseup = function(event)
		{
			self.handleMouseUp(event);
			return false;
		};
		projectSandbox.canvas.onmousemove = function(event)
		{
			self.handleMouseMove(event);
			return false;
		};
		
		// Hook scroll wheel
		projectSandbox.canvas.onmousewheel = function(event)
		{
			self.handleMouseWheelEvent(event);
			return false;
		};
	},
	
	handleMouseDown: function(event)
	{
		switch(event.button)
		{
			case 0:
				this.left = true;
				break;
			case 2:
				this.right = true;
				break;
		}
	},
	
	handleMouseUp: function(event)
	{
		switch(event.button)
		{
			case 0:
				this.left = false;
				break;
			case 2:
				this.right = false;
				break;
		}
	},
	
	handleMouseMove: function(event)
	{
		this.x = event.x;
		this.y = event.y;
	},
	
	handleMouseWheelEvent: function(event)
	{
		this.scrollDelta = event.wheelDelta;
	}
}