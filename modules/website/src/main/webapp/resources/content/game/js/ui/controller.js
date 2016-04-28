game.ui.controller =
{
    // Render container
    renderContainer: null,

    // Elements - main
    elementRender: null,
    elementDeathScreen: null,
    elementConnecting: null,

    // Elements - UI
    elementUI: null,
    elementUIHealthBar: null,
    elementUIInventory: null,
    elementUIScore: null,

    // Elements - side
    elementSidebarActivity: null,
    elementSidebarChatBox: null,
    elementSidebarChatMessages: null,
    elementSidebarScoreboard: null,

    // Elements - options
    elementFpsCounter: null,


    setup: function()
    {
        // Fetch elements
        // -- Render container
        this.renderContainer = document.getElementById("ps-render-container");

        // -- Main
        this.elementRender = document.getElementById("ps_render");
        this.elementDeathScreen = document.getElementById("ps-death-screen");
        this.elementConnecting = document.getElementById("ps-connecting");
        this.elementError = document.getElementById("ps-error");

        // -- UI
        this.elementUI = document.getElementById("ps-ui");
        this.elementUIHealthBar = document.getElementById("ps-healthbar-fill");
        this.elementUIInventory = document.getElementById("ps-inventory");
        this.elementUIScore = document.getElementById("ps-score");

        // -- Sidebar
        this.elementSidebarActivity = document.getElementById("ps-activity-items");
        this.elementSidebarChatBox = document.getElementById("ps-chat-box-field");
        this.elementSidebarChatMessages = document.getElementById("ps-chat-messages");
        this.elementSidebarScoreboard = document.getElementById("ps-scoreboard");

        // -- Options
        this.elementFpsCounter = document.getElementById("ps-fps-value");

        // Bind resize event for window
        $(window).resize(function () {
            game.ui.controller.resize();
        });

        // Bind death screen to close
        $(this.elementDeathScreen).keyup(function (event) {
            if (String.fromCharCode(event.which) == " ")
            {
                game.ui.controller.deathScreenHide();
            }
        });

        // Bind chatbox key-down
        $(this.elementSidebarChatBox).bind("keyup", this.chatboxKeyUp);

        // Bind render area key-up
        $(this.renderContainer).bind("keyup", this.renderAreaKeyUp);

        // Bind options / fullscreen
        $("#button-fullscreen").click(function () {
            game.ui.controller.toggleFullScreen();
        });

        // Set UI size
        this.resize();

        // Reset UI
        this.reset();
    },

    renderAreaKeyUp: function(event)
    {
        if (projectSandbox.interaction.shared.isRenderArea(event))
        {
            var keyCode = String.fromCharCode(event.keyCode);

            if (keyCode == "Y")
            {
                // Put focus on chatbox input
                $(game.ui.controller.elementSidebarChatBox).focus();
            }
        }
    },

    chatboxKeyUp: function(event)
    {
        var keyCode = event.keyCode;

        // Check if keycode is enter key (13); if so, we'll send message...
        if (keyCode == 13)
        {
            // Fetch message
            var message = $(game.ui.controller.elementSidebarChatBox).val();

            // Check we can send it...
            if (message != null && message.length > 0)
            {
                // Reset field
                $(game.ui.controller.elementSidebarChatBox).val("");

                // Send message
                projectSandbox.network.player.sendChatMessage(message);
            }

            // Put focus back on render area
            projectSandbox.interaction.shared.focusRenderArea();
        }
    },

    resize: function()
    {
        var gl = projectSandbox.gl;

        // Set canvas to fill page
        var width = $("body").width();
        var height = $("body").height();

        // By setting the actual width/height attributes, the canvas renders at a much higher quality,
        // rather than scaling between the initial width/height and CSS styles
        $(this.elementRender).attr("width", width);
        $(this.elementRender).attr("height", height);
    },

    updateRenderOverlayElement: function(element, width, height)
    {
        // Update size
        if (width != null && height != null)
        {
            $(element).width(width).height(height);
        }

        // Update offset
        $(element).offset(
            $(this.elementRender).position()
        );
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
        $(this.elementSidebarChatMessages).children().remove();

        // Reset map
        game.ui.map.reset();
    },
    
    logic: function()
    {
        // TODO: move this to be entirely event driven, so no logic loop is needed for UI except fps

        var ent = projectSandbox.playerEntity;
        
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
        $(this.elementFpsCounter).text(projectSandbox.fps);

        // Update map
        game.ui.map.logic();
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
        $(this.elementDeathScreen).show();
        $(this.elementDeathScreen).addClass("visible");

        // Set focus to control
        $(this.elementDeathScreen).focus();

        // Apply offset
        this.updateRenderOverlayElement(this.elementDeathScreen);
    },

    deathScreenHide: function()
    {
        $(this.elementDeathScreen).hide();
        $(this.elementDeathScreen).removeClass("visible");

        // Focus on canvas...
        $(this.renderContainer).focus();
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
        // Check if current player, so that we can set special class
        var specialClasses = "";

        if (player.playerId == projectSandbox.playerId)
        {
            specialClasses = "current-player";
        }

        // Build HTML
        var html = "<li id='scoreboard_item_" + player.playerId + "' class=\"" + specialClasses + "\"><span>" + player.score + "</span>" + player.displayName + "</li>";

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
                var scoreA = parseInt( $(a).find("span").text() );
                var scoreB = parseInt( $(b).find("span").text() );
                var displayNameA = $(a).text();
                var displayNameB = $(b).text();

                if (scoreA > scoreB)
                {
                    return -1;
                }
                else if (scoreA < scoreB)
                {
                    return 1;
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

    chatMessageAdd: function(player, nickname, message)
    {
        // Determine name to use
        var playerName;

        if (player != null)
        {
            playerName = player.name;
        }
        else if (playerName == null)
        {
            playerName = nickname;
        }

        // Build chat message HTML
        var html = "<p>";

        html += "<span class='player'><img src='thumbnail' />" + playerName + "</span><span class='text'>" + message + "</span>";

        html += "</p>";

        // Prepend to chat
        $(this.elementSidebarChatMessages).prepend(html);
    },


    /*
        Socket hooks
        ----------------------------------------------------------------------------------------------------------------
    */

    hookSocket_connected: function()
    {
        $(this.elementConnecting).hide();
    },

    hookSocket_disconnected: function()
    {
        // Ensure inventory is not visible
        this.hook_inventoryReset();

        // Show connecting screen
        $(this.elementConnecting).show();
    },

    /*
        Session hooks
        ----------------------------------------------------------------------------------------------------------------
    */

    hookSession_errorCode: function(errorCode)
    {
        // Set more appropriate error message in UI, if error code is recognized
        switch (errorCode)
        {
            case 1:
                $(this.elementConnecting).find("div").html("Session not found, try rejoining.<br />Redirecting to home in 5s...");
                break;
        }

        // Ensure inventory is not visible
        this.hook_inventoryReset();

        // Show error message
        $(this.elementConnecting).show();

        // Hook redirect for 5s
        setTimeout(function() {
            console.info("Redirecting back to home...");
            window.location = "../home";
        }, 5000);
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
            // Reset inventory
            projectSandbox.inventory.reset();

            // Invoke UI inventory reset
            this.hook_inventoryReset();

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
    },

    /*
        Chat Hooks
        ----------------------------------------------------------------------------------------------------------------
    */

    hook_playerChatMessage: function(player, nickname, message)
    {
        this.chatMessageAdd(player, nickname, message);
    }

}
