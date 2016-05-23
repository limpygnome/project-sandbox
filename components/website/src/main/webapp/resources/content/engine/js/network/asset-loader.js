projectSandbox.assetLoader =
{
    // Map of URL <> asset content
    assets: new Map(),
    
    // Total asset files to be loaded
    assetListIndexCounter: 0,
    
    // Array of total assets to be loaded for each list
    expectedAssets: [],
    
    get: function(key)
    {
        return this.assets.get(key);
    },
    
    loadFromAssetsFile: function(assetListUrl)
    {
        var self = this;
        
        // Increment ID counter
        var assetListIndexId = this.assetListIndexCounter++;
        
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
        
        // Check we have assets array, else it must be an asset
        if (assets == null)
        {
            self.expectedAssets[assetListIndexId][0] = false;
            console.warn("engine/asset-loader - loading list as asset - " + assetListUrl);
            self.loadAssetCallback(assetListIndexId, 0, assetListUrl, json);
            return;
        }
        
        var url;
        for (var i = 0; i < assets.length; i++)
        {
            url = assets[i];
            
            // Setup asset array for item
            self.expectedAssets[assetListIndexId][i] = false;
            
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
            projectSandbox.textures.loadTextureJson(data);
            console.log("Asset loader - loaded texture - " + url);
        }
        else
        {
            self.assets.set(url, data);
            console.log("Asset loader - loaded - '" + url + "'");
        }
        
        // Set asset to loaded
        self.expectedAssets[assetListIndexId][assetId] = true;
        
        // Check if all the assets have loaded yet
        if (this.isLoaded())
        {
            // Invoke postResources method to continue game
            projectSandbox.postResources();
        }
    },
    
    isLoaded: function()
    {
        // Check if all the assets have even loaded
        if (this.assetListIndexCounter != this.expectedAssets.length)
        {
            return false;
        }
        
        // Check if no asset files loading
        if (this.expectedAssets.length == 0)
        {
            return false;
        }
        
        // Check assets array
        var subAssets;
        for (var i = 0; i < this.expectedAssets.length; i++)
        {
            subAssets = this.expectedAssets[i];
            
            // Check it has assets
            if (subAssets == null || subAssets.length == undefined || subAssets.length <= 0)
            {
                return false;
            }
            
            // Check each asset
            for (var j = 0; j < subAssets.length; j++) {
                if (subAssets[j] != true)
                {
                    return false;
                }
            }
        }
        
        return true;
    },
    
    ajaxJsonFailure: function(ajax, url)
    {
        console.error("Failed to load resource at '" + url + "' - HTTP status code " + ajax.status + ".");
    }
}