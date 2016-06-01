function TextureSrc(name, url, width, height, texture)
{
    var gl = projectSandbox.rendering.core.getGl();

    // Assign data
    this.name = name;
    this.width = width;
    this.height = height;

    if (url != null)
    {
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
    else if (texture != null)
    {
        this.texture = texture;
    }
}

TextureSrc.prototype.bindData = function(gl)
{
    // Bind to texture
    gl.bindTexture(gl.TEXTURE_2D, this.texture);
    
    // Set the texture data
    gl.texImage2D(gl.TEXTURE_2D, 0, gl.RGBA, gl.RGBA, gl.UNSIGNED_BYTE, this.image);
    
    // Set texture properties - we allow non-power of two images
    gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_MIN_FILTER, gl.LINEAR);
    gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_MAG_FILTER, gl.LINEAR);
    gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_WRAP_S, gl.CLAMP_TO_EDGE);
    gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_WRAP_T, gl.CLAMP_TO_EDGE);

    // Generate mipmap for scaling
    gl.generateMipmap(gl.TEXTURE_2D);
    
    // Unbind texture
    gl.bindTexture(gl.TEXTURE_2D, null);
    
    console.log("Texture src - loaded texture file - '" + this.image.src + "'");
}
