game.ui =
{
    // Elements - main
    elementRender: null,
    elementDeathScreen: null,

    // Elements - UI
    elementUI: null,
	elementUIHealthBar: null,
	elementUIInventory: null,
	elementUIScore: null,

	// Elements - side
	elementSidebarActivity: null,
	elementSidebarChat: null,
	elementSidebarScoreboard: null,

	// Elements - options
	elementOptionsFpsCounter: null,


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
        this.elementUIScore = document.getElementById("ps-ui-score");

        // -- Sidebar
        this.elementSidebarActivity = document.getElementById("ps-activity");
        this.elementSidebarChat = document.getElementById("ps-chat");
        this.elementSidebarScoreboard = document.getElementById("ps-scoreboard");

        // -- Options
        this.elementOptionsFpsCounter = document.getElementById("ps-options-fps");

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

        // Reset UI
        this.reset();
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
	},
	
	setPrimitivePosTopLeft: function(primitive, x, y)
	{
		primitive.x = this.uiWidth - x - (primitive.width / 2.0);
		primitive.y = this.uiHeight - y - (primitive.height / 2.0);
	},

	reset: function()
	{
		// Clear activity
		$(this.elementSidebarActivity).children().remove();

		// Clear scoreboard
		$(this.elementSidebarScoreboard).find("ol").children().remove();

		// Clear chat
		$(this.elementSidebarChat).children().remove();
	},
	
	logic: function()
	{
	    // TODO: move this to be entirely event driven, so no logic loop is needed for UI except fps

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

			    // Update player UI to render
			    this.renderPlayerUI = true;
			}
		}
		else if (!projectSandbox.network.closed)
        {
            console.warn("UI - unable to find player entity, cannot update UI");
        }

        // Update FPS counter
        $(this.elementOptionsFpsCounter).text(projectSandbox.fps);
	},
	
	render: function(gl, shaderProgram, modelView, perspective)
	{
	    // Not used at present, replaced by HTML...

	    // TODO: remove this function

	    /*
	    // Disable depth for transparency
	    gl.disable(gl.DEPTH_TEST);

		// Switch into orthographic mode
		mat4.ortho(perspective, 0, this.uiWidth, 0, this.uiHeight, 0, 1);
		mat4.identity(modelView);

        // Do rendering here...

        // Re-enable depth testing
        gl.enable(gl.DEPTH_TEST);
        */
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

	/*
        Functions for creating UI elements
        ----------------------------------------------------------------------------------------------------------------
    */

    activityAdd: function(text, icon1, icon2)
    {
        // Construct HTML
        var html = "<p>";

        if (icon1 != null)
        {
            html += "<span class='left'><img src='" + icon1 + "' /></span>";
        }

        html += "<span class='info'>" + text + "</span>";

        if (icon2 != null)
        {
            html += "<span class='left'><img src='" + icon2 + "' /></span>";
        }

        html += "</p>";

        // Add HTML as element
        $(this.elementSidebarActivity).prepend(html);
    },

    scoreboardAdd: function(player)
    {
        // Build HTML
        var html = "<li id='scoreboard_item_" + player.playerId + "'><span>" + player.score + "</span>" + player.displayName + "</li>";

        // Add element
        $(this.elementSidebarScoreboard).find("ol").append(html);

        // Sort
        this.scoreboardSort();
    },

    scoreboardUpdate: function(player)
    {
        // Find scoreboard item
        var item = $("#scoreboard_item_" + player.playerId);

        // Update UI score if the player is us
        if (player.playerId == projectSandbox.playerId)
        {
            this.hook_updateLocalPlayerScore();
        }

        // Update score in scoreboard
        $(item).find("span").text(player.score);

        // Update K/D
        $(item).attr("title", "kills: " + player.kills + ", deaths: " + player.deaths);

        // Sort
        this.scoreboardSort();
    },

    scoreboardRemove: function(player)
    {
        // Remove the element
        $("#scoreboard_item_" + player.playerId).remove();
    },

    scoreboardSort: function()
    {
        var parent = $(this.elementSidebarScoreboard).find("ol");
        var scoreboardItems = $(parent).children();

        // Sort indexes
        scoreboardItems.sort(
            function(a, b)
            {
                var scoreA = $(a).find("span");
                var scoreB = $(b).find("span");
                var displayNameA = $(a).text();
                var displayNameB = $(b).text();

                if (scoreA > scoreB)
                {
                    return 1;
                }
                else if (scoreA < scoreB)
                {
                    return -1;
                }
                else if (displayNameA > displayNameB)
                {
                    return 1;
                }
                else if (displayNameA < displayNameB)
                {
                    return -1;
                }

                return 0;
            }
        );

        // Reattach elements to show changes
        $(scoreboardItems).detach().appendTo(parent);
    },

	/*
        Entity Hooks
        ----------------------------------------------------------------------------------------------------------------
    */

	hookPlayer_entChanged: function()
    {
        // Reset inventory
        this.hook_inventoryReset();

        // Update score
        this.hook_updateLocalPlayerScore();
    },

    hookPlayer_entKilled: function(causeText, entityIdVictim, entityIdKiller, playerIdVictim, playerIdKiller)
    {
        // Create activity
        this.activityAdd(causeText);

        // Check if we were killed
        if (playerIdVictim == projectSandbox.playerId)
        {
            // Show death screen
            this.deathScreenShow(causeText);
        }
    },

    /*
	    Inventory Hooks
	    ----------------------------------------------------------------------------------------------------------------
	*/

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
	    $("#ps-ui-slot-" + inventoryItem.slotId).text(inventoryItem.text);
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
	},

	/*
        Player Event Hooks
        ----------------------------------------------------------------------------------------------------------------
    */

    hook_playerJoined: function(player)
    {
        this.activityAdd(player.displayName + " has joined");
        this.scoreboardAdd(player);
    },

    hook_playerUpdated: function(player)
    {
        this.scoreboardUpdate(player);
    },

    hook_playerLeft: function(player)
    {
        this.activityAdd(player.displayName + " has left");
        this.scoreboardRemove(player);
    },

    /*
        Score Hooks
        ----------------------------------------------------------------------------------------------------------------
    */

    hook_updateLocalPlayerScore: function()
    {
        // Fetch player object
        var player = projectSandbox.players.get(projectSandbox.playerId);

        if (player != null)
        {
            var scoreText = "$" + projectSandbox.utils.formatNumberCommas(player.score);

            $(this.elementUIScore).text(scoreText);
        }
        else
        {
            console.error("game/ui - failed to update local player score, cannot find player");
        }
    }

}
