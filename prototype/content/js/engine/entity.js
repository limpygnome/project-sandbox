function Entity(width, height)
{
    // Set size
	if (width == undefined || width == null)
	{
		this.width = 32;
	}
	else
	{
		this.width = width;
	}
	
	if (height == undefined || height == null)
	{
		this.height = 32;
	}
	else
	{
		this.height = height;
	}
	
	// Setup position vertices
	var halfWidth = this.width / 2;
	var halfHeight = this.height / 2;
    
	this.verticesPosition =
	[
		-halfWidth,	-halfHeight,  0.0,
		+halfWidth, -halfHeight,  0.0,
		+halfWidth, +halfHeight,  0.0,
		-halfWidth, +halfHeight,  0.0
	];
	
	// Setup vertex index array
	this.verticesIndex = 
	[
		0, 1, 2,
		0, 2, 3
	];
	
	// Actual live version of entity's position
	this.x = 0.0;
	this.y = 0.0;
	this.z = 0.0;
	this.rotation = 0;
	
	// Keep a separate copy for rendering to avoid flickering from updates mid-way
	this.renderX = 0.0;
	this.renderY = 0.0;
	this.renderZ = 0.0;
	this.renderRotation = 0.0;
	
	this.buffer = null;
    this.texture = null;
}

Entity.prototype.compile = function(gl)
{
	// Create buffer for position vertices
	this.bufferPosition = gl.createBuffer();
	gl.bindBuffer(gl.ARRAY_BUFFER, this.bufferPosition);
	gl.bufferData(gl.ARRAY_BUFFER, new Float32Array(this.verticesPosition), gl.STATIC_DRAW);
	this.bufferPosition.itemSize = 3;
	this.bufferPosition.numItems = 4;
	
	// Create buffer for index vertices
	this.bufferIndexes = gl.createBuffer();
	gl.bindBuffer(gl.ELEMENT_ARRAY_BUFFER, this.bufferIndexes);
	gl.bufferData(gl.ELEMENT_ARRAY_BUFFER, new Uint16Array(this.verticesIndex), gl.STATIC_DRAW);
	this.bufferIndexes.itemSize = 1;
	this.bufferIndexes.numItems = 6;
};

Entity.prototype.render = function(gl, shaderProgram, modelView, perspective)
{
	// Translate modelview to location of entity
	mat4.translate(modelView, modelView, [this.renderX, this.renderY, this.renderZ]);
	mat4.rotateZ(modelView, modelView, this.renderRotation);
	
	// Bind position data for shader program
	gl.bindBuffer(gl.ARRAY_BUFFER, this.bufferPosition);
	gl.vertexAttribPointer(shaderProgram.vertexPositionAttribute, this.bufferPosition.itemSize, gl.FLOAT, false, 0, 0);
	
	// Check if texture defined
    var texture = this.texture;
	if (texture == undefined || texture == null)
	{
		// Fetch error texture
		texture = projectSandbox.textures.get("error");
	}
    
    // Bind texture
    texture.bind(gl, shaderProgram);
	
	// Bind index data
	gl.bindBuffer(gl.ELEMENT_ARRAY_BUFFER, this.bufferIndexes);
	
	// Set matrix uniforms
	gl.uniformMatrix4fv(shaderProgram.pMatrixUniform, false, perspective);
	gl.uniformMatrix4fv(shaderProgram.mvMatrixUniform, false, modelView);
	
	// Draw vertex data
	gl.drawElements(gl.TRIANGLES, this.bufferIndexes.numItems, gl.UNSIGNED_SHORT, 0);

	// Undo translation
	mat4.rotateZ(modelView, modelView, -this.renderRotation);
	mat4.translate(modelView, modelView, [-this.renderX, -this.renderY, -this.renderZ]);
	
	// Unbind texture
	texture.unbind(gl);
	
	// Update render co-ordinates
	this.renderX = this.x;
	this.renderY = this.y;
	this.renderZ = this.z;
	this.renderRotation = this.rotation;
};
