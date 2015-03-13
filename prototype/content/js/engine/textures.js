projectSandbox.textures =
{
	textures: new Map(),
	src: new Map(),
	
	get: function(name)
	{
		return textures.get(name);
	},
	
	getSrc: function(name)
	{
		return src.get(name);
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
		textures.clear();
		src.clear();
	},
	
	load: function(texturePathFile)
	{
		// Fetch list of textures and read them in...
		projectSandbox.utils.ajaxJson(texturePathFile, this.loadTextureList, this.ajaxJsonFailure);
	},
	
	loadTextureList: function(json)
	{
		var self = projectSandbox.textures;
		
		// Iterate each src file and load that too
		var src = json["src"];
		var url;
		for (var i = 0; i < src.length; i++)
		{
			url = src[i];
			projectSandbox.utils.ajaxJson(url, self.loadTextureFile, self.ajaxJsonFailure);
		}
	},
	
	loadTextureFile: function(json)
	{
		var self = projectSandbox.textures;
	
		// Read src values
		var name = json["name"];
		var url = json["url"]
		var width = json["width"];
		var height = json["height"];
		
		// Create src entry
		var src = new TextureSrc(name, url);
		self.src.set(name, src);
		
		// Parse textures
		var textures = json["textures"];
		var texture;
		for (var i = 0; i < textures.length; i++)
		{
			texture = textures[i];
		}
	},
	
	ajaxJsonFailure: function(ajax, url)
	{
		console.error("Failed to load resource at '" + url + "' - HTTP status code " + ajax.status + ".");
	}
	
}
