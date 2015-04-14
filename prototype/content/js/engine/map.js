projectSandbox.map =
{
	// Indicates if the map is setup
	setup: false,
    
    // The z-level at which to render the map
    renderZ: -1.0,
	
	// The size of each tile (scaled)
	tileSize: 0,
	
	// Scaled tile sizes used for rendering
	scaledTileSize: 0,
	
	// Half of scaledTileSize
	scaledTileSizeHalf: 0,
	
	// The width of the tiles
	width: 0,
	
	// The height of the tiles
	height: 0,
	
	// Each type has {texture, height}
	types: [],
	
	// Each tile is a short indicating the type
	tiles: [],
	
	reset: function()
	{
		this.setup = false;
		
		this.types = [];
		this.tiles = [];
		this.width = 0;
		this.height = 0;
		this.tileSize = 0;
		this.scaledTileSize = 0;
	},
	
	compileTile: function()
	{
		var gl = projectSandbox.gl;
		
		// Setup scaled tile size
		this.scaledTileSize = this.tileSize * projectSandbox.SCALE_FACTOR;
		this.scaledTileSizeHalf = this.scaledTileSize / 2;
		
		// Compile position vertices
		var halfTileSize = this.scaledTileSizeHalf;
		this.verticesPosition =
		[
			-halfTileSize, -halfTileSize,  0.0,
			+halfTileSize, -halfTileSize,  0.0,
			+halfTileSize, +halfTileSize,  0.0,
			-halfTileSize, +halfTileSize,  0.0
		];
		
		this.bufferPosition = gl.createBuffer();
		gl.bindBuffer(gl.ARRAY_BUFFER, this.bufferPosition);
		gl.bufferData(gl.ARRAY_BUFFER, new Float32Array(this.verticesPosition), gl.STATIC_DRAW);
		this.bufferPosition.itemSize = 3;
		this.bufferPosition.numItems = 4;
		
		// Compile vertex index array
		this.verticesIndex = 
		[
			0, 1, 2,
			0, 2, 3
		];
		
		this.bufferIndexes = gl.createBuffer();
		gl.bindBuffer(gl.ELEMENT_ARRAY_BUFFER, this.bufferIndexes);
		gl.bufferData(gl.ELEMENT_ARRAY_BUFFER, new Uint16Array(this.verticesIndex), gl.STATIC_DRAW);
		this.bufferIndexes.itemSize = 1;
		this.bufferIndexes.numItems = 6;
	},
	
	render: function(gl, shaderProgram, modelView, perspective)
	{
		// Check map is setup
		if(!this.setup)
		{
			return;
		}
		
		// Clip to render tiles within view of camera
		var startX = 0;
		var endX = this.width -1;
		var startY = 0;
		var endY = this.height -1;
		
		// Setup buffers for rendering
		// -- Bind tile buffers
		gl.bindBuffer(gl.ARRAY_BUFFER, this.bufferPosition);
		gl.vertexAttribPointer(shaderProgram.vertexPositionAttribute, this.bufferPosition.itemSize, gl.FLOAT, false, 0, 0);
		
		// -- Bind index data
		gl.bindBuffer(gl.ELEMENT_ARRAY_BUFFER, this.bufferIndexes);
		
		// Translate map so bottom left is 0,0
		mat4.translate(modelView, modelView, [this.scaledTileSizeHalf, this.scaledTileSizeHalf, this.renderZ]);
		
		// Render tiles
		var tileTypeId;
		var tileType;
		var texture = null;
		var textureName = null;
		
		for(y = endY; y >= startY; y--) // Y is inverse!
		{
			// Translate to next row
			for(x = startX; x <= endX; x++)
			{
				tileTypeId = this.tiles[y][x];
				tileType = this.types[tileTypeId];
				
				// Rebind if texture is different
				if(tileType[0] != textureName)
				{
					// Bind texture
					texture = tileType[0];
					texture.bind(gl, shaderProgram);
					
					// Update current texture
					textureName = tileType[0];
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
        if (texture != null)
        {
            texture.unbind(gl);
        }
		
		// Undo translation for tiles
		mat4.translate(modelView, modelView, [0, this.height * -this.scaledTileSize, 0]);
		
		// Undo bottom left translation
		mat4.translate(modelView, modelView, [-this.scaledTileSizeHalf, -this.scaledTileSizeHalf, -this.renderZ]);
	}
}
