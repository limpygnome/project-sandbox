projectSandbox.bufferCache =
{
	// Map of index buffers by model
	indexBuffers: new Map(),

	// Map of vertices by sizes
	vertexBuffers: new Map(),
	
	setup: function()
	{
		// Compile initial index buffers
		this.buildIndexBuffers();
	},

	buildIndexBuffers: function()
	{
	    var gl = projectSandbox.gl;

	    // -- 2D
        var indexBufferIndices2d =
        [
            0, 1, 2,
            0, 2, 3
        ];

        var indexBuffer2dRect = gl.createBuffer();
        gl.bindBuffer(gl.ELEMENT_ARRAY_BUFFER, indexBuffer2dRect);
        gl.bufferData(gl.ELEMENT_ARRAY_BUFFER, new Uint16Array(indexBufferIndices2d), gl.STATIC_DRAW);
        indexBuffer2dRect.itemSize = 1;
        indexBuffer2dRect.numItems = 6;

        this.indexBuffers.set("2d-rect", indexBuffer2dRect);

        // -- 3D
        var indexBufferIndices3d =
        [
            // Top
            0, 1, 2,
            0, 2, 3,

            // North
            4, 5, 6,
            4, 6, 7,

            // East
            8, 9, 10,
            8, 10, 11,

            // South
            12, 13, 14,
            12, 14, 15,

            // West
            16, 17, 18,
            16, 18, 19
        ];

        var indexBuffer3dRect = gl.createBuffer();
        gl.bindBuffer(gl.ELEMENT_ARRAY_BUFFER, indexBuffer3dRect);
        gl.bufferData(gl.ELEMENT_ARRAY_BUFFER, new Uint16Array(indexBufferIndices3d), gl.STATIC_DRAW);
        indexBuffer3dRect.itemSize = 1;
        indexBuffer3dRect.numItems = 30;

        this.indexBuffers.set("3d-cube", indexBuffer3dRect);
	},

	fetchIndexBuffer: function(params)
	{
	    var model = params.model;

	    if (model != null)
	    {
	        return this.indexBuffers.get(model);
	    }
	    else
	    {
	        return this.indexBuffers.get("2d-rect");
	    }
	},

	fetchVertexBuffer: function(params)
	{
	    var model = params.model;

	    switch (model)
	    {
	        case "3d-cube":
	            return this.fetchVertexBuffer3dRect(params);
            default:
                return this.fetchVertexBuffer2dRect(params);
	    }
	},
	
	fetchVertexBuffer3dRect: function(params)
	{
	    var width = params.width;
	    var height = params.height;
	    var depth = params.depth;

		// Note: width is on x, height is on z, height is y, depth is z
		var gl = projectSandbox.gl;
		
		// Round value, to avoid lots of decimally-sized buffers which are similar
		width = Math.round(width);
		height = Math.round(height);
		depth = Math.round(depth);
		
		// Attempt to fetch existing buffer
		var bufferPosition = this.vertexBuffers.get(width + "x" + height + "x" + depth);
		
		// Create and insert new verex buffer for dimensions if it does not exist
		if (bufferPosition == null)
		{
			var halfWidth = width / 2.0;
			var halfHeight = height / 2.0;
			
			var verticesPosition =
			[
				// Top (T - 0 to 3)
				-halfWidth, +halfHeight,  depth,
				+halfWidth, +halfHeight,  depth,
				+halfWidth, -halfHeight,  depth,
				-halfWidth,	-halfHeight,  depth,
				
				// North
				+halfWidth, +halfHeight, depth,
				-halfWidth, +halfHeight, depth,
				-halfWidth, +halfHeight, 0.0,
				+halfWidth, +halfHeight, 0.0,
				
				// East
				+halfWidth, -halfHeight, depth,
				+halfWidth, +halfHeight, depth,
				+halfWidth, +halfHeight, 0.0,
				+halfWidth, -halfHeight, 0.0,
				
				// South
				-halfWidth, -halfHeight, depth,
				+halfWidth, -halfHeight, depth,
				+halfWidth, -halfHeight, 0.0,
				-halfWidth, -halfHeight, 0.0,
				
				// West
				-halfWidth, +halfHeight, depth,
				-halfWidth, -halfHeight, depth,
				-halfWidth, -halfHeight, 0.0,
				-halfWidth, +halfHeight, 0.0
			];
			
			// Create buffer for position vertices
			bufferPosition = gl.createBuffer();
			gl.bindBuffer(gl.ARRAY_BUFFER, bufferPosition);
			gl.bufferData(gl.ARRAY_BUFFER, new Float32Array(verticesPosition), gl.STATIC_DRAW);
			bufferPosition.itemSize = 3;
			bufferPosition.numItems = 30;
			
			// Store buffer
			this.vertexBuffers.set(width + "x" + height + "x" + depth, bufferPosition);
		}


		return bufferPosition;
	},
	
	fetchVertexBuffer2dRect: function(params)
	{
	    var width = params.width;
        var height = params.height;

		var gl = projectSandbox.gl;
		
		// Round value, to avoid lots of decimally-sized buffers which are similar
		width = Math.round(width);
		height = Math.round(height);
		
		// Attempt to fetch existing buffer
		var bufferPosition = this.vertexBuffers.get(width + "x" + height);
		
		// Create and insert new verex buffer for dimensions if it does not exist
		if (bufferPosition == null)
		{
			var halfWidth = width / 2.0;
			var halfHeight = height / 2.0;
			
			var verticesPosition =
			[
				-halfWidth,	-halfHeight,  0.0,
				+halfWidth, -halfHeight,  0.0,
				+halfWidth, +halfHeight,  0.0,
				-halfWidth, +halfHeight,  0.0
			];
			
			// Create buffer for position vertices
			bufferPosition = gl.createBuffer();
			gl.bindBuffer(gl.ARRAY_BUFFER, bufferPosition);
			gl.bufferData(gl.ARRAY_BUFFER, new Float32Array(verticesPosition), gl.STATIC_DRAW);
			bufferPosition.itemSize = 3;
			bufferPosition.numItems = 4;
			
			// Store buffer
			this.vertexBuffers.set(width + "x" + height, bufferPosition);
		}
		
		return bufferPosition;
	}
	
}
