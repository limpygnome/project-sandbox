game.ui =
{
	// Size of the UI viewport
	uiWidth: 800,
	uiHeight: 600,
	
	// Weapon icon
	iconWeapon: null,
	
	// Bounty / wanted stars
	starsIcon: null,
	starsOn: true,
	
	// Health
	healthBar: null,
	
	setup: function()
	{
		this.rebuildUI();
	},
	
	resize: function()
	{
		// TODO: call this.
	},
	
	rebuildUI: function()
	{
		var gl = projectSandbox.gl;
		var width = gl.viewportWidth;
		var height = gl.viewportHeight;
		var ratio = width / height;
		
		// Create weapons icon
		this.iconWeapon = new Primitive(48 * ratio, 48 * ratio);
		this.iconWeapon.setTexture("ui-weapons/fist");
		this.setPrimitivePosTopLeft(this.iconWeapon, width, height, 8, 8);
		
		// Create health bar
		this.healthBar = new PrimitiveBar(128, 8, true);
		this.setPrimitivePosTopLeft(this.healthBar, width, height, 12, 78);
		this.healthBar.setValue(0.8);
		this.healthBar.setColour(
			0.0,	1.0,	0.0,	1.0,
			1.0,	0.0,	0.0,	1.0
		);
		
		// Create bounty/wanted stars
		var starCount = 6;
		
		this.starsIcon = [];
		var star;
		for (var i = 0; i < starCount; i++)
		{
			star = new Primitive(16 * ratio, 16 * ratio);
			star.setTexture("ui/star_off");
			this.setPrimitivePosTopLeft(star, width, height, 8 + ((star.width) * i), 90);
			
			this.starsIcon[i] = star;
		}
	},
	
	setPrimitivePosTopLeft: function(primitive, viewWidth, viewHeight, x, y)
	{
		primitive.x = viewWidth - x - (primitive.width / 2.0);
		primitive.y = viewHeight - y - (primitive.height / 2.0);
	},

	reset: function()
	{
		// Does nothing at present...
	},
	
	render: function(gl, shaderProgram, modelView, perspective)
	{
		var width = gl.viewportWidth;
		var height = gl.viewportHeight;
		var ratio = width / height;
		
		// Switch into orthographic mode
		mat4.ortho(perspective, 0, this.uiWidth, 0, this.uiHeight, 0, 1);
		mat4.identity(modelView);
		
		// Render cash score
		
		// Render health bar
		this.healthBar.render(gl, shaderProgram, modelView, perspective);
		
		// Render weapon icon
		this.iconWeapon.render(gl, shaderProgram, modelView, perspective);
		
		// Render stars / bounty level
		if (this.starsOn)
		{
			for(var i = 0; i < this.starsIcon.length; i++)
			{
				this.starsIcon[i].render(gl, shaderProgram, modelView, perspective);
			}
		}
	}
}
