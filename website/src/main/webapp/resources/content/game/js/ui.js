game.ui =
{
	// Size of the UI viewport
	// TODO: update when resized
	uiWidth: 800,
	uiHeight: 600,
	
	// Indicates if to render UI regarding player
	renderPlayerUI: false,
	renderWeapon: false,
	
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
		this.iconWeapon.setTexture("error");
		this.setPrimitivePosTopLeft(this.iconWeapon, width, height, 8, 8);
		
		// Create health bar
		this.healthBar = new PrimitiveBar(128, 8, true);
		this.setPrimitivePosTopLeft(this.healthBar, width, height, 12, 78);
		this.healthBar.setValue(0.5);
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
	
	logic: function()
	{
		var plyEntId = projectSandbox.playerEntityId;
		var ent = projectSandbox.entities.get(plyEntId);
		
		if (ent != null)
		{
			// Update health
			var maxHealth = ent.maxHealth;
			var health = ent.health;
			
			var healthPercent;
			if (health > 0 && maxHealth > 0)
			{
				healthPercent = health / maxHealth;
			}
			else
			{
				healthPercent = 0.0;
			}
			
			this.healthBar.setValue(healthPercent);
			
			// Update player UI to render
			this.renderPlayerUI = true;
		}
		else
		{
			this.renderPlayerUI = false;

			if (!projectSandbox.comms.closed)
			{
				console.warn("UI - unable to find player entity, cannot update UI");
			}
		}
	},
	
	render: function(gl, shaderProgram, modelView, perspective)
	{
		var width = gl.viewportWidth;
		var height = gl.viewportHeight;
		var ratio = width / height;
		
		// Switch into orthographic mode
		mat4.ortho(perspective, 0, this.uiWidth, 0, this.uiHeight, 0, 1);
		mat4.identity(modelView);
		
		if (this.renderPlayerUI)
		{
			// Render cash score
			
			// Render health bar
			this.healthBar.render(gl, shaderProgram, modelView, perspective);
			
			// Render weapon icon
			if (this.renderWeapon)
			{
			    this.iconWeapon.render(gl, shaderProgram, modelView, perspective);
			}
			
			// Render stars / bounty level
			if (this.starsOn)
			{
				for(var i = 0; i < this.starsIcon.length; i++)
				{
					this.starsIcon[i].render(gl, shaderProgram, modelView, perspective);
				}
			}
		}
	},

	hookPlayer_entChanged: function()
	{
	    // Assume inventory reset
	    this.renderWeapon = false;
	},

	hookInventory_selectedChanged: function()
	{
		console.debug("UI - inventory selectedChanged hook invoked");

		// Update icon to current item
		var item = projectSandbox.inventory.getSelected();

		if (item != null)
		{
            this.iconWeapon.setTexture(item.getIcon());
            this.renderWeapon = true;
        }
        else
        {
            this.renderWeapon = false;
        }
	}

}
