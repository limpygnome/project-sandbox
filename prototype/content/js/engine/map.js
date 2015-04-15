projectSandbox.map =
{
	// Indicates if the map is setup
	isSetup: false,
    
    // The z-level at which to render the map
    renderZ: -1.0,
	
	// The size of each tile (scaled)
	tileSize: 0.0,
	
	// Scaled tile sizes used for rendering
	scaledTileSize: 0.0,
	
	// Half of scaledTileSize
	scaledTileSizeHalf: 0.0,
	
	// The width of the tiles
	width: 0,
	
	// The height of the tiles
	height: 0,
	
	// Each type has {texture, height}
	types: [],
	
	// Each tile is a short indicating the type
	tiles: [],
	
	setup: function()
	{
		// Setup scaled tile size
		this.scaledTileSize = this.tileSize * projectSandbox.SCALE_FACTOR;
		this.scaledTileSizeHalf = this.scaledTileSize / 2;

		this.isSetup = true;
	},
	
	reset: function()
	{
		this.isSetup = false;
		
		this.types = [];
		this.tiles = [];
		this.width = 0;
		this.height = 0;
		this.tileSize = 0.0;
		this.scaledTileSize = 0.0;
		this.scaledTileSizeHalf = 0.0;
	},
	
	render: function(gl, shaderProgram, modelView, perspective)
	{
		// Check map is setup
		if(!this.isSetup)
		{
			return;
		}
		
		// Clip to render tiles within view of camera
		var startX = 0;
		var endX = this.width -1;
		var startY = 0;
		var endY = this.height -1;
		
		// Translate map so bottom left is 0,0
		mat4.translate(modelView, modelView, [this.scaledTileSizeHalf, this.scaledTileSizeHalf, this.renderZ]);
		
		// Render tiles
		var tileTypeId;
		var tileType;
		
		var lastTexture = null;
		var lastHeight = -1;
		
		for(y = endY; y >= startY; y--) // Y is inverse!
		{
			// Translate to next row
			for(x = startX; x <= endX; x++)
			{
				tileTypeId = this.tiles[y][x];
				tileType = this.types[tileTypeId];
				
				// Rebind if texture is different
				if(tileType[0] != lastTexture)
				{
					// Bind texture
					lastTexture = tileType[0];
					lastTexture.bind(gl, shaderProgram);
				}
				
				// Rebind the buffers being used if the tile height is different
				if (tileType[1] != lastHeight)
				{
					lastHeight = tileType[1];
					this.bindTile(gl, shaderProgram, lastHeight);
				}
				
				// -- Set shader matrix uniforms
				gl.uniformMatrix4fv(shaderProgram.pMatrixUniform, false, perspective);
				gl.uniformMatrix4fv(shaderProgram.mvMatrixUniform, false, modelView);
				
				// Render tile
				gl.drawElements(gl.TRIANGLES, this.bufferIndexes.numItems, gl.UNSIGNED_SHORT, 0);
				
				// Translate for next tile
				mat4.translate(modelView, modelView, [this.scaledTileSize, 0, 0]);
			}
			mat4.translate(modelView, modelView, [-this.scaledTileSize * this.width, this.scaledTileSize, 0]);
		}
        
        // Unbind texture
        if (lastTexture != null)
        {
            lastTexture.unbind(gl);
        }
		
		// Undo translation for tiles
		mat4.translate(modelView, modelView, [0, this.height * -this.scaledTileSize, 0]);
		
		// Undo bottom left translation
		mat4.translate(modelView, modelView, [-this.scaledTileSizeHalf, -this.scaledTileSizeHalf, -this.renderZ]);
	},
	
	bindTile: function(gl, shaderProgram, height)
	{
		if (height == 0)
		{
			// Bind 2D buffers
			this.bufferIndexes = projectSandbox.bufferCache.fetchIndexBuffer2dRect();
			this.bufferPosition = projectSandbox.bufferCache.fetchVertexBuffer2dRect(this.tileSize, this.tileSize);
		}
		else
		{
			// Bind 3D buffers
			this.bufferIndexes = projectSandbox.bufferCache.fetchIndexBuffer3dRect();
			this.bufferPosition = projectSandbox.bufferCache.fetchVertexBuffer3dRect(this.tileSize, this.tileSize, height);
		}
		
		// Bind vertices
		gl.bindBuffer(gl.ARRAY_BUFFER, this.bufferPosition);
		gl.vertexAttribPointer(shaderProgram.vertexPositionAttribute, this.bufferPosition.itemSize, gl.FLOAT, false, 0, 0);
		
		// Bind index data
		gl.bindBuffer(gl.ELEMENT_ARRAY_BUFFER, this.bufferIndexes);
	}
}
