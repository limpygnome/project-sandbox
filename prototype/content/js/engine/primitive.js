function Primitive(width, height, compile)
{
	// Set initial render flag
	this.flagRender = false;
	
	// Set initial texture to null
	this.texture = null;
	
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
	
	// Actual live version of position
	this.x = 0.0;
	this.y = 0.0;
	this.z = 0.0;
	this.rotation = 0;
	
	// Keep a separate copy for rendering to avoid flickering from updates mid-way
	this.renderX = 0.0;
	this.renderY = 0.0;
	this.renderZ = 0.0;
	this.renderRotation = 0.0;
	
	// Set default colours
	this.setColour(1.0, 1.0, 1.0, 1.0);
	
	this.buffer = null;
    this.texture = null;
	
	// Compile vertices to graphics card
	if (compile == undefined || compile == null || compile)
	{
		this.compile();
	}
}

Primitive.prototype.setColour = function(r, g, b, a)
{
	this.r = r;
	this.g = g;
	this.b = b;
	this.a = a;
},

Primitive.prototype.setAlpha = function(a)
{
	if (a < 0.0 || a > 1.0)
	{
		console.warn("Primitive - attempted to set invalid alpha value of " + a);
	}
	
	this.a = a;
},

Primitive.prototype.compile = function()
{
	var gl = projectSandbox.gl;
	
	if (gl != null && this.width > 0 && this.height > 0)
	{
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
		
		// Set render flag
		this.flagRender = true;
	}
	else
	{
		this.flagRender = false;
	}
}

Primitive.prototype.render = function(gl, shaderProgram, modelView, perspective)
{
	// Check we are allowed to render
	if (!this.flagRender)
	{
		return;
	}
	
	// Translate modelview to location of primitive
	mat4.translate(modelView, modelView, [this.renderX, this.renderY, this.renderZ]);
	mat4.rotateZ(modelView, modelView, -this.renderRotation);
	
	// Bind position data for shader program
	gl.bindBuffer(gl.ARRAY_BUFFER, this.bufferPosition);
	gl.vertexAttribPointer(shaderProgram.vertexPositionAttribute, this.bufferPosition.itemSize, gl.FLOAT, false, 0, 0);
	
	// Bind colour data for shader program
	gl.vertexAttrib4f(shaderProgram.vertexColourAttribute, this.r, this.g, this.b, this.a);
	
	// Fetch texture
    var texture = this.texture;
    
    // Bind texture
	if (texture != null)
	{
		texture.bind(gl, shaderProgram);
	}
	else
	{
		projectSandbox.textures.bindNoTexture(gl, shaderProgram);
	}
	
	// Bind index data
	gl.bindBuffer(gl.ELEMENT_ARRAY_BUFFER, this.bufferIndexes);
	
	// Set matrix uniforms
	gl.uniformMatrix4fv(shaderProgram.pMatrixUniform, false, perspective);
	gl.uniformMatrix4fv(shaderProgram.mvMatrixUniform, false, modelView);
	
	// Draw vertex data
	gl.drawElements(gl.TRIANGLES, this.bufferIndexes.numItems, gl.UNSIGNED_SHORT, 0);

	// Undo translation
	mat4.rotateZ(modelView, modelView, this.renderRotation);
	mat4.translate(modelView, modelView, [-this.renderX, -this.renderY, -this.renderZ]);
	
	// Unbind texture
	if (texture != null)
	{
		texture.unbind(gl);
	}
	else
	{
		projectSandbox.textures.unbindNoTexture(gl, shaderProgram);
	}
	
	// Update render co-ordinates
	this.renderX = this.x;
	this.renderY = this.y;
	this.renderZ = this.z;
	this.renderRotation = this.rotation;
};

Primitive.prototype.setTexture = function(name)
{
	if (name == null)
	{
		this.texture = null;
	}
	else
	{
		var texture = projectSandbox.textures.get(name);
		
		if (texture == undefined || texture == null)
		{
			// Fetch error texture
			texture = projectSandbox.textures.get("error");
		}
		
		this.texture = texture;
	}
}
