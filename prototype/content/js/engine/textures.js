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
			console.log("Textures - added texture '" + texture.name + "' - " + texture.frames + " frames");
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
		// -- frames * 8 floats (4 verts)
		var frameData = new Float32Array(frames.length * 8);
		var frameDataOffset = 0;
		var frame;
		var vert;
        
		for (var f = 0; f < frames.length; f++)
		{
			frame = frames[f];
			for (var v = 0; v < frame.length; v++)
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
		//gl.disableVertexAttribArray(shaderProgram.textureCoordAttribute);
	},
	
	unbindNoTexture: function(gl, shaderProgram)
	{
		this.emptyTexture.unbind(gl, shaderProgram);
		//gl.enableVertexAttribArray(shaderProgram.textureCoordAttribute);
	},
	
	bindNoColour: function(gl, shaderProgram)
	{
		if (!this.flagColourDisabled)
		{
			//gl.disableVertexAttribArray(shaderProgram.vertexColourAttribute);
			gl.vertexAttrib4f(shaderProgram.vertexColourAttribute, 1, 1, 1, 1);
			this.flagColourDisabled = true;
		}
	},
	
	unbindNoColour: function(gl, shaderProgram)
	{
		if (this.flagColourDisabled)
		{
			//gl.vertexAttrib4f(shaderProgram.vertexColourAttribute, 1, 1, 1, 1);
			gl.enableVertexAttribArray(shaderProgram.vertexColourAttribute);
			this.flagColourDisabled = false;
		}
	}
	
}
