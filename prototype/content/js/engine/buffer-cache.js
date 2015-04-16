projectSandbox.bufferCache =
{
	// The index buffer for 2D rectangle polygons, which consists of two triangles
	indexBuffer2dRect: null,
	
	// The index buffer for 3D cube polygons, which consists of 2 * 6 triangles
	indexBuffer3dRect: null,
	
	// Map of vertices by sizes
	vertexBuffers: new Map(),
	
	setup: function()
	{
		var gl = projectSandbox.gl;
		
		// Compile initial index buffers
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
		
		this.indexBuffer2dRect = indexBuffer2dRect;
		
		// -- 3D
		var indexBufferIndices3d = 
		[
			// Top
			0, 1, 2,
			0, 2, 3,
			
			// North
			4, 7, 3,
			4, 3, 2,
			
			// East
			5, 4, 2,
			5, 2, 1,
			
			// South
			6, 5, 1,
			6, 1, 0,
			
			// West
			7, 6, 0,
			7, 0, 3
		];
		
		var indexBuffer3dRect = gl.createBuffer();
		gl.bindBuffer(gl.ELEMENT_ARRAY_BUFFER, indexBuffer3dRect);
		gl.bufferData(gl.ELEMENT_ARRAY_BUFFER, new Uint16Array(indexBufferIndices3d), gl.STATIC_DRAW);
		indexBuffer3dRect.itemSize = 1;
		indexBuffer3dRect.numItems = 6;
		
		this.indexBuffer3dRect = indexBuffer3dRect;
	},
	
	fetchIndexBuffer2dRect: function()
	{
		return this.indexBuffer2dRect;
	},
	
	fetchIndexBuffer3dRect: function()
	{
		return this.indexBuffer3dRect;
	},
	
	fetchVertexBuffer3dRect: function(width, height, depth)
	{
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
				-halfWidth,	-halfHeight,  depth,
				+halfWidth, -halfHeight,  depth,
				+halfWidth, +halfHeight,  depth,
				-halfWidth, +halfHeight,  depth,
				
				// NE (4)
				+halfWidth, +halfHeight,  0.0,
				
				// SE (5)
				+halfWidth, -halfHeight,  0.0,
				
				// SW (6)
				-halfWidth,	-halfHeight,  0.0,
				
				// NW (7)
				-halfWidth, +halfHeight,  0.0
			];
			
			// Create buffer for position vertices
			bufferPosition = gl.createBuffer();
			gl.bindBuffer(gl.ARRAY_BUFFER, bufferPosition);
			gl.bufferData(gl.ARRAY_BUFFER, new Float32Array(verticesPosition), gl.STATIC_DRAW);
			bufferPosition.itemSize = 3;
			bufferPosition.numItems = 8;
			
			// Store buffer
			this.vertexBuffers.set(width + "x" + height + "x" + depth, bufferPosition);
		}
		
		return bufferPosition;
	},
	
	fetchVertexBuffer2dRect: function(width, height)
	{
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
