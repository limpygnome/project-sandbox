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



    // Elements - main
    elementRender: null,
    elementDeathScreen: null,

    // Elements - UI
    elementUI: null,
	elementUIHealthBar: null,
	elementUIInventory: null,


	setup: function()
	{
	    // Fetch elements
	    // -- Main
	    this.elementRender = document.getElementById("ps_render");
	    this.elementDeathScreen = document.getElementById("ps-death-screen");

	    // -- UI
	    this.elementUI = document.getElementById("ps-ui");
        this.elementUIHealthBar = document.getElementById("ps-ui-healthbar-fill");
        this.elementUIInventory = document.getElementById("ps-ui-inventory");



	    // Bind resize event for window
        $(window).resize(function () {
            game.ui.resize();
        });

	    // Bind death screen to close
        $(this.elementDeathScreen).keyup(function (event) {
        	if (String.fromCharCode(event.which) == " ")
        	{
            	game.ui.deathScreenHide();
            }
        });

        // Bind options / fullscreen
        $(".options.button.fullscreen").click(function () {
            game.ui.toggleFullScreen();
        });



	    // Set UI size
        this.resize();
	},
	
	resize: function()
	{
	    var gl = projectSandbox.gl;

        // Calculate size of render area
        var totalWidth = $("#projectsandbox").width();
        var sidebarLeftWidth = $("#ps-sidebar-left").width();
        var sidebarRightWidth = $("#ps-sidebar-right").width();

        var newWidth = totalWidth - (sidebarLeftWidth + sidebarRightWidth);
        newWidth *= 0.95;
        var newHeight = newWidth / 1.33; $("#projectsandbox").height();

        // Update render canvas
        $(this.elementRender).width(newWidth).height(newHeight);

        // Update death screen
        $(this.elementDeathScreen).width(newWidth).height(newHeight);

        // Apply death screen offset
        this.deathScreenOffset();

        // Update UI
        $(this.elementUI).width(newWidth).height(newHeight);

        // Apply offset to UI
        this.uiOffset();

        console.debug("engine/ui - render size changed - " + newWidth + "x" + newHeight);

        // Recompute size of UI
		this.uiWidth = gl.viewportWidth;
        this.uiHeight = gl.viewportHeight;

        console.debug("engine/ui - size set to viewport - " + this.uiWidth + "x" + this.uiHeight);

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
			// Check if dead
			if (maxHealth != -1 && health <= 0.0)
			{
			    this.renderPlayerUI = false;
			}
			else
			{
                // Update health bar
                var maxHealth = ent.maxHealth;
                var health = ent.health;

                var healthPercent;
                if (health > 0 && maxHealth > 0 && health <= maxHealth)
                {
                    healthPercent = (health / maxHealth) * 100.0;
                }
                else
                {
                    healthPercent = 0;
                }

                $(this.elementUIHealthBar).width(healthPercent + "%");

                this.healthBar.setValue(healthPercent);

			    // Update player UI to render
			    this.renderPlayerUI = true;
			}
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
		return;

	    // Disable depth for transparency
	    gl.disable(gl.DEPTH_TEST);

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

        // Re-enable depth testing
        gl.enable(gl.DEPTH_TEST);
	},

	hookPlayer_entChanged: function()
	{
	    // Assume inventory reset
	    this.renderWeapon = false;
	},

	hookPlayer_entKilled: function(causeText)
	{
	    this.deathScreenShow(causeText);
	},

    deathScreenShow: function(causeText)
    {
        // Set cause text
        $("#ps-death-screen-cause").text(causeText);

        // Set to visible
        $(this.elementDeathScreen).css({
            "visibility" : "visible"
        });

        // Set focus to control
        $(this.elementDeathScreen).focus();

        // Apply offset
        this.deathScreenOffset();
    },

	deathScreenHide: function()
	{
        $(this.elementDeathScreen).css({
            "visibility" : "hidden"
        });
	},

	deathScreenOffset: function()
	{
	    // Set offset of death screen to render location
        $(this.elementDeathScreen).offset(
            $("#ps_render").position()
        );
	},

	uiOffset: function()
	{
	    $(this.elementUI).offset(
            $(this.elementRender).position()
        );
	},

	toggleFullScreen: function()
	{
	    var isFullscreen = document.fullscreenElement || document.mozFullScreenElement || document.webkitFullscreenElement;

        if (!isFullscreen)
        {
            var target = document.documentElement;

            if (target.requestFullscreen)
            {
                target.requestFullscreen();
            }
            else if (target.mozRequestFullScreen)
            {
                target.mozRequestFullScreen();
            }
            else if (target.webkitRequestFullscreen)
            {
                target.webkitRequestFullscreen();
            }
            else if (target.msRequestFullscreen)
            {
                target.msRequestFullscreen();
            }
            else
            {
                alert("Fullscreen not supported by your browser :_:");
            }
        }
        else
        {
            if (document.exitFullscreen)
            {
                document.exitFullscreen();
            }
            else if (document.mozCancelFullScreen)
            {
                document.mozCancelFullScreen();
            }
            else if (document.webkitExitFullscreen)
            {
                document.webkitExitFullscreen();
            }
            else
            {
                alert("Fullscreen exit not supported by your browser :_:");
            }
        }
	},

	hook_inventorySlotCreate: function(inventoryItem)
	{
		// Build text
		var text = "test";

		// Create new slot
	    $(this.elementUIInventory).append(
	        "<div id='ps-ui-slot-" + inventoryItem.slotId + "' class='slot item_" + inventoryItem.typeId + "'>" + text + "</div>"
        );
	},

	hook_inventorySlotUpdate: function(inventoryItem)
	{
	    $("#ps-ui-slot-" + inventoryItem.slotId).text("test");
	},

	hook_inventorySlotRemove: function(inventoryItem)
	{
	    $("#ps-ui-slot-" + inventoryItem.slotId).remove();
	},

	hook_inventorySlotSelected: function(inventoryItem)
	{
	    $(this.elementUIInventory).find('*').removeClass("selected");

	    if (inventoryItem != null)
	    {
	    	$("#ps-ui-slot-" + inventoryItem.slotId).addClass("selected");
	    }
	},

	hook_inventoryReset: function()
	{
	    $(this.elementUIInventory).empty();
	}

}
