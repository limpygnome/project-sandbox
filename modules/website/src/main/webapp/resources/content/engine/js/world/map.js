projectSandbox.world.map =
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

        // Set state to setup
        this.isSetup = true;

        // TODO: create hook/event system for this?
        // Rebuild camera limits
        projectSandbox.camera.buildLimits();
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
        if (!this.isSetup)
        {
            return;
        }

        // Fetch frustrum culled region to render
        var clippedIndexes = projectSandbox.frustrum.mapRegionToRender(this.tileSize);

        if (clippedIndexes == null)
        {
            // Scene not ready to render / no frustrum built
            console.warn("Map - frustrum not yet computed");
            return;
        }

        var renderStartX = clippedIndexes[0];
        var renderEndX = clippedIndexes[2];
        var renderStartY = clippedIndexes[1];
        var renderEndY = clippedIndexes[3];
        
        // Translate map so bottom left is 0,0
        mat4.translate(modelView, modelView, [this.scaledTileSizeHalf, this.scaledTileSizeHalf, this.renderZ]);
        
        // Render tiles
        var tileTypeId;
        var tileType;
        
        var lastTexture = null;
        var lastHeight = -1;

        // Translate to start Y
        mat4.translate(modelView, modelView, [0, this.scaledTileSize * ((this.height - 1) - renderEndY), 0]);

        for(y = renderEndY; y >= renderStartY; y--) // Y is inverse!
        {
            // Move to start of x
            mat4.translate(modelView, modelView, [this.scaledTileSize * renderStartX, 0, 0]);

            // Translate to next row
            for(x = renderStartX; x <= renderEndX; x++)
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

                // Set uniform normal matrix
                var normalMatrix = mat4.create();

                mat4.copy(normalMatrix, modelView);
                mat4.invert(normalMatrix, normalMatrix);
                mat4.transpose(normalMatrix, normalMatrix);
                gl.uniformMatrix4fv(shaderProgram.nMatrixUniform, false, normalMatrix);

                // Render tile
                gl.drawElements(gl.TRIANGLES, this.bufferIndexes.numItems, gl.UNSIGNED_SHORT, 0);

                // Translate for next tile
                mat4.translate(modelView, modelView, [this.scaledTileSize, 0, 0]);
            }

            // Undo x translation and move to next row
            mat4.translate(modelView, modelView, [-this.scaledTileSize * (renderEndX + 1), this.scaledTileSize, 0]);
        }
        
        // Undo translation for Y
        // -- We don't subtract 1 from height since the last translation in the loop always shifts on Y by ts
        mat4.translate(modelView, modelView, [0, this.scaledTileSize * -((this.height) - renderStartY), 0]);

        // Undo bottom left translation
        mat4.translate(modelView, modelView, [-this.scaledTileSizeHalf, -this.scaledTileSizeHalf, -this.renderZ]);

        // Unbind texture
        if (lastTexture != null)
        {
            lastTexture.unbind(gl);
        }
    },
    
    bindTile: function(gl, shaderProgram, height)
    {
        // Determine model
        var model = height == 0 ? "2d-rect" : "3d-cube";

        // Build params
        var params =
        {
            model: model,
            width: this.tileSize,
            height: this.tileSize,
            depth: height
        };

        // Fetch buffers
        this.bufferNormals = projectSandbox.bufferCache.fetchNormalsBuffer(params);
        this.bufferPosition = projectSandbox.bufferCache.fetchVertexBuffer(params);
        this.bufferIndexes = projectSandbox.bufferCache.fetchIndexBuffer(params);

        // Bind normals data
        gl.bindBuffer(gl.ARRAY_BUFFER, this.bufferNormals);
        gl.vertexAttribPointer(shaderProgram.normalsAttribute, this.bufferNormals.itemSize, gl.FLOAT, false, 0, 0);

        // Bind vertices
        gl.bindBuffer(gl.ARRAY_BUFFER, this.bufferPosition);
        gl.vertexAttribPointer(shaderProgram.vertexPositionAttribute, this.bufferPosition.itemSize, gl.FLOAT, false, 0, 0);

        // Bind index data
        gl.bindBuffer(gl.ELEMENT_ARRAY_BUFFER, this.bufferIndexes);
    }

}
