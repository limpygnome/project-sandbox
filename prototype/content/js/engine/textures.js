projectSandbox.textures =
{
	textures: new Map(),
	src: new Map(),

    // Array which tracks which src files have loaded
    srcLoaded: null,
	
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
	
	load: function(texturePathFile)
	{
		// Fetch list of textures and read them in...
		projectSandbox.utils.ajaxJson(texturePathFile, this.loadTextureList, this.ajaxJsonFailure);
	},
	
	loadTextureList: function(json)
	{
		var self = projectSandbox.textures;
        
        // Fetch src files to load
        var src = json["src"];
        
        // Setup var for tracking the src's which have loaded
        self.srcLoaded = new Array(src.length);
        for (var i = 0; i < src.length; i++)
        {
            self.srcLoaded[i] = false;
        }
		
		// Iterate each src file and load that too
		var url;
		for (var i = 0; i < src.length; i++)
		{
            // Fetch url
			url = src[i];
            
            // Make request to load file
			self.loadTextureFileUrl(i, url);
		}
	},
    
    loadTextureFileUrl: function(srcLoadedId, url)
    {
        var self = projectSandbox.textures;
        
        // Use ajax and pass the id and json
        projectSandbox.utils.ajaxJson(
                url,
                function(json)
                {
                    self.loadTextureFile(srcLoadedId, json);
                },
                self.ajaxJsonFailure
            );
    },
	
	loadTextureFile: function(srcLoadedId, json)
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
        
        // Set src file to loaded
        self.srcLoaded[srcLoadedId] = true;
        
        // Check if all src files have loaded
        if (self.isLoaded())
        {
            // Continue rest of game
            projectSandbox.postResources();
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
	
	ajaxJsonFailure: function(ajax, url)
	{
		console.error("Failed to load resource at '" + url + "' - HTTP status code " + ajax.status + ".");
	}
	
}
