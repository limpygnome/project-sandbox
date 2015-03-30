game.ui =
{
	reset: function()
	{
		// Does nothing at present...
	},
	
	render: function()
	{
		// Switch to ortho
		mat4.ortho(projectSandbox.perspective, -1.0, 1.0, -1.0, 1.0, 1, 1000);
		
		// Render cash score
		
		// Render health bar
		
		// Render weapon icon
	}
}
