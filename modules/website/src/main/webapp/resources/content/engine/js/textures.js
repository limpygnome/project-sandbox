projectSandbox.textures =
{
	textures: new Map(),
	src: new Map(),
	emptyTexture: null,
	
	flagColourDisabled: false,
	
	setup: function(gl)
	{
		var gl = projectSandbox.gl;
		
		// Fetch empty texture
		this.emptyTexture = this.textures.get("white");
	},
	
	get: function(name)
	{
		return this.textures.get(name);
	},
	
	getSrc: function(name)
	{
		return this.src.get(name);
	},
	
	logic: function()
	{
		// Execute logic for each texture
		var texture;
		for(var kv of this.textures)
		{
			texture = kv[1];
			texture.logic();
		}
	},
	
	reset: function()
	{
		this.textures.clear();
		this.src.clear();
	},
	
	loadTextureJson: function(json)
	{
		var self = projectSandbox.textures;
	
		// Read src values
		var name = json["name"];
		var url = json["url"]
		var width = json["width"];
		var height = json["height"];
		
		// Create src entry
		var src = new TextureSrc(name, url, width, height);
		self.src.set(name, src);
		console.log("Textures - added texture src '" + name + "'");
		
		// Parse textures
		var textures = json["textures"];
		var textureData;
		var texture;
		
		for (var i = 0; i < textures.length; i++)
		{
			// Fetch JSON element
			textureData = textures[i];
			
			// Create texture
			texture = self.buildTexture(src, textureData);
			
			// Add to collection
			self.textures.set(texture.name, texture);
			console.log("Textures - added texture '" + texture.name + "' - " + texture.frames + " frames, " + texture.frameVertices + " vertices");
		}
	},
    
    isLoaded : function()
    {
        var self = projectSandbox.textures;
        
        // Check for the first false
        for (var i = 0; i < self.srcLoaded.length; i++)
        {
            if (self.srcLoaded[i] == false)
            {
                return false;
            }
        }
        
        return true;
    },
	
	buildTexture : function(src, textureData)
	{
		// Load properties from texture
		var name = textureData["name"];
		var speed = textureData["speed"];
		var frames = textureData["frames"];
		
		// Build framedata - convert each frame into array of vertices
		// -- Check we have at least one frame
		if (frames.length == 0)
		{
			console.error("Textures - cannot build texture - no frame data available - name: " + name);
			return;
		}
		
		// -- Check number of vertices per frame from first item
		// -- -- Note: this is actually not verts, but total co-ordinates - divide by 2 for total verts
		var expectedVertsPerFrame = frames[0].length;
		if (expectedVertsPerFrame == 0)
		{
			console.error("Textures - cannot build texture - no vertex data - name: " + name);
			return;
		}
		else if (expectedVertsPerFrame % 8 != 0)
		{
			console.error("Textures - cannot build texture - must be multiples of 8 vertices - name: " + name);
			return;
		}
		
		// -- frames * 8 floats (4 verts)
		var frameData = new Float32Array(frames.length * expectedVertsPerFrame);
		var frameDataOffset = 0;
		var frame;
		var frameVertices;
		var vert;
        
		for (var f = 0; f < frames.length; f++)
		{
			frame = frames[f];
			frameVertices = frame.length;
			
			// Check frame has correct number of vertices
			if (frameVertices != expectedVertsPerFrame)
			{
				console.error("Textures - cannot build texture - differing number of vertices for frame " + f + " - name: " + name);
				return;
			}
			
			for (var v = 0; v < frameVertices; v++)
			{
                // Compue co-ordinate into unit vector
                if (v % 2 == 0)
                {
                    vert = frame[v] / src.width;
                }
                else
                {
                    // Texture system starts from 0,0 bottom-left, but we start 0,0 top-left, so subtract value
                    // from 1 to invert y axis
                    vert = frame[v] / src.height;
                }
                
                // Set vertex co-ordinate
				frameData[frameDataOffset++] = vert;
			}
		}
	
		// Create texture from element
		return new Texture(
			src,
			name,
			speed,
			frames.length,
			expectedVertsPerFrame / 2.0,
			frameData
		);
	},
    
    switchFrameY: function(frameData, index1, index2)
    {
        var v = frameData[index1];
        frameData[index1] = frameData[index2];
        frameData[index2] = v;
    },
	
	bindNoTexture: function(gl, shaderProgram)
	{
		this.emptyTexture.bind(gl, shaderProgram);
	},
	
	unbindNoTexture: function(gl, shaderProgram)
	{
		this.emptyTexture.unbind(gl, shaderProgram);
	}
}
