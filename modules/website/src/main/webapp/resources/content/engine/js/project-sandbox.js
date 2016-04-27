var projectSandbox =
{
    // Constants
    // -- The rate at which logic is executed in ms
    RATE_LOGIC_MS: 60,

    // Used to toggle transparency; this will disable the depth buffer / 3D
    TRANSPARENCY_ENABLED: true,

    // Session ID
    sessionId: null,
    
    // Rendering
    canvas: null,
    gl: null,
    shaderProgram: null,
    
    // Matrices
    modelView: mat4.create(),
    perspective: mat4.create(),
    cameraView: mat4.create(),
    
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
    
    // The current time, updated every render loop execution
    currentTime: (new Date).getTime(),
    
    // FPS calculation
    fps: 0,
    fpsTime: (new Date).getTime(),
    fpsFrames : 0,
    
    // The current game
    game: { },
    
    reset: function()
    {
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
        console.log("Initializing project sandbox...");
        
        // Validate session ID
        if (this.sessionId == null || this.sessionId.length != 36)
        {
            console.error("Invalid session identifier - " + this.sessionId);
            return;
        }
        else
        {
            console.info("Session ID: " + this.sessionId);
        }

        // Fetch canvas instance
        this.canvas = document.getElementById("ps_render");
        
        // Initialise WebGL
        this.gl = this.initWebGl();
        
        if (!this.gl)
        {
            alert("Browser does not support WebGL.");
            return;
        }

        // Setup text rendering
        this.text.setup();
        
        // Initialize request animation frame function
        // -- Needs improving - what if null/fails?
        this.initRequestAnimationFrame();
        
        // Setup buffer cache
        this.bufferCache.setup();

        // Load assets
        this.assetLoader.loadFromAssetsFile("/content/game/settings.json");
        this.assetLoader.loadFromAssetsFile("/content/game/shaders/list.json");
        this.assetLoader.loadFromAssetsFile("/content/game/textures/list.json");
        
        // Setup game
        this.game.effects = game.effects;
        this.game = game;
    },
    
    postResources: function()
    {
        console.log("engine/project-sandbox - starting project sandbox...");
        
        // Setup comms
        projectSandbox.network.setup();

        // Hook interaction
        projectSandbox.interaction.shared.hook();
        
        // Setup scene
        this.sceneSetup();
        
        // Setup shader program
        this.shaderProgram = this.shaders.createDefaultTextureProgram(this.gl);
        
        // Setup texture manager
        this.textures.setup();

        // Setup lights
        this.lights.init();
        
        // Start the game
        this.gameStart();
        
        console.log("engine/project-sandbox - load complete");
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
        var ent;
        for (var kv of this.entities)
        {
            ent = kv[1];

            // Core logic
            ent.coreLogic();

            // Custom ent logic
            if (ent.logic)
            {
                ent.logic();
            }
        }
        
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
        var gl = this.gl;
    
        // Reset scene
        // -- If width/height changes, update frustrum culling since it uses it for calculating aspect ratio
        gl.viewport(0, 0, projectSandbox.camera.getRenderWidth(), projectSandbox.camera.getRenderHeight());
        gl.clear(gl.COLOR_BUFFER_BIT | gl.DEPTH_BUFFER_BIT);

        // Reset perspective matrix
        // -- fovy (vertical field of view)
        // -- aspect ratio
        // -- near bound of frustrum
        // -- far bound of frustrum
        var aspectRatio = projectSandbox.camera.getRenderRatio();

        mat4.perspective(
            this.perspective,

            projectSandbox.frustrum.FRUSTRUM_VERTICAL_FOV,
            aspectRatio,
            projectSandbox.frustrum.FRUSTRUM_DISTANCE_NEAR,
            projectSandbox.frustrum.FRUSTRUM_DISTANCE_FAR
        );
        
        // Reset matrices
        mat4.identity(this.modelView);
        mat4.identity(this.cameraView);

        // Perform camera render logic
        projectSandbox.camera.renderLogic();

        // Update camera view matrix
        gl.uniformMatrix4fv(this.shaderProgram.uniformCameraViewMatrix, false, this.cameraView);
        
        // Render map
        if (this.map != null && this.map.isSetup())
        {
            this.map.render(gl, this.shaderProgram, this.modelView, this.perspective);
        }

        // Render effects
        var effect;
        for (var i = this.effects.length - 1; i >= 0; i--)
        {
            effect = this.effects[i];
            if (projectSandbox.frustrum.intersects(effect))
            {
                effect.render(gl, this.shaderProgram, this.modelView, this.perspective);
            }
        }
        
        // Render ents
        var ent;
        for (var kv of this.entities)
        {
            ent = kv[1];
            if (!ent.dead && projectSandbox.frustrum.intersects(ent))
            {
                // Render entity
                ent.render(gl, this.shaderProgram, this.modelView, this.perspective);
            }
        }
        
        // Update FPS
        var currentTime = (new Date).getTime();
        if(currentTime - this.fpsTime >= 1000)
        {
            // Update counters
            this.fps = this.fpsFrames;
            this.fpsTime = currentTime;
            this.fpsFrames = 1;
        }
        else
        {
            // Update total frames for current second
            this.fpsFrames++;
        }
        
        // Render UI
        this.game.ui.controller.render(gl, this.shaderProgram, this.modelView, this.perspective);
    },
    
    initWebGl: function()
    {
        var gl = null;

        try
        {
            // Setup WebGL
            gl = this.canvas.getContext("webgl", {alpha: true}) || this.canvas.getContext("experimental-webgl", {alpha: true});

            if (gl == null)
            {
                console.error("engine/project-sandbox - failed to setup WebGL context (critical)");
            }

            // Set default size to use canvas
            gl.viewportWidth = this.canvas.width;
            gl.viewportHeight = this.canvas.height;
        }
        catch(e)
        {
            console.log("Failed to setup WebGL: " + e);
        }
        
        return gl;
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
    
    sceneSetup: function()
    {
        var gl = this.gl;
        
        gl.clearColor(0.0, 0.0, 0.0, 1.0);

        if (this.TRANSPARENCY_ENABLED)
        {
            gl.disable(gl.DEPTH_TEST);
        }
        else
        {
            gl.enable(gl.DEPTH_TEST);
        }
        
        // Due to depth, we use alpha test rather than blending, implemented in shader
        gl.enable(gl.BLEND);
        gl.blendFunc(gl.SRC_ALPHA, gl.ONE_MINUS_SRC_ALPHA);

        // Setup camera
        projectSandbox.camera.setup();
    },

    getPlayerEntity: function()
    {
        var result = null;

        if (this.playerEntityId != null && this.playerEntityId != 0)
        {
            result = this.entities.get(this.playerEntityId);
        }

        return result;
    }

}
