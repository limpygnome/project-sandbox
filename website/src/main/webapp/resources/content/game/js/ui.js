game.ui =
{
	// Size of the UI viewport
	// TODO: update when resized
	uiWidth: null,
	uiHeight: null,
	
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

    testText: null,
    wrektContinueText: null,
    testError: null,
    wrektBackground2: null,
	
	setup: function()
	{
	    // Set UI size
        this.resize();
	},
	
	resize: function()
	{
	    var gl = projectSandbox.gl;

        // Recompute size
		this.uiWidth = gl.viewportWidth;
        this.uiHeight = gl.viewportHeight;

        console.debug("engine/ui - size set to " + this.uiWidth + "x" + this.uiHeight);

        // Rebuild UI
        this.rebuildUI();
	},
	
	rebuildUI: function()
	{
		var ratio = this.uiWidth / this.uiHeight;
		
		// Create weapons icon
		this.iconWeapon = new Primitive(48 * ratio, 48 * ratio);
		this.iconWeapon.setTexture("error");
		this.setPrimitivePosTopLeft(this.iconWeapon, 8, 8);
		
		// Create health bar
		this.healthBar = new PrimitiveBar(128, 8, true);
		this.setPrimitivePosTopLeft(this.healthBar, 12, 78);
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
			this.setPrimitivePosTopLeft(star, 8 + ((star.width) * i), 90);
			
			this.starsIcon[i] = star;
		}

		this.testText = projectSandbox.text.buildPrimitive('#wrekt', 80, 'white', 15.0, 'black');
        this.testText.x = this.uiWidth / 2.0;
        this.testText.y = this.uiHeight / 2.0;

        this.wrektContinueText = projectSandbox.text.buildPrimitive('press space to respawn', 45.0, 'white', 20.0, 'black');
        this.wrektContinueText.x = this.uiWidth / 2.0;
        this.wrektContinueText.y = this.wrektContinueText.height;

		this.testError = new Primitive(this.uiWidth, this.uiHeight);
		this.testError.x = this.testText.x;
		this.testError.y = this.testText.y;
		this.testError.setColour(0.0, 0.0, 0.0, 0.3);

		this.wrektBackground2 = new Primitive(this.uiWidth, this.testText.height);
		this.wrektBackground2.x = this.uiWidth / 2.0;
		this.wrektBackground2.y = this.uiHeight / 2.0;
		this.wrektBackground2.setColour(0.0, 0.0, 0.0, 0.1);
	},
	
	setPrimitivePosTopLeft: function(primitive, x, y)
	{
		primitive.x = this.uiWidth - x - (primitive.width / 2.0);
		primitive.y = this.uiHeight - y - (primitive.height / 2.0);
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

			if (!projectSandbox.network.closed)
			{
				console.warn("UI - unable to find player entity, cannot update UI");
			}
		}
	},
	
	render: function(gl, shaderProgram, modelView, perspective)
	{
	    // Disable depth for transparency
	    gl.disable(gl.DEPTH_TEST);

		// Switch into orthographic mode
		mat4.ortho(perspective, 0, this.uiWidth, 0, this.uiHeight, 0, 1);
		mat4.identity(modelView);


		this.testError.render(gl, shaderProgram, modelView, perspective);
		this.wrektBackground2.render(gl, shaderProgram, modelView, perspective);

		this.testText.render(gl, shaderProgram, modelView, perspective);
		this.wrektContinueText.render(gl, shaderProgram, modelView, perspective);

		
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

        // Re-enable depth testing
        gl.enable(gl.DEPTH_TEST);
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
