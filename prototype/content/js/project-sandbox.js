var projectSandbox =
{
	// Constants
	// -- The rate at which logic is executed in ms
	RATE_LOGIC_MS: 60,
	// -- Pixel scaling factor for co-ords
	SCALE_FACTOR: null,
	
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
	
	reset: function()
	{
		// Reset map
		projectSandbox.map.reset();
		
		// Reset textures
		this.textures.reset();
		
		// Reset player
		this.playerEntityId = null;
		
		// Wipe entities
		this.entities.clear();
	},
	
	preInit: function()
	{
		console.log("Pre-initializing project sandbox...");
		
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
		
		// Load textures
		this.textures.load("/content/game/textures/list.json");
		
		// Begin init...
		//this.init();
	},
	
	init: function()
	{
		console.log("Initializing project sandbox...");
		
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
		// Reset scene
		this.gl.viewport(0, 0, this.gl.viewportWidth, this.gl.viewportHeight);
		this.gl.clear(this.gl.COLOR_BUFFER_BIT | this.gl.DEPTH_BUFFER_BIT);
		
		// Reset perspective matrix
        mat4.perspective(this.perspective, 45, this.gl.viewportWidth / this.gl.viewportHeight, 1, 1000.0);
		
		// Reset identity matrix
		mat4.identity(this.modelView);
		
		// Perform camera render logic
		projectSandbox.camera.renderLogic();
		
		// Set camera translation
		projectSandbox.camera.applyToModelView();
		
		// Render all the objects
		var ent;
		for(var kv of this.entities)
		{
			ent = kv[1];
			ent.render(this.gl, this.shaderProgram, this.modelView, this.perspective);
		}
		
		// Render map
		projectSandbox.map.render(this.gl, this.shaderProgram, this.modelView, this.perspective);
		
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
	},
	
	initWebGl: function()
	{
		var gl = null;

		try
		{
			gl = this.canvas.getContext("webgl") || this.canvas.getContext("experimental-webgl");
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
		this.gl.clearColor(0.0, 0.0, 0.0, 1.0);
        this.gl.enable(this.gl.DEPTH_TEST);
	}
}
