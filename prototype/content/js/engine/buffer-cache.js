projectSandbox.bufferCache =
{
	// The index buffer for 2D rectangle polygons, which consists of two triangles
	indexBuffer2dRect: null,
	
	// Map of vertices by sizes
	vertexBuffers: new Map(),
	
	setup: function()
	{
		var gl = projectSandbox.gl;
		
		// Compile initial index buffers
		var indexBufferIndices = 
		[
			0, 1, 2,
			0, 2, 3
		];
		
		var indexBuffer2dRect = gl.createBuffer();
		gl.bindBuffer(gl.ELEMENT_ARRAY_BUFFER, indexBuffer2dRect);
		gl.bufferData(gl.ELEMENT_ARRAY_BUFFER, new Uint16Array(indexBufferIndices), gl.STATIC_DRAW);
		indexBuffer2dRect.itemSize = 1;
		indexBuffer2dRect.numItems = 6;
		
		this.indexBuffer2dRect = indexBuffer2dRect;
	},
	
	fetchIndexBuffer2dRect: function()
	{
		return this.indexBuffer2dRect;
	},
	
	fetchVertexBuffer2dRect: function(width, height)
	{
		var gl = projectSandbox.gl;
		
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
