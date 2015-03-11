function TextureSrc(id, url)
{
	var gl = projectSandbox.gl;

	// Assign data
	this.id = id;
	this.url = url;
	
	// Create new texture
	this.texture = gl.createTexture();
	this.image = new Image();
	
	// Attempt to load image from URL
	var self = this;
	this.image.onload = function()
	{
		self.bindData(gl);
	};
	this.image.onerror = function()
	{
		console.error("Texture src - failed to load texture - " + url);
	};
	this.image.src = url;
}

TextureSrc.prototype.bindData = function(gl)
{
	// Bind to texture
	gl.bindTexture(gl.TEXTURE_2D, this.texture);
	
	// Set the texture data
	gl.texImage2D(gl.TEXTURE_2D, 0, gl.RGBA, gl.RGBA, gl.UNSIGNED_BYTE, this.image);
	
	// Set texture properties - we allow non-power of two images
	gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_MIN_FILTER, gl.NEAREST);
	gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_MAG_FILTER, gl.NEAREST);
	gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_WRAP_S, gl.CLAMP_TO_EDGE);
	gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_WRAP_T, gl.CLAMP_TO_EDGE);
	
	// Generate mipmap for scaling
	gl.generateMipmap(gl.TEXTURE_2D);
	
	// Unbind texture
	gl.bindTexture(gl.TEXTURE_2D, null);
	
	console.log("Texture src - loaded texture - " + this.image.src);
}
