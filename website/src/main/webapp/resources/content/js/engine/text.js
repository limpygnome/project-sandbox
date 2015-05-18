projectSandbox.text =
{
    canvasText: null,
    canvasTextContext: null,

    setup: function()
    {
        // Setup canvas rendering
        this.canvasText = document.getElementById("ps_render_text");
        this.canvasTextContext = this.canvasText.getContext("2d");
    },

    calculateCanvasSize: function(text, fontSize)
    {
        var textSize = this.canvasTextContext.measureText(text);
        var textWidth = textSize.width;
        var textHeight = fontSize * 2.0;

        // Build result
        var canvasSize = new Array();

        canvasSize[0] = this.calculateClosestPowerOfTwo(textWidth);
        canvasSize[1] = this.calculateClosestPowerOfTwo(textHeight);
        canvasSize[2] = textWidth;
        canvasSize[3] = textHeight;

        return canvasSize;
    },

    calculateClosestPowerOfTwo: function(value)
    {
        var exp = Math.ceil(
            Math.log(value) / Math.log(2)
        );

        return Math.pow(2, exp);
    },

    buildTexture: function()
    {
        var gl = projectSandbox.gl;

        var texture = gl.createTexture();

        gl.pixelStorei(gl.UNPACK_FLIP_Y_WEBGL, true);
        gl.bindTexture(gl.TEXTURE_2D, texture);
        gl.texImage2D(gl.TEXTURE_2D, 0, gl.RGBA, gl.RGBA, gl.UNSIGNED_BYTE, this.canvasText);
        gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_MAG_FILTER, gl.LINEAR);
        gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_MIN_FILTER, gl.LINEAR_MIPMAP_NEAREST);
        gl.generateMipmap(gl.TEXTURE_2D);

        gl.bindTexture(gl.TEXTURE_2D, null);

        return texture;
    },

    buildVertices: function(width, height)
    {
        // we cant use this, NEEDS TO BE VECTOR ARRAY...
        return projectSandbox.bufferCache.fetchVertexBuffer2dRect(width, height);
    },

    buildPrimitive: function(text, fontSize)
    {
        // Setup font
        var font = fontSize + "px serif";
        this.canvasTextContext.font = font;

        // Re-setup canvas
        var canvasSize = this.calculateCanvasSize(text, fontSize);

        this.canvasText.width = canvasSize[0];
        this.canvasText.height = canvasSize[1];
        this.canvasTextContext.font = font;

        // Render text
        this.canvasTextContext.fillText(text, 0.0, fontSize);

        // Create texture / verts
        var textureWidth = canvasSize[2];
        var textureHeight = canvasSize[3];

        var texture = this.buildTexture();
        var textureVertices = this.buildVertices(textureWidth, textureHeight);

        // Create primitive
        var textureSrc = new TextureSrc(texture, textureWidth, textureHeight);
        var primitiveTexture = new Texture(textureSrc, textureVertices);

        var primitive = new Primitive(textureWidth, textureHeight);
        primitive.setTextureRaw(primitiveTexture);

        return primitive;
    }
}
