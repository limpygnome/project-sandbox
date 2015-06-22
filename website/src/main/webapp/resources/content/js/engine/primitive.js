function Primitive(params)
{
    // Check for initial call to constructor - not during object construction
    if (params == null)
    {
        return;
    }

    var width = params.width;
    var height = params.height;

	// Set initial texture to null
	this.texture = null;
	
    // Set size
	this.width = width;
	this.height = height;
	this.radius = width > height ? width : height;

	// Actual live version of position
	this.x = 0.0;
	this.y = 0.0;
	this.z = 0.0;
	this.rotation = 0.0;
	
	// Keep a separate copy for rendering to avoid flickering from updates mid-way
	this.renderX = null;
	this.renderY = null;
	this.renderZ = null;
	this.renderRotation = null;
	
	// Set default colours
	this.setColour(1.0, 1.0, 1.0, 1.0);
	
	this.buffer = null;
    this.texture = null;
		
	// Fetch index buffer
	this.bufferIndexes = projectSandbox.bufferCache.fetchIndexBuffer(params);
	
	// Fetch vertex buffer
	this.bufferPosition = projectSandbox.bufferCache.fetchVertexBuffer(params);

	// Fetch normals buffer
	this.bufferNormals = projectSandbox.bufferCache.fetchNormalsBuffer(params);
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

Primitive.prototype.render = function(gl, shaderProgram, modelView, perspective)
{
	// Check initial render co-ords have been setup
	if (this.renderX == null || this.renderY == null || this.renderZ == null || this.renderRotation == null)
	{
		this.renderX = this.x;
		this.renderY = this.y;
		this.renderZ = this.z;
		this.renderRotation = this.rotation;
	}
	
	// Translate modelview to location of primitive
	mat4.translate(modelView, modelView, [this.renderX, this.renderY, this.renderZ]);
	mat4.rotateZ(modelView, modelView, -this.renderRotation);

	// Bind normals data
    gl.bindBuffer(gl.ARRAY_BUFFER, this.bufferNormals);
    gl.vertexAttribPointer(shaderProgram.normalsAttribute, this.bufferNormals.itemSize, gl.FLOAT, false, 0, 0);

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

	// Set uniform normal matrix
	var normalMatrix = mat4.create();

	mat4.copy(normalMatrix, modelView);
	mat4.invert(normalMatrix, normalMatrix);
	mat4.transpose(normalMatrix, normalMatrix);
	gl.uniformMatrix4fv(shaderProgram.nMatrixUniform, false, normalMatrix);

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

Primitive.prototype.setTextureRaw = function(texture)
{
	this.texture = texture;
}
