var projectSandbox =
{
    // Constants
    // -- The rate at which logic is executed in ms
    RATE_LOGIC_MS: 60,

    // Session ID
    sessionId: null,

    // The base URL for all requests
    baseUrl: "",
    
    // Entities
    entities: new Map(),
    
    // Effects
    effects: new Array(),

    // Current map
    map: null,

    // Identifier of current player; set by: network / player
    playerId: null,
    
    // Identifier of current ent
    playerEntityId: null,

    // The player's current entity
    playerEntity: null,
    
    // The current time, updated every render loop execution
    currentTime: (new Date).getTime(),

    // The current game
    game: { },
    
    reset: function()
    {
        // Rendering
        this.rendering.depthTree.reset();

        // Lights
        this.lights.reset();

        // Reset UI
        this.game.ui.controller.reset();

        // Reset inventory
        projectSandbox.inventory.reset();
        
        // Reset player
        this.playerEntityId = null;
        
        // Wipe entities
        this.entities.clear();
    },
    
    init: function()
    {
        console.info("projectsandbox - starting up...");
        
        // Validate session ID
        if (this.sessionId == null || this.sessionId.length != 36)
        {
            console.error("projectsandbox - invalid session identifier - " + this.sessionId);
            return;
        }
        else
        {
            console.info("projectsandbox - session ID: " + this.sessionId);
        }

        // Fetch canvas instance
        var canvas = document.getElementById("ps_render");

        // Initialize rendering
        if (!this.rendering.core.init(canvas))
        {
            alert("Your browser does not support WebGL ;_:...");
            return;
        }

        // Setup text rendering
        this.text.setup();
        
        // Initialize request animation frame function
        // -- Needs improving - what if null/fails?
        this.initRequestAnimationFrame();
        
        // Load assets
        this.assetLoader.loadFromAssetsFile("/content/game/settings.json");
        this.assetLoader.loadFromAssetsFile("/content/game/shaders/list.json");
        this.assetLoader.loadFromAssetsFile("/content/game/textures/list.json");

        console.debug("finished loading assets");
        
        // Setup game
        this.game.effects = game.effects;
        this.game = game;
    },
    
    postResources: function()
    {
        console.log("project-sandbox - starting game, post resources...");
        
        // Setup comms
        projectSandbox.network.setup();

        // Hook interaction
        projectSandbox.interaction.shared.hook();

        // Perform post-resources init of rendering
        this.rendering.core.postResourcesInit();

        // Start the game
        this.gameStart();
        
        console.log("project-sandbox - load complete");
    },
    
    gameStart: function()
    {
        var self = this;
        
        // Setup UI
        this.game.ui.controller.setup();
        
        // Setup logic loop
        window.setInterval(
            function()
            {
                self.gameLogic();
            },
            this.RATE_LOGIC_MS
        );
        
        // Start render cycle
        this.gameRenderLoop(this);
    },
    
    gameLogic: function()
    {
        // Update player's entity
        this.playerEntity = this.entities.get(this.playerEntityId);

        // Update time
        this.currentTime = (new Date).getTime();
        
        // Update camera
        this.camera.logic();
        
        // Update movement of player to server
        this.network.player.sendUpdateMovementPacket();

        // Update inventory
        this.inventory.logic();
        
        // Update textures
        this.textures.logic();
        
        // Update ents
        this.entities.forEach(function (entity) {
            // Core logic
            entity.coreLogic();

            // Custom ent logic
            if (entity.logic)
            {
                entity.logic();
            }
        });
        
        // Update effects
        var effect;
        for (var i = this.effects.length - 1; i >= 0; i--)
        {
            effect = this.effects[i];

            if (effect != null)
            {
                effect.logic();

                if (effect.isExpired())
                {
                    this.effects.splice(i, 1);
                    projectSandbox.rendering.depthTree.remove(effect);
                }
            }
        }
        
        // Update UI
        this.game.ui.controller.logic();
    },
    
    gameRenderLoop: function(self)
    {
        // Render game
        self.gameRender();
        
        // Setup next invocation
        window.requestAnimationFrame(
            function()
            {
                self.gameRenderLoop(self);
            }
        );
    },
    
    gameRender: function()
    {
        this.rendering.core.render();
    },
    
    initRequestAnimationFrame: function()
    {
        window.requestAnimationFrame =
            window.requestAnimationFrame           || 
            window.webkitRequestAnimationFrame     || 
            window.mozRequestAnimationFrame        || 
            window.oRequestAnimationFrame          || 
            window.msRequestAnimationFrame        ||
            function(callback)
            {
                window.setTimeout(callback, 1000/60);
            };
            
        if(!window.requestAnimationFrame)
        {
            console.log("Failed to setup request animation frame.");
        }
    },

    addEffect: function(effect)
    {
        // Add to internal array
        this.effects.push(effect);

        // Add to depth tree
        projectSandbox.rendering.depthTree.update(effect);
    }

}
