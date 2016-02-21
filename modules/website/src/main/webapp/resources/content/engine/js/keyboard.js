projectSandbox.keyboard =
{
	keys: new Map(),
	
	hook: function()
	{
		var self = this;

        // Fetch render container
        var renderContainer = $("#ps-render-container");

		// Hook key down...
		$(renderContainer).keydown(function (event) {
		    self.handlerDown(event);
		});

		// Hook key up...
		$(renderContainer).keyup(function (event) {
            self.handlerUp(event);
        });

        // Put focus on render container
        $(renderContainer).focus();
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
