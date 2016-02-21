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

		// Fetch render container
		var renderContainer = $("#ps-render-container");

        // Hook mouse-down
        $(renderContainer).bind("mousedown", function(e) {
            $(renderContainer).focus();
            self.handleMouseDown(e.originalEvent);
            return false;
        });

        // Hook mouse-up
        $(renderContainer).bind("mouseup", function(e) {
            self.handleMouseMove(e.originalEvent);
            return false;
        });

        // Hook mouse scroll wheel
		$(renderContainer).bind("mousewheel", function(e) {
		    self.handleMouseWheelEvent(e.originalEvent);
            return false;
		});
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