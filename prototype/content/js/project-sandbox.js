var projectSandbox =
{
	// Constants
	// -- The rate at which logic is executed in ms
	RATE_LOGIC_MS: 60,
	// -- Pixel scaling factor for co-ords
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
	
	// Entities
	entities: new Map(),
	
	// Identifier of current ent
	playerEntityId: null,
	
	// The current time, updated every render loop execution
	currentTime: (new Date).getTime(),
	
	// FPS calculation
	htmlFpsCounter: null,
	fps: 0,
	fpsTime: (new Date).getTime(),
	fpsFrames : 0,
	
	// The current UI
	ui: null,
	
	reset: function()
	{
		// Reset UI
		if (this.ui != null)
		{
			this.ui.reset();
		}
		
		// Reset map
		projectSandbox.map.reset();
		
		// Reset player
		this.playerEntityId = null;
		
		// Wipe entities
		this.entities.clear();
	},
	
	init: function()
	{
		console.log("Initializing project sandbox...");
		
		// Retrieve session ID
		// TODO: actually retrieve session ID
		this.sessionId = "6b1091d6-11fa-4ca1-b8cc-f38d1f266011";
		
		// Fetch canvas instance
		this.canvas = document.getElementById("ps_render");
		
		// Setup scaling in respect to canvas
		// TODO: remove this scale rubbish...probably not needed now.
		this.SCALE_FACTOR = 1.0;//0.05;
		
		// Fetch FPS counter
		this.htmlFpsCounter = document.getElementById("fps");
		
		// Initialise WebGL
		this.gl = this.initWebGl();
		
		if(!this.gl)
		{
			alert("Browser does not support WebGL.");
			return;
		}
		
		// Initialize request animation frame function
		// -- Needs improving - what if null/fails?
		this.initRequestAnimationFrame();
		
		// Load textures - this will call postResources when finished
		//this.textures.load("/content/game/textures/list.json");
		
		// Load assets
		this.assetLoader.loadFromAssetsFile("/content/game/shaders/list.json");
		this.assetLoader.loadFromAssetsFile("/content/game/textures/list.json");
		
		// Setup game
		this.ui = game.ui;
	},
	
	postResources: function()
	{
		console.log("Starting project sandbox...");
        
		// Setup comms
		projectSandbox.comms.setup();
		
		// Hook keyboard
		projectSandbox.keyboard.hook();
		
		// Hook mouse
		projectSandbox.mouse.hook();
		
		// Setup scene
		this.sceneSetup();
		
		// Setup shader program
		this.shaderProgram = this.shaders.createDefaultTextureProgram(this.gl);
		
		// Start the game
		this.gameStart();
		
		console.log("Done.");
	},
	
	gameStart: function()
	{
		var self = this;
		
		// Setup UI
		this.ui.setup();
		
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
		this.commsPacket.updateMovement();
		
		// Update textures
		this.textures.logic();
        
        // Update ents
        var ent;
		for(var kv of this.entities)
		{
			ent = kv[1];
			if (ent.logic)
            {
                ent.logic();
            }
		}
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
        mat4.perspective(this.perspective, 45, gl.viewportWidth / gl.viewportHeight, 1, 1000.0);
		
		// Reset identity matrix
		mat4.identity(this.modelView);
		
		// Perform camera render logic
		projectSandbox.camera.renderLogic();
		
		// Set camera translation
		projectSandbox.camera.applyToModelView();
        
        // Render map
		projectSandbox.map.render(gl, this.shaderProgram, this.modelView, this.perspective);
		
		// Render all the objects
        // If we use alpha blend ever, disable depth-test for ents on the same z as hack - also works, as
        // opposed to alpha testing
        gl.disable(gl.DEPTH_TEST);
		var ent;
		for(var kv of this.entities)
		{
			ent = kv[1];
			ent.render(gl, this.shaderProgram, this.modelView, this.perspective);
		}
        gl.enable(gl.DEPTH_TEST);
		
		// Update FPS
		var currentTime = (new Date).getTime();
		if(currentTime - this.fpsTime >= 1000)
		{
			// Update counters
			this.fps = this.fpsFrames;
			this.fpsTime = currentTime;
			this.fpsFrames = 1;
			
			// Update counter on page
			this.htmlFpsCounter.innerText = this.fps;
		}
		else
		{
			// Update total frames for current second
			this.fpsFrames++;
		}
		
		// Render UI
		if (this.ui != null)
		{
			this.ui.render(gl, this.shaderProgram, this.modelView, this.perspective);
		}
	},
	
	initWebGl: function()
	{
		var gl = null;

		try
		{
            // Setup WebGL
			gl = this.canvas.getContext("webgl", {alpha: false}) || this.canvas.getContext("experimental-webgl", {alpha: false});
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
	}
}
