projectSandbox.assetLoader =
{
	// Map of URL <> asset content
	assets: new Map(),
	
	// Total asset files to be loaded
	assetListIndexCounter: 0,
	
	// Array of total assets to be loaded for each list
	expectedAssets: [],
	
	loadFromAssetsFile: function(assetListUrl)
	{
		var self = this;
		
		// Increment ID counter
		assetListIndexId = this.assetListIndexCounter++;
		
		// Set initial array to empty
		this.expectedAssets[assetListIndexId] = [];
		
		// Make request to load assets file
		projectSandbox.utils.ajaxJson(
			assetListUrl,
			function(json)
			{
				self.loadFromAssetsFileCallback(assetListUrl, assetListIndexId, json);
			},
			self.ajaxJsonFailure
		);
	},
	
	loadFromAssetsFileCallback: function(assetListUrl, assetListIndexId, json)
	{
		var self = this;
		
		// Fetch list of assets
		var assets = json["assets"];
		
		if (assets == null)
		{
			console.error("Assets loader - malformed assets list - " + assetListUrl);
			return;
		}
		
		// Setup array for assets
		self.expectedAssets[assetListIndexId] = [assets.length];
		
		var url;
		for (var i = 0; i < assets.length; i++)
		{
			url = assets[i];
			
			// Set loaded state to false
			self.expectedAssets[assetListIndexId] = false;
			
			// Load asset, unless it's another asset file
			if (url.endsWith("/list.json"))
			{
				self.loadFromAssetsFile(url);
			}
			else
			{
				self.loadAsset(assetListIndexId, i, url);
			}
		}
	},
	
	loadAsset: function(assetListIndexId, assetId, url)
	{
		var self = this;
		
		// If the extension is .json, load as json
		if (url.endsWith(".json"))
		{
			projectSandbox.utils.ajaxJson(
				url,
				function(data)
				{
					self.loadAssetCallback(assetListIndexId, assetId, url, data);
				},
				self.ajaxJsonFailure
			);
		}
		else
		{
			projectSandbox.utils.ajax(
				url,
				function(data)
				{
					self.loadAssetCallback(assetListIndexId, assetId, url, data);
				},
				self.ajaxJsonFailure
			);
		}
	},
	
	loadAssetCallback: function(assetListIndexId, assetId, url, data)
	{
		var self = this;
		
		// Check if texture file  - if so, push to texture manager to handle
		var isJson = url.endsWith(".json");
		
		if (isJson && data["textures"] != null)
		{
			console.log("Asset loader - loaded texture - " + url);
		}
		else
		{
			self.assets.set(url, data);
			console.log("Asset loader - loaded - " + url);
		}
		
		// Check if all the assets have loaded yet
		if (isLoaded)
		{
			// Invoke postResources method to continue game
		}
	},
	
	isLoaded: function()
	{
		// Check if no asset files loading
		if (totalAssetLists == 0)
		{
			return true;
		}
		
		// Check assets array is 
	},
	
	ajaxJsonFailure: function(ajax, url)
	{
		console.error("Failed to load resource at '" + url + "' - HTTP status code " + ajax.status + ".");
	}
}