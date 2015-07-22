var projectSandbox =
{
	// Constants
	// -- The rate at which logic is executed in ms
	RATE_LOGIC_MS: 60,
	// -- Pixel scaling factor for co-ords
	// TODO: remove this...
	SCALE_FACTOR: null,
	
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
		// Reset UI
		if (this.game.ui != null)
		{
			this.game.ui.reset();
		}
		
		// Reset map
		projectSandbox.map.reset();

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
		
		// Setup scaling in respect to canvas
		// TODO: remove this scale rubbish...probably not needed now.
		this.SCALE_FACTOR = 1.0;//0.05;
		
		// Initialise WebGL
		this.gl = this.initWebGl();
		
		if(!this.gl)
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
		this.game.ui = game.ui;
	},
	
	postResources: function()
	{
		console.log("engine/project-sandbox - starting project sandbox...");
        
		// Setup comms
		projectSandbox.network.setup();
		
		// Hook keyboard
		projectSandbox.keyboard.hook();
		
		// Hook mouse
		projectSandbox.mouse.hook();
		
		// Setup scene
		this.sceneSetup();
		
		// Setup shader program
		this.shaderProgram = this.shaders.createDefaultTextureProgram(this.gl);
		
		// Setup texture manager
		this.textures.setup();
		
		// Start the game
		this.gameStart();
		
		console.log("engine/project-sandbox - load complete");
	},
	
	gameStart: function()
	{
		var self = this;
		
		// Setup UI
		this.game.ui.setup();
		
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
		this.game.ui.logic();
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
		gl.viewport(0, 0, gl.viewportWidth, gl.viewportHeight);
		gl.clear(gl.COLOR_BUFFER_BIT | gl.DEPTH_BUFFER_BIT);
		
		// Reset perspective matrix
		// -- fovy (vertical field of view)
		// -- aspect ratio
		// -- near bound of frustrum
		// -- far bound of frustrum
        mat4.perspective(
            this.perspective,

            projectSandbox.frustrum.FRUSTRUM_VERTICAL_FOV,
            gl.viewportWidth / gl.viewportHeight,
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
		projectSandbox.map.render(gl, this.shaderProgram, this.modelView, this.perspective);

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
		for(var kv of this.entities)
		{
			ent = kv[1];
			if (!ent.dead && projectSandbox.frustrum.intersects(ent))
			{
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
		if (this.game.ui != null)
		{
			this.game.ui.render(gl, this.shaderProgram, this.modelView, this.perspective);
		}
	},
	
	initWebGl: function()
	{
		var gl = null;

		try
		{
            // Setup WebGL
			gl = this.canvas.getContext("webgl", {alpha: false}) || this.canvas.getContext("experimental-webgl", {alpha: false});

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
			window.requestAnimationFrame       	|| 
            window.webkitRequestAnimationFrame 	|| 
            window.mozRequestAnimationFrame    	|| 
            window.oRequestAnimationFrame      	|| 
            window.msRequestAnimationFrame		||
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
        gl.enable(gl.DEPTH_TEST);
        
        // Due to depth, we use alpha test rather than blending, implemented in shader
        gl.enable(gl.BLEND);
        gl.blendFunc(gl.SRC_ALPHA, gl.ONE_MINUS_SRC_ALPHA);

        // Setup camera
        projectSandbox.camera.setup();
	}
}
